package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PutMapping;

import panacea.website_dat_lich_khach_san.entity.Payment;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.PaymentDTO;
import panacea.website_dat_lich_khach_san.repository.PaymentRepository;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.BookingDTO;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminBookingService;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminPaymentService;

@Controller
@RequestMapping("/admin/payments")
public class AdminPaymentController {
    
    // Xử lý lỗi chung cho controller
    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleException(Exception ex) {
        ex.printStackTrace();
        return java.util.Map.of(
            "error", true,
            "message", "Lỗi xử lý: " + ex.getMessage()
        );
    }
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private AdminBookingService adminBookingService;
    
    @Autowired
    private AdminPaymentService adminPaymentService;
    
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
    public Object getPayment(@PathVariable Long id) {
        try {
            Payment payment = paymentRepository.findById(id).orElse(null);
            if (payment == null) {
                return java.util.Map.of(
                    "error", true,
                    "message", "Không tìm thấy thanh toán với ID: " + id
                );
            }
            return convertToDTO(payment);
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Map.of(
                "error", true,
                "message", "Lỗi khi lấy thông tin thanh toán: " + e.getMessage()
            );
        }
    }

    // API: Thêm mới thanh toán
    @PostMapping
    @ResponseBody
    public PaymentDTO createPayment(@RequestBody PaymentDTO dto) {
        Payment payment = adminPaymentService.createPayment(
            dto.getBookingId() != null ? dto.getBookingId().intValue() : null,
            dto.getSoTien() != null ? dto.getSoTien() : dto.getAmount(),
            dto.getHinhThucThanhToan() != null ? dto.getHinhThucThanhToan() : dto.getPaymentMethod(),
            "Thanh toán thêm từ admin"
        );
        if (payment == null) return null;
        return convertToDTO(payment);
    }

    // API: Sửa thanh toán
    @PutMapping("/{id}")
    @ResponseBody
    public PaymentDTO updatePayment(@PathVariable Long id, @RequestBody PaymentDTO dto) {
        Payment payment = paymentRepository.findById(id).orElse(null);
        if (payment == null) return null;
        if (dto.getSoTien() != null) payment.setSoTien(dto.getSoTien());
        if (dto.getAmount() != null) payment.setSoTien(dto.getAmount());
        if (dto.getHinhThucThanhToan() != null) payment.setPhuongThuc(dto.getHinhThucThanhToan());
        if (dto.getPaymentMethod() != null) payment.setPhuongThuc(dto.getPaymentMethod());
        if (dto.getThoiGianThanhToan() != null) payment.setNgayThanhToan(dto.getThoiGianThanhToan());
        if (dto.getPaymentDate() != null) payment.setNgayThanhToan(dto.getPaymentDate());
        if (dto.getTrangThai() != null) payment.setTrangThai(Payment.TrangThaiPayment.fromLabel(dto.getTrangThai()));
        if (dto.getStatus() != null) payment.setTrangThai(Payment.TrangThaiPayment.fromLabel(dto.getStatus()));
        paymentRepository.save(payment);
        return convertToDTO(payment);
    }

    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId() != null ? payment.getId().longValue() : null);
        dto.setBookingId(payment.getBooking() != null && payment.getBooking().getId() != null
            ? payment.getBooking().getId().longValue()
            : null);

        // Sửa tên trường để khớp với HTML
        dto.setAmount(payment.getSoTien());                    // Thay vì setSoTien
        dto.setSoTien(payment.getSoTien());                    // Đảm bảo cả hai trường đều có giá trị
        dto.setPaymentMethod(payment.getPhuongThuc());         // Thay vì setHinhThucThanhToan
        dto.setHinhThucThanhToan(payment.getPhuongThuc());     // Đảm bảo cả hai trường đều có giá trị
        dto.setPaymentDate(payment.getNgayThanhToan());        // Thay vì setThoiGianThanhToan
        dto.setThoiGianThanhToan(payment.getNgayThanhToan());  // Đảm bảo cả hai trường đều có giá trị
        
        String statusValue = payment.getTrangThai() != null ? payment.getTrangThai().getValue() : null;
        dto.setStatus(statusValue); // Thay vì setTrangThai
        dto.setTrangThai(statusValue); // Đảm bảo cả hai trường đều có giá trị

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