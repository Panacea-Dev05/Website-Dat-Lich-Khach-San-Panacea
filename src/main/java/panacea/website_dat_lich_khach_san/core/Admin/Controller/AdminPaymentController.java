package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import panacea.website_dat_lich_khach_san.entity.Payment;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.PaymentDTO;
import panacea.website_dat_lich_khach_san.repository.PaymentRepository;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.BookingDTO;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminBookingService;

@Controller
@RequestMapping("/admin/payments")
public class AdminPaymentController {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private AdminBookingService adminBookingService;
    
    @GetMapping
    public String paymentManagement(Model model) {
        try {
            List<PaymentDTO> payments = paymentRepository.findAll().stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            List<BookingDTO> bookings = adminBookingService.getAllBookings();
            List<String> paymentMethods = Arrays.asList("Cash", "Credit Card", "Debit Card", "Bank Transfer", "E-wallet");
            List<String> paymentStatuses = Arrays.asList("Pending", "Completed", "Failed", "Refunded");
            model.addAttribute("payments", payments);
            model.addAttribute("bookings", bookings);
            model.addAttribute("paymentMethods", paymentMethods);
            model.addAttribute("paymentStatuses", paymentStatuses);
            return "Admin/view/QuanLyThanhToan";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "Lỗi khi tải danh sách thanh toán: " + e.getMessage());
            return "error";
        }
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public PaymentDTO getPayment(@PathVariable Long id) {
        return paymentRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }
    
    @PostMapping("/create")
    @ResponseBody
    public String createPayment(@RequestBody PaymentDTO dto) {
        Booking booking = bookingRepository.findById(dto.getBookingId() != null ? dto.getBookingId().longValue() : null).orElse(null);
        if (booking == null) {
            return "Booking không tồn tại!";
        }
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setSoTien(dto.getSoTien());
        payment.setPhuongThuc(dto.getHinhThucThanhToan());
        payment.setNgayThanhToan(dto.getThoiGianThanhToan());
        payment.setTrangThai(dto.getTrangThai() != null ? Payment.TrangThaiPayment.fromLabel(dto.getTrangThai()) : Payment.TrangThaiPayment.DANG_XU_LY);
        payment.setCreatedDate(System.currentTimeMillis());
        payment.setUuidId(java.util.UUID.randomUUID());
        paymentRepository.save(payment);
        return "OK";
    }

    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId() != null ? payment.getId().longValue() : null);
        dto.setBookingId(payment.getBooking() != null && payment.getBooking().getId() != null
            ? payment.getBooking().getId().longValue()
            : null);

        // Sửa tên trường để khớp với HTML
        dto.setAmount(payment.getSoTien());                    // Thay vì setSoTien
        dto.setPaymentMethod(payment.getPhuongThuc());         // Thay vì setHinhThucThanhToan
        dto.setPaymentDate(payment.getNgayThanhToan());        // Thay vì setThoiGianThanhToan
        dto.setStatus(payment.getTrangThai() != null ?
                payment.getTrangThai().getValue() : null); // Thay vì setTrangThai

        dto.setUuidId(payment.getUuidId());
        dto.setCreatedDate(payment.getCreatedDate());

        if (payment.getBooking() != null && payment.getBooking().getKhachHang() != null) {
            dto.setCustomerName(payment.getBooking().getKhachHang().getHo() + " " +
                    payment.getBooking().getKhachHang().getTen());
        } else {
            dto.setCustomerName("Không rõ");
        }
        return dto;
    }
} 