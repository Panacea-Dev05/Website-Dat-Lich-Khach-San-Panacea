package panacea.website_dat_lich_khach_san.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.service.EmailService;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.entity.Customer;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/test")
public class EmailTestController {

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private BookingRepository bookingRepository;

    /**
     * Test gửi email thanh toán VNPAY
     */
    @GetMapping("/send-vnpay-email/{bookingId}")
    public String testVNPayEmail(@PathVariable Integer bookingId) {
        try {
            Booking booking = bookingRepository.findById(bookingId).orElse(null);
            if (booking == null) {
                return "Không tìm thấy booking với ID: " + bookingId;
            }
            
            emailService.sendVNPayPaymentEmail(booking);
            return "Đã gửi email thanh toán VNPAY cho booking: " + booking.getMaDatPhong();
            
        } catch (Exception e) {
            return "Lỗi khi gửi email: " + e.getMessage();
        }
    }
    
    /**
     * Test tạo booking mẫu và gửi email
     */
    @GetMapping("/create-test-booking")
    public String createTestBooking() {
        try {
            // Tạo customer mẫu
            Customer customer = new Customer();
            customer.setHo("Nguyễn");
            customer.setTen("Văn A");
            customer.setEmail("test@example.com");
            customer.setSoDienThoai("0123456789");
            
            // Tạo booking mẫu
            Booking booking = new Booking();
            booking.setMaDatPhong("TEST" + System.currentTimeMillis());
            booking.setKhachHang(customer);
            booking.setNgayNhanPhong(LocalDate.now().plusDays(1));
            booking.setNgayTraPhong(LocalDate.now().plusDays(3));
            booking.setSoNguoiLon((byte) 2);
            booking.setSoTreEm((byte) 1);
            booking.setTongThanhToan(new BigDecimal("1500000")); // 1.5 triệu VND
            
            // Gửi email test
            emailService.sendVNPayPaymentEmail(booking);
            
            return "Đã tạo booking test và gửi email VNPAY thành công!";
            
        } catch (Exception e) {
            return "Lỗi khi tạo test booking: " + e.getMessage();
        }
    }
}
