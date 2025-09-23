package panacea.website_dat_lich_khach_san.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.entity.RoomPricing;
import panacea.website_dat_lich_khach_san.repository.RoomPricingRepositoty;
import panacea.website_dat_lich_khach_san.repository.BookingDetailRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class OvertimeSurchargeService {

    @Autowired
    private RoomPricingRepositoty roomPricingRepository;

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    // Thời gian checkout chuẩn (mặc định 12:00)
    private static final LocalTime STANDARD_CHECKOUT_TIME = LocalTime.of(12, 0);
    
    // Thời gian miễn phí quá giờ (30 phút)
    private static final int FREE_OVERTIME_MINUTES = 30;

    /**
     * Tính phụ thu quá giờ cho booking
     * @param booking Booking cần tính phụ thu
     * @param actualCheckoutTime Thời gian checkout thực tế
     * @return Thông tin phụ thu quá giờ
     */
    public OvertimeSurchargeResult calculateOvertimeSurcharge(Booking booking, LocalDateTime actualCheckoutTime) {
        OvertimeSurchargeResult result = new OvertimeSurchargeResult();
        
        try {
            // Lấy thời gian checkout chuẩn (ngày trả phòng + 12:00)
            LocalDateTime standardCheckoutTime = booking.getNgayTraPhong().atTime(STANDARD_CHECKOUT_TIME);
            
            // Tính số phút quá giờ
            long overtimeMinutes = ChronoUnit.MINUTES.between(standardCheckoutTime, actualCheckoutTime);
            
            result.setStandardCheckoutTime(standardCheckoutTime);
            result.setActualCheckoutTime(actualCheckoutTime);
            result.setOvertimeMinutes(overtimeMinutes);
            
            // Nếu không quá giờ hoặc quá giờ trong thời gian miễn phí
            if (overtimeMinutes <= FREE_OVERTIME_MINUTES) {
                result.setOvertimeSurcharge(BigDecimal.ZERO);
                result.setHasOvertime(false);
                result.setMessage("Không có phụ thu quá giờ");
                return result;
            }
            
            result.setHasOvertime(true);
            
            // Lấy thông tin phòng từ booking
            List<panacea.website_dat_lich_khach_san.entity.BookingDetail> bookingDetails = 
                bookingDetailRepository.findByDatPhongId(booking.getId());
            
            BigDecimal totalSurcharge = BigDecimal.ZERO;
            StringBuilder surchargeDetails = new StringBuilder();
            
            for (panacea.website_dat_lich_khach_san.entity.BookingDetail detail : bookingDetails) {
                if (detail.getPhongId() != null) {
                    // Lấy giá phụ thu từ RoomPricing
                    RoomPricing roomPricing = roomPricingRepository
                        .findFirstByRoomType_IdAndLoaiGia(booking.getRoomType().getId(), RoomPricing.LoaiGia.BASE);
                    
                    if (roomPricing != null && roomPricing.getGiaPhuThuQuaGio() != null) {
                        BigDecimal hourlySurcharge = roomPricing.getGiaPhuThuQuaGio();
                        
                        // Tính số giờ quá (làm tròn lên)
                        long overtimeHours = (overtimeMinutes + 59) / 60; // Làm tròn lên
                        
                        // Tính phụ thu cho phòng này
                        BigDecimal roomSurcharge = hourlySurcharge.multiply(BigDecimal.valueOf(overtimeHours));
                        totalSurcharge = totalSurcharge.add(roomSurcharge);
                        
                        surchargeDetails.append(String.format("Phòng %d: %s VNĐ/giờ × %d giờ = %s VNĐ\n", 
                            detail.getPhongId(), 
                            formatCurrency(hourlySurcharge), 
                            overtimeHours, 
                            formatCurrency(roomSurcharge)));
                    }
                }
            }
            
            result.setOvertimeSurcharge(totalSurcharge);
            result.setSurchargeDetails(surchargeDetails.toString());
            result.setMessage(String.format("Phụ thu quá giờ: %s VNĐ (%d phút quá giờ)", 
                formatCurrency(totalSurcharge), overtimeMinutes));
            
        } catch (Exception e) {
            result.setHasError(true);
            result.setErrorMessage("Lỗi tính phụ thu quá giờ: " + e.getMessage());
            result.setOvertimeSurcharge(BigDecimal.ZERO);
        }
        
        return result;
    }

    /**
     * Áp dụng phụ thu quá giờ vào booking
     * @param booking Booking cần cập nhật
     * @param actualCheckoutTime Thời gian checkout thực tế
     * @return true nếu thành công
     */
    public boolean applyOvertimeSurcharge(Booking booking, LocalDateTime actualCheckoutTime) {
        try {
            OvertimeSurchargeResult surchargeResult = calculateOvertimeSurcharge(booking, actualCheckoutTime);
            
            if (surchargeResult.isHasOvertime() && surchargeResult.getOvertimeSurcharge().compareTo(BigDecimal.ZERO) > 0) {
                // Cập nhật tổng thanh toán
                BigDecimal currentTotal = booking.getTongThanhToan();
                BigDecimal newTotal = currentTotal.add(surchargeResult.getOvertimeSurcharge());
                booking.setTongThanhToan(newTotal);
                
                // Lưu thông tin phụ thu vào ghi chú
                String currentNote = booking.getGhiChuNoiBo() != null ? booking.getGhiChuNoiBo() : "";
                String surchargeNote = String.format("\n[PHỤ THU QUÁ GIỜ] %s", surchargeResult.getMessage());
                booking.setGhiChuNoiBo(currentNote + surchargeNote);
                
                return true;
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("Lỗi áp dụng phụ thu quá giờ: " + e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra xem có cần tính phụ thu quá giờ không
     * @param booking Booking cần kiểm tra
     * @param actualCheckoutTime Thời gian checkout thực tế
     * @return true nếu cần tính phụ thu
     */
    public boolean needsOvertimeSurcharge(Booking booking, LocalDateTime actualCheckoutTime) {
        LocalDateTime standardCheckoutTime = booking.getNgayTraPhong().atTime(STANDARD_CHECKOUT_TIME);
        long overtimeMinutes = ChronoUnit.MINUTES.between(standardCheckoutTime, actualCheckoutTime);
        return overtimeMinutes > FREE_OVERTIME_MINUTES;
    }

    private String formatCurrency(BigDecimal amount) {
        return String.format("%,.0f", amount);
    }

    /**
     * Class chứa kết quả tính phụ thu quá giờ
     */
    public static class OvertimeSurchargeResult {
        private LocalDateTime standardCheckoutTime;
        private LocalDateTime actualCheckoutTime;
        private long overtimeMinutes;
        private BigDecimal overtimeSurcharge = BigDecimal.ZERO;
        private boolean hasOvertime = false;
        private boolean hasError = false;
        private String message = "";
        private String errorMessage = "";
        private String surchargeDetails = "";

        // Getters and Setters
        public LocalDateTime getStandardCheckoutTime() { return standardCheckoutTime; }
        public void setStandardCheckoutTime(LocalDateTime standardCheckoutTime) { this.standardCheckoutTime = standardCheckoutTime; }

        public LocalDateTime getActualCheckoutTime() { return actualCheckoutTime; }
        public void setActualCheckoutTime(LocalDateTime actualCheckoutTime) { this.actualCheckoutTime = actualCheckoutTime; }

        public long getOvertimeMinutes() { return overtimeMinutes; }
        public void setOvertimeMinutes(long overtimeMinutes) { this.overtimeMinutes = overtimeMinutes; }

        public BigDecimal getOvertimeSurcharge() { return overtimeSurcharge; }
        public void setOvertimeSurcharge(BigDecimal overtimeSurcharge) { this.overtimeSurcharge = overtimeSurcharge; }

        public boolean isHasOvertime() { return hasOvertime; }
        public void setHasOvertime(boolean hasOvertime) { this.hasOvertime = hasOvertime; }

        public boolean isHasError() { return hasError; }
        public void setHasError(boolean hasError) { this.hasError = hasError; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

        public String getSurchargeDetails() { return surchargeDetails; }
        public void setSurchargeDetails(String surchargeDetails) { this.surchargeDetails = surchargeDetails; }
    }
}