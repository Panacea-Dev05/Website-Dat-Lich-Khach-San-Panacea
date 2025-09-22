package panacea.website_dat_lich_khach_san.controller;

import panacea.website_dat_lich_khach_san.service.VNPayService;
import panacea.website_dat_lich_khach_san.service.EmailService;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.Optional;

@RestController
@RequestMapping("/vnpay")
public class VNPayController {

    @Autowired(required = false)
    private VNPayService vnPayService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private BookingRepository bookingRepository;

    // API tạo link thanh toán
    @GetMapping("/create")
    public String createPayment(@RequestParam long amount) {
        if (vnPayService == null) {
            return "VNPayService not available";
        }
        String orderId = UUID.randomUUID().toString().substring(0, 8);
        return vnPayService.createPaymentUrl(amount, orderId);
    }

    // URL khách hàng quay lại sau khi thanh toán
    @GetMapping("/return")
    public String returnUrl(@RequestParam(required = false) String vnp_ResponseCode,
                           @RequestParam(required = false) String vnp_TxnRef,
                           @RequestParam(required = false) String vnp_Amount) {
        try {
            if ("00".equals(vnp_ResponseCode)) {
                // Tìm booking theo mã đặt phòng
                Optional<Booking> bookingOpt = bookingRepository.findByMaDatPhong(vnp_TxnRef);
                if (bookingOpt.isPresent()) {
                    Booking booking = bookingOpt.get();
                    // Cập nhật trạng thái thanh toán
                    // booking.setTrangThaiThanhToan("DA_THANH_TOAN");
                    // bookingRepository.save(booking);
                    
                    // Gửi email xác nhận thanh toán thành công
                    emailService.sendHtmlEmail(
                        booking.getKhachHang().getEmail(),
                        "Xác nhận thanh toán thành công - " + booking.getMaDatPhong(),
                        createPaymentSuccessEmail(booking)
                    );
                }
                
                return "<html>" +
                    "<body style=\"font-family: Arial, sans-serif; text-align: center; padding: 50px;\">" +
                        "<div style=\"max-width: 500px; margin: 0 auto; padding: 30px; border: 2px solid #28a745; border-radius: 10px; background-color: #f8fff9;\">" +
                            "<h1 style=\"color: #28a745;\">✅ Thanh toán thành công!</h1>" +
                            "<p style=\"font-size: 18px; margin: 20px 0;\">Cảm ơn bạn đã thanh toán. Chúng tôi đã gửi email xác nhận đến bạn.</p>" +
                            "<p style=\"color: #666;\">Mã đặt phòng: <strong>" + vnp_TxnRef + "</strong></p>" +
                            "<p style=\"color: #666;\">Số tiền: <strong>" + String.format("%,.0f", Double.parseDouble(vnp_Amount) / 100) + " VND</strong></p>" +
                            "<a href=\"/\" style=\"display: inline-block; background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-top: 20px;\">Về trang chủ</a>" +
                        "</div>" +
                    "</body>" +
                    "</html>";
            } else {
                return "<html>" +
                    "<body style=\"font-family: Arial, sans-serif; text-align: center; padding: 50px;\">" +
                        "<div style=\"max-width: 500px; margin: 0 auto; padding: 30px; border: 2px solid #dc3545; border-radius: 10px; background-color: #fff5f5;\">" +
                            "<h1 style=\"color: #dc3545;\">❌ Thanh toán thất bại!</h1>" +
                            "<p style=\"font-size: 18px; margin: 20px 0;\">Có lỗi xảy ra trong quá trình thanh toán. Vui lòng thử lại.</p>" +
                            "<p style=\"color: #666;\">Mã lỗi: <strong>" + vnp_ResponseCode + "</strong></p>" +
                            "<a href=\"/\" style=\"display: inline-block; background-color: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-top: 20px;\">Về trang chủ</a>" +
                        "</div>" +
                    "</body>" +
                    "</html>";
            }
        } catch (Exception e) {
            return "Lỗi xử lý thanh toán: " + e.getMessage();
        }
    }

    // IPN (server VNPAY gọi để cập nhật trạng thái)
    @PostMapping("/ipn")
    public String ipn(@RequestParam(required = false) String vnp_ResponseCode,
                     @RequestParam(required = false) String vnp_TxnRef) {
        try {
            if ("00".equals(vnp_ResponseCode)) {
                // Tìm booking và cập nhật trạng thái
                Optional<Booking> bookingOpt = bookingRepository.findByMaDatPhong(vnp_TxnRef);
                if (bookingOpt.isPresent()) {
                    Booking booking = bookingOpt.get();
                    // Cập nhật trạng thái thanh toán trong database
                    // booking.setTrangThaiThanhToan("DA_THANH_TOAN");
                    // bookingRepository.save(booking);
                    
                    System.out.println("IPN: Thanh toán thành công cho booking: " + vnp_TxnRef);
                }
                return "IPN: Thanh toán thành công!";
            }
            return "IPN: Thanh toán thất bại!";
        } catch (Exception e) {
            return "IPN: Lỗi xử lý - " + e.getMessage();
        }
    }
    
    /**
     * Tạo nội dung email xác nhận thanh toán thành công
     */
    private String createPaymentSuccessEmail(Booking booking) {
        return String.format(
            "<html>" +
            "<body style=\"font-family: Arial, sans-serif; line-height: 1.6; color: #333;\">" +
                "<div style=\"max-width: 600px; margin: 0 auto; padding: 20px;\">" +
                    "<h2 style=\"color: #28a745; text-align: center;\">✅ Thanh toán thành công!</h2>" +
                    "<p>Xin chào %s,</p>" +
                    "<p>Chúng tôi xác nhận rằng thanh toán cho đặt phòng <strong>%s</strong> đã được thực hiện thành công.</p>" +
                    
                    "<div style=\"background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0;\">" +
                        "<h3>Thông tin thanh toán:</h3>" +
                        "<p><strong>Mã đặt phòng:</strong> %s</p>" +
                        "<p><strong>Số tiền đã thanh toán:</strong> %,.0f VND</p>" +
                        "<p><strong>Phương thức:</strong> VNPAY</p>" +
                        "<p><strong>Ngày thanh toán:</strong> %s</p>" +
                    "</div>" +
                    
                    "<p>Vui lòng đến khách sạn đúng giờ để làm thủ tục check-in.</p>" +
                    "<p>Trân trọng,<br>Đội ngũ Panacea Hotel</p>" +
                "</div>" +
            "</body>" +
            "</html>", 
            booking.getKhachHang().getHo() + " " + booking.getKhachHang().getTen(),
            booking.getMaDatPhong(),
            booking.getMaDatPhong(),
            booking.getTongThanhToan().doubleValue(),
            java.time.LocalDateTime.now().toString()
        );
    }
}
