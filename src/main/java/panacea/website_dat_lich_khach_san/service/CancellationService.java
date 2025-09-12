package panacea.website_dat_lich_khach_san.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.entity.BookingDetail;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.entity.CancellationPolicy;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.repository.BookingDetailRepository;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.CancellationInfoDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.CancellationRequestDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.CancellationResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Transactional
public class CancellationService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Lấy thông tin hủy đặt phòng
     */
    public CancellationInfoDTO getCancellationInfo(Integer bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đặt phòng với ID: " + bookingId);
        }

        Booking booking = bookingOpt.get();
        
        // Kiểm tra trạng thái có thể hủy
        if (!canCancelBooking(booking)) {
            throw new RuntimeException("Đặt phòng này không thể hủy");
        }

        // Tính toán thông tin hủy
        CancellationCalculation calculation = calculateCancellation(booking);
        
        CancellationInfoDTO info = new CancellationInfoDTO();
        info.setBookingId(booking.getId());
        info.setMaDatPhong(booking.getMaDatPhong());
        info.setCancellationPolicy(booking.getCancellationPolicy());
        info.setPolicyDescription(booking.getCancellationPolicy().getDescription());
        info.setOriginalAmount(booking.getTongThanhToan());
        info.setCancellationFee(calculation.getCancellationFee());
        info.setRefundAmount(calculation.getRefundAmount());
        info.setHoursUntilCheckIn(calculation.getHoursUntilCheckIn());
        info.setCanCancel(calculation.isCanCancel());
        info.setCancellationReason(calculation.getCancellationReason());
        
        return info;
    }

    /**
     * Thực hiện hủy đặt phòng
     */
    public CancellationResponseDTO cancelBooking(CancellationRequestDTO request) {
        Optional<Booking> bookingOpt = bookingRepository.findById(request.getBookingId());
        if (bookingOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy đặt phòng với ID: " + request.getBookingId());
        }

        Booking booking = bookingOpt.get();
        
        // Kiểm tra quyền hủy (nếu có email)
        if (request.getCustomerEmail() != null && !request.getCustomerEmail().trim().isEmpty() && 
            !request.getCustomerEmail().equals(booking.getKhachHang().getEmail())) {
            throw new RuntimeException("Bạn không có quyền hủy đặt phòng này");
        }

        // Kiểm tra trạng thái có thể hủy
        if (!canCancelBooking(booking)) {
            throw new RuntimeException("Đặt phòng này không thể hủy");
        }

        // Tính toán thông tin hủy
        CancellationCalculation calculation = calculateCancellation(booking);
        
        if (!calculation.isCanCancel()) {
            throw new RuntimeException(calculation.getCancellationReason());
        }

        // Cập nhật thông tin booking
        booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_HUY);
        booking.setNgayHuy(LocalDateTime.now());
        booking.setLyDoHuy(request.getCancellationReason());
        booking.setCancellationFee(calculation.getCancellationFee());
        booking.setRefundAmount(calculation.getRefundAmount());
        
        bookingRepository.save(booking);

        // Giải phóng phòng - cập nhật trạng thái phòng về SAN_SANG
        java.util.List<BookingDetail> details = bookingDetailRepository.findByDatPhongId(booking.getId());
        for (BookingDetail detail : details) {
            if (detail.getPhongId() != null) {
                Room room = roomRepository.findById(detail.getPhongId()).orElse(null);
                if (room != null) {
                    room.setTrangThai(Room.TrangThaiPhong.SAN_SANG); // Trả về trạng thái sẵn sàng
                    roomRepository.save(room);
                }
            }
        }

        // Gửi email thông báo
        try {
            sendCancellationEmail(booking, calculation);
        } catch (Exception e) {
            // Log lỗi nhưng không throw exception để không ảnh hưởng đến việc hủy
            System.err.println("Lỗi gửi email hủy đặt phòng: " + e.getMessage());
        }

        // Tạo response
        CancellationResponseDTO response = new CancellationResponseDTO();
        response.setSuccess(true);
        response.setMessage("Hủy đặt phòng thành công");
        response.setBookingId(booking.getId());
        response.setMaDatPhong(booking.getMaDatPhong());
        response.setCancellationFee(calculation.getCancellationFee());
        response.setRefundAmount(calculation.getRefundAmount());
        response.setCancellationDate(LocalDateTime.now());
        
        return response;
    }

    /**
     * Kiểm tra xem booking có thể hủy không
     */
    private boolean canCancelBooking(Booking booking) {
        // Chỉ có thể hủy các booking ở trạng thái chờ xác nhận hoặc đã xác nhận
        // Không thể hủy khi đã nhận phòng, đã hoàn thành hoặc đã hủy
        return booking.getTrangThaiDatPhong() == Booking.TrangThaiDatPhong.CHO_XAC_NHAN ||
               booking.getTrangThaiDatPhong() == Booking.TrangThaiDatPhong.DA_XAC_NHAN;
    }

    /**
     * Tính toán thông tin hủy đặt phòng
     */
    private CancellationCalculation calculateCancellation(Booking booking) {
        CancellationCalculation calculation = new CancellationCalculation();
        
        // Tính số giờ từ hiện tại đến check-in
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime checkInDateTime = booking.getNgayNhanPhong().atTime(14, 0); // Giả sử check-in lúc 14:00
        long hoursUntilCheckIn = ChronoUnit.HOURS.between(now, checkInDateTime);
        
        calculation.setHoursUntilCheckIn(hoursUntilCheckIn);
        
        CancellationPolicy policy = booking.getCancellationPolicy();
        BigDecimal originalAmount = booking.getTongThanhToan();
        
        // Tính toán theo chính sách
        BigDecimal refundAmount = policy.calculateRefund(originalAmount, checkInDateTime, now);
        BigDecimal cancellationFee = originalAmount.subtract(refundAmount);
        
        calculation.setRefundAmount(refundAmount);
        calculation.setCancellationFee(cancellationFee);
        
        // Kiểm tra xem có thể hủy không
        // Chỉ kiểm tra thời gian nếu booking chưa được nhận phòng
        if (booking.getTrangThaiDatPhong() == Booking.TrangThaiDatPhong.DA_NHAN_PHONG ||
            booking.getTrangThaiDatPhong() == Booking.TrangThaiDatPhong.DA_HOAN_THANH ||
            booking.getTrangThaiDatPhong() == Booking.TrangThaiDatPhong.DA_HUY) {
            calculation.setCanCancel(false);
            calculation.setCancellationReason("Không thể hủy ở trạng thái hiện tại");
        } else if (policy == CancellationPolicy.NON_REFUNDABLE && hoursUntilCheckIn < 24) {
            calculation.setCanCancel(false);
            calculation.setCancellationReason("Chính sách không hoàn tiền - không thể hủy trong vòng 24h");
        } else {
            calculation.setCanCancel(true);
            calculation.setCancellationReason("Có thể hủy");
        }
        
        return calculation;
    }

    /**
     * Gửi email thông báo hủy đặt phòng
     */
    private void sendCancellationEmail(Booking booking, CancellationCalculation calculation) {
        String customerEmail = booking.getKhachHang().getEmail();
        String customerName = booking.getKhachHang().getHo() + " " + booking.getKhachHang().getTen();
        
        String subject = "[Panacea Hotel] Xác nhận hủy đặt phòng - " + booking.getMaDatPhong();
        
        String emailBody = String.format(
            "Kính chào %s,\n\n" +
            "Chúng tôi xác nhận rằng đặt phòng của bạn đã được hủy thành công.\n\n" +
            "Thông tin hủy đặt phòng:\n" +
            "- Mã đặt phòng: %s\n" +
            "- Ngày nhận phòng: %s\n" +
            "- Ngày trả phòng: %s\n" +
            "- Tổng tiền gốc: %,.0f VND\n" +
            "- Phí hủy: %,.0f VND\n" +
            "- Số tiền hoàn lại: %,.0f VND\n" +
            "- Chính sách hủy: %s\n" +
            "- Thời gian hủy: %s\n\n" +
            "Số tiền hoàn lại sẽ được xử lý trong vòng 3-5 ngày làm việc.\n\n" +
            "Cảm ơn bạn đã sử dụng dịch vụ của Panacea Hotel.\n\n" +
            "Trân trọng,\n" +
            "Đội ngũ Panacea Hotel",
            customerName,
            booking.getMaDatPhong(),
            booking.getNgayNhanPhong(),
            booking.getNgayTraPhong(),
            booking.getTongThanhToan().doubleValue(),
            calculation.getCancellationFee().doubleValue(),
            calculation.getRefundAmount().doubleValue(),
            booking.getCancellationPolicy().getLabel(),
            LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        );
        
        emailService.sendSimpleEmail(customerEmail, subject, emailBody);
    }

    /**
     * Class để lưu trữ kết quả tính toán hủy đặt phòng
     */
    private static class CancellationCalculation {
        private BigDecimal refundAmount;
        private BigDecimal cancellationFee;
        private long hoursUntilCheckIn;
        private boolean canCancel;
        private String cancellationReason;

        // Getters and setters
        public BigDecimal getRefundAmount() { return refundAmount; }
        public void setRefundAmount(BigDecimal refundAmount) { this.refundAmount = refundAmount; }
        
        public BigDecimal getCancellationFee() { return cancellationFee; }
        public void setCancellationFee(BigDecimal cancellationFee) { this.cancellationFee = cancellationFee; }
        
        public long getHoursUntilCheckIn() { return hoursUntilCheckIn; }
        public void setHoursUntilCheckIn(long hoursUntilCheckIn) { this.hoursUntilCheckIn = hoursUntilCheckIn; }
        
        public boolean isCanCancel() { return canCancel; }
        public void setCanCancel(boolean canCancel) { this.canCancel = canCancel; }
        
        public String getCancellationReason() { return cancellationReason; }
        public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
    }
}