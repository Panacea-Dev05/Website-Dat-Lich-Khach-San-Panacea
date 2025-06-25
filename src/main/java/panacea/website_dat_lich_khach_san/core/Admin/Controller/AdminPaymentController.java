package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.entity.Payment;
import panacea.website_dat_lich_khach_san.repository.PaymentRepository;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.PaymentDTO;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/payments")
public class AdminPaymentController {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @GetMapping
    public String paymentManagement(Model model) {
        List<PaymentDTO> payments = paymentRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        model.addAttribute("payments", payments);
        return "Admin/view/QuanLyThanhToan";
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public PaymentDTO getPayment(@PathVariable Long id) {
        return paymentRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }
    
    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setBookingId(payment.getBooking().getId());
        dto.setSoTien(payment.getSoTien());
        dto.setHinhThucThanhToan(payment.getPhuongThuc());
        dto.setTrangThai(payment.getTrangThai() != null ? payment.getTrangThai().name() : null);
        dto.setThoiGianThanhToan(payment.getNgayThanhToan());
        dto.setUuidId(payment.getUuidId());
        dto.setCreatedDate(payment.getCreatedDate());
        return dto;
    }
} 