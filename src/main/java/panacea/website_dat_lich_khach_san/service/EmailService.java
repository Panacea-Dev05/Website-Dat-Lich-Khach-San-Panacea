package panacea.website_dat_lich_khach_san.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import panacea.website_dat_lich_khach_san.entity.Booking;

@Service
public class EmailService {
    
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Autowired(required = false)
    private VNPayService vnPayService;
    
    /**
     * Gửi email đơn giản với text content
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        if (mailSender == null) {
            System.out.println("JavaMailSender not configured. Email not sent.");
            return;
        }
        
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Lỗi gửi email: " + e.getMessage());
            throw new RuntimeException("Không thể gửi email", e);
        }
    }
    
    /**
     * Gửi email với HTML content
     */
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        if (mailSender == null) {
            System.out.println("JavaMailSender not configured. Email not sent.");
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            System.err.println("Lỗi gửi email HTML: " + e.getMessage());
            throw new RuntimeException("Không thể gửi email HTML", e);
        }
    }
    
    /**
     * Gửi email thanh toán VNPAY cho booking
     */
    public void sendVNPayPaymentEmail(Booking booking) {
        if (mailSender == null) {
            System.out.println("JavaMailSender not configured. Email not sent.");
            return;
        }
        
        if (vnPayService == null) {
            System.out.println("VNPayService not configured. Email not sent.");
            return;
        }
        
        try {
            // Tạo URL thanh toán VNPAY
            String vnpayUrl = vnPayService.createPaymentUrl(
                booking.getTongThanhToan().longValue(), 
                booking.getMaDatPhong()
            );
            
            String subject = "Thanh toán đặt phòng - " + booking.getMaDatPhong();
            String htmlContent = createVNPayEmailContent(booking, vnpayUrl);
            
            sendHtmlEmail(booking.getKhachHang().getEmail(), subject, htmlContent);
            System.out.println("Đã gửi email thanh toán VNPAY cho: " + booking.getKhachHang().getEmail());
            
        } catch (Exception e) {
            System.err.println("Lỗi gửi email thanh toán VNPAY: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Tạo nội dung email thanh toán VNPAY
     */
    private String createVNPayEmailContent(Booking booking, String vnpayUrl) {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; background-color: #f9f9f9;">
                    
                    <!-- Header -->
                    <div style="text-align: center; margin-bottom: 30px; padding: 20px; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); color: white; border-radius: 8px;">
                        <h1 style="margin: 0; font-size: 28px;">🏨 Panacea Hotel</h1>
                        <p style="margin: 10px 0 0 0; font-size: 16px; opacity: 0.9;">Thanh toán đặt phòng</p>
                    </div>
                    
                    <!-- Greeting -->
                    <div style="margin-bottom: 25px;">
                        <h2 style="color: #2c5aa0; margin-bottom: 10px;">Xin chào %s!</h2>
                        <p style="font-size: 16px; margin-bottom: 0;">Cảm ơn bạn đã chọn Panacea Hotel. Vui lòng thanh toán để hoàn tất đặt phòng.</p>
                    </div>
                    
                    <!-- Booking Info -->
                    <div style="background-color: white; padding: 25px; border-radius: 8px; margin-bottom: 25px; box-shadow: 0 2px 4px rgba(0,0,0,0.1);">
                        <h3 style="color: #2c5aa0; margin-top: 0; margin-bottom: 20px; font-size: 20px;">📋 Thông tin đặt phòng</h3>
                        <table style="width: 100%%; border-collapse: collapse;">
                            <tr>
                                <td style="padding: 12px 0; font-weight: bold; width: 40%%; color: #555;">🏷️ Mã đặt phòng:</td>
                                <td style="padding: 12px 0; font-size: 16px; color: #2c5aa0; font-weight: bold;">%s</td>
                            </tr>
                            <tr style="background-color: #f8f9fa;">
                                <td style="padding: 12px 0; font-weight: bold; color: #555;">🏨 Khách sạn:</td>
                                <td style="padding: 12px 0;">Panacea Hotel</td>
                            </tr>
                            <tr>
                                <td style="padding: 12px 0; font-weight: bold; color: #555;">📅 Ngày nhận phòng:</td>
                                <td style="padding: 12px 0;">%s</td>
                            </tr>
                            <tr style="background-color: #f8f9fa;">
                                <td style="padding: 12px 0; font-weight: bold; color: #555;">📅 Ngày trả phòng:</td>
                                <td style="padding: 12px 0;">%s</td>
                            </tr>
                            <tr>
                                <td style="padding: 12px 0; font-weight: bold; color: #555;">👥 Số người lớn:</td>
                                <td style="padding: 12px 0;">%d người</td>
                            </tr>
                            <tr style="background-color: #f8f9fa;">
                                <td style="padding: 12px 0; font-weight: bold; color: #555;">👶 Số trẻ em:</td>
                                <td style="padding: 12px 0;">%d người</td>
                            </tr>
                            <tr style="border-top: 2px solid #2c5aa0;">
                                <td style="padding: 15px 0; font-weight: bold; color: #2c5aa0; font-size: 18px;">💰 Tổng thanh toán:</td>
                                <td style="padding: 15px 0; font-weight: bold; color: #e74c3c; font-size: 20px;">%,.0f VND</td>
                            </tr>
                        </table>
                    </div>
                    
                    <!-- VNPAY Payment Section -->
                    <div style="background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); padding: 30px; border-radius: 8px; text-align: center; margin-bottom: 25px;">
                        <h3 style="color: white; margin-top: 0; margin-bottom: 20px; font-size: 22px;">💳 Thanh toán VNPAY</h3>
                        <p style="color: white; margin-bottom: 25px; font-size: 16px;">Click vào nút bên dưới để thanh toán an toàn và nhanh chóng</p>
                        
                        <a href="%s" style="display: inline-block; background-color: #ff6b35; color: white; padding: 15px 40px; text-decoration: none; border-radius: 50px; font-weight: bold; font-size: 18px; box-shadow: 0 4px 15px rgba(255, 107, 53, 0.3); transition: all 0.3s ease;">
                            🚀 Thanh toán ngay với VNPAY
                        </a>
                        
                        <div style="margin-top: 20px; color: white; font-size: 14px; opacity: 0.9;">
                            <p>✅ Bảo mật cao với mã hóa SSL</p>
                            <p>✅ Hỗ trợ thẻ ATM, Visa, Mastercard</p>
                            <p>✅ Xác nhận thanh toán ngay lập tức</p>
                        </div>
                    </div>
                    
                    <!-- Instructions -->
                    <div style="background-color: #fff3cd; border: 1px solid #ffeaa7; padding: 20px; border-radius: 8px; margin-bottom: 25px;">
                        <h4 style="color: #856404; margin-top: 0; margin-bottom: 15px;">📝 Hướng dẫn thanh toán:</h4>
                        <ol style="color: #856404; margin: 0; padding-left: 20px;">
                            <li>Click vào nút "Thanh toán ngay với VNPAY" ở trên</li>
                            <li>Chọn phương thức thanh toán (ATM, Visa, Mastercard...)</li>
                            <li>Nhập thông tin thẻ và xác nhận</li>
                            <li>Hệ thống sẽ tự động xác nhận và gửi email xác nhận</li>
                        </ol>
                    </div>
                    
                    <!-- Footer -->
                    <div style="text-align: center; padding-top: 20px; border-top: 1px solid #ddd; color: #666;">
                        <p style="margin: 10px 0; font-size: 14px;">Nếu có bất kỳ thắc mắc nào, vui lòng liên hệ:</p>
                        <p style="margin: 5px 0; font-weight: bold; color: #2c5aa0;">📞 Hotline: 1900 1234 | 📧 Email: info@panaceahotel.com</p>
                        <p style="margin: 20px 0 0 0; font-size: 12px; color: #999;">Trân trọng,<br>Đội ngũ Panacea Hotel</p>
                    </div>
                    
                </div>
            </body>
            </html>
            """, 
            booking.getKhachHang().getHo() + " " + booking.getKhachHang().getTen(),
            booking.getMaDatPhong(),
            booking.getNgayNhanPhong(),
            booking.getNgayTraPhong(),
            booking.getSoNguoiLon(),
            booking.getSoTreEm(),
            booking.getTongThanhToan().doubleValue(),
            vnpayUrl
        );
    }
}