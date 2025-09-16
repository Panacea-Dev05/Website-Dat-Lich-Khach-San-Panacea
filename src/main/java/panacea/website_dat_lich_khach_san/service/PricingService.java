package panacea.website_dat_lich_khach_san.service;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class PricingService {
    
    /**
     * Tính tổng giá tiền cho đặt phòng
     * @param unitPrice Giá đơn vị (giá/ngày, giá/giờ, giá/đêm)
     * @param numberOfUnits Số đơn vị (số ngày, số giờ, số đêm)
     * @param numberOfRooms Số phòng
     * @param bookingType Loại đặt phòng ("ngay", "gio", "dem")
     * @return Tổng giá tiền
     */
    public BigDecimal calculateTotalPrice(BigDecimal unitPrice, int numberOfUnits, int numberOfRooms, String bookingType) {
        if (unitPrice == null || numberOfUnits <= 0 || numberOfRooms <= 0) {
            return BigDecimal.ZERO;
        }
        
        // Tính tổng giá = giá đơn vị * số đơn vị * số phòng
        BigDecimal totalPrice = unitPrice
            .multiply(BigDecimal.valueOf(numberOfUnits))
            .multiply(BigDecimal.valueOf(numberOfRooms));
        
        // Áp dụng hệ số điều chỉnh nếu cần (ví dụ: giảm giá cho đặt nhiều phòng)
        if (numberOfRooms >= 3) {
            totalPrice = totalPrice.multiply(new BigDecimal("0.95")); // Giảm 5% cho 3+ phòng
        }
        
        return totalPrice;
    }
    
    /**
     * Tính số ngày giữa hai ngày
     * @param checkInDate Ngày nhận phòng
     * @param checkOutDate Ngày trả phòng
     * @return Số ngày
     */
    public int calculateNumberOfDays(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            return 1; // Mặc định 1 ngày
        }
        
        if (checkOutDate.isBefore(checkInDate) || checkOutDate.isEqual(checkInDate)) {
            return 1; // Tối thiểu 1 ngày
        }
        
        return (int) ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }
    
    /**
     * Tính số giờ giữa hai thời điểm
     * @param checkInDate Ngày nhận phòng
     * @param checkOutDate Ngày trả phòng
     * @return Số giờ
     */
    public int calculateNumberOfHours(LocalDate checkInDate, LocalDate checkOutDate) {
        int days = calculateNumberOfDays(checkInDate, checkOutDate);
        return Math.max(1, days * 24); // Tối thiểu 1 giờ
    }
    
    /**
     * Tính số đêm giữa hai ngày
     * @param checkInDate Ngày nhận phòng
     * @param checkOutDate Ngày trả phòng
     * @return Số đêm
     */
    public int calculateNumberOfNights(LocalDate checkInDate, LocalDate checkOutDate) {
        return calculateNumberOfDays(checkInDate, checkOutDate);
    }
    
    /**
     * Lấy giá đơn vị theo loại đặt phòng
     * @param dailyPrice Giá/ngày
     * @param hourlyPrice Giá/giờ
     * @param overnightPrice Giá/đêm
     * @param bookingType Loại đặt phòng
     * @return Giá đơn vị
     */
    public BigDecimal getUnitPrice(BigDecimal dailyPrice, BigDecimal hourlyPrice, 
                                 BigDecimal overnightPrice, String bookingType) {
        if (bookingType == null) {
            return dailyPrice != null ? dailyPrice : BigDecimal.ZERO;
        }
        
        switch (bookingType.toLowerCase()) {
            case "gio":
                return hourlyPrice != null ? hourlyPrice : BigDecimal.ZERO;
            case "dem":
                return overnightPrice != null ? overnightPrice : BigDecimal.ZERO;
            case "ngay":
            default:
                return dailyPrice != null ? dailyPrice : BigDecimal.ZERO;
        }
    }
    
    /**
     * Tính số đơn vị theo loại đặt phòng
     * @param checkInDate Ngày nhận phòng
     * @param checkOutDate Ngày trả phòng
     * @param bookingType Loại đặt phòng
     * @return Số đơn vị
     */
    public int calculateNumberOfUnits(LocalDate checkInDate, LocalDate checkOutDate, String bookingType) {
        if (bookingType == null) {
            return calculateNumberOfDays(checkInDate, checkOutDate);
        }
        
        switch (bookingType.toLowerCase()) {
            case "gio":
                return calculateNumberOfHours(checkInDate, checkOutDate);
            case "dem":
                return calculateNumberOfNights(checkInDate, checkOutDate);
            case "ngay":
            default:
                return calculateNumberOfDays(checkInDate, checkOutDate);
        }
    }
    
    /**
     * Tính tổng giá tiền hoàn chỉnh
     * @param dailyPrice Giá/ngày
     * @param hourlyPrice Giá/giờ
     * @param overnightPrice Giá/đêm
     * @param checkInDate Ngày nhận phòng
     * @param checkOutDate Ngày trả phòng
     * @param numberOfRooms Số phòng
     * @param bookingType Loại đặt phòng
     * @return Kết quả tính toán chi tiết
     */
    public PricingResult calculatePricing(BigDecimal dailyPrice, BigDecimal hourlyPrice, 
                                       BigDecimal overnightPrice, LocalDate checkInDate, 
                                       LocalDate checkOutDate, int numberOfRooms, String bookingType) {
        
        // Lấy giá đơn vị
        BigDecimal unitPrice = getUnitPrice(dailyPrice, hourlyPrice, overnightPrice, bookingType);
        
        // Tính số đơn vị
        int numberOfUnits = calculateNumberOfUnits(checkInDate, checkOutDate, bookingType);
        
        // Tính tổng giá
        BigDecimal totalPrice = calculateTotalPrice(unitPrice, numberOfUnits, numberOfRooms, bookingType);
        
        return new PricingResult(unitPrice, numberOfUnits, numberOfRooms, totalPrice, bookingType);
    }
    
    /**
     * Class kết quả tính toán giá
     */
    public static class PricingResult {
        private final BigDecimal unitPrice;
        private final int numberOfUnits;
        private final int numberOfRooms;
        private final BigDecimal totalPrice;
        private final String bookingType;
        
        public PricingResult(BigDecimal unitPrice, int numberOfUnits, int numberOfRooms, 
                           BigDecimal totalPrice, String bookingType) {
            this.unitPrice = unitPrice;
            this.numberOfUnits = numberOfUnits;
            this.numberOfRooms = numberOfRooms;
            this.totalPrice = totalPrice;
            this.bookingType = bookingType;
        }
        
        // Getters
        public BigDecimal getUnitPrice() { return unitPrice; }
        public int getNumberOfUnits() { return numberOfUnits; }
        public int getNumberOfRooms() { return numberOfRooms; }
        public BigDecimal getTotalPrice() { return totalPrice; }
        public String getBookingType() { return bookingType; }
        
        @Override
        public String toString() {
            return String.format("PricingResult{unitPrice=%s, numberOfUnits=%d, numberOfRooms=%d, totalPrice=%s, bookingType='%s'}", 
                               unitPrice, numberOfUnits, numberOfRooms, totalPrice, bookingType);
        }
    }
}
