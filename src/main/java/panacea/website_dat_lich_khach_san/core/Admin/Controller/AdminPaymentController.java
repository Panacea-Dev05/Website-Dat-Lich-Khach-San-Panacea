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
import org.springframework.web.bind.annotation.DeleteMapping;

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
            List<Payment> allPayments = paymentRepository.findAll();
            System.out.println("Total payments in database: " + allPayments.size());
            
            List<PaymentDTO> payments = allPayments.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList());
            
            System.out.println("Converted payments: " + payments.size());
            for (PaymentDTO payment : payments) {
                System.out.println("Payment ID: " + payment.getId() + ", Status: " + payment.getStatus());
            }
            
            List<BookingDTO> bookings = adminBookingService.getPayableBookings(); // Chỉ lấy booking có thể thanh toán (loại bỏ booking đã hủy)
            
            // Lấy danh sách booking đã hủy để tạo hoàn tiền
            List<BookingDTO> cancelledBookings = adminBookingService.getCancelledBookings();
            
            List<String> paymentMethods = Arrays.asList("Cash", "Credit Card", "Debit Card", "Bank Transfer", "E-wallet");
            List<String> paymentStatuses = Arrays.asList("DANG_XU_LY", "THANH_CONG", "THAT_BAI", "HOAN_TIEN");
            model.addAttribute("payments", payments);
            model.addAttribute("bookings", bookings);
            model.addAttribute("cancelledBookings", cancelledBookings);
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
    public Object getPayment(@PathVariable Integer id) {
        try {
            System.out.println("Getting payment with ID: " + id);
            
            // First, try to get payment
            Payment payment = paymentRepository.findById(id).orElse(null);
            if (payment == null) {
                System.out.println("Payment not found with ID: " + id);
                return java.util.Map.of(
                    "error", true,
                    "message", "Không tìm thấy thanh toán với ID: " + id
                );
            }
            
            System.out.println("Payment found: " + payment.getId() + ", Status: " + payment.getTrangThai());
            
            // Use convertToDTO for proper mapping
            PaymentDTO dto = convertToDTO(payment);
            System.out.println("Converted DTO status: " + dto.getStatus());
            return dto;
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error getting payment: " + e.getMessage());
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
        try {
            // Kiểm tra booking có hợp lệ không (không bị hủy)
            List<BookingDTO> payableBookings = adminBookingService.getPayableBookings();
            boolean isBookingValid = payableBookings.stream()
                .anyMatch(b -> b.getId().equals(dto.getBookingId()));
            
            if (!isBookingValid) {
                return null; // Booking đã bị hủy hoặc không hợp lệ
            }
            
            Payment payment = adminPaymentService.createPayment(
                dto.getBookingId() != null ? dto.getBookingId().intValue() : null,
                dto.getSoTien() != null ? dto.getSoTien() : dto.getAmount(),
                dto.getHinhThucThanhToan() != null ? dto.getHinhThucThanhToan() : dto.getPaymentMethod(),
                "Thanh toán thêm từ admin"
            );
            if (payment == null) return null;
            
            // Update status if provided
            if (dto.getStatus() != null || dto.getTrangThai() != null) {
                String statusValue = dto.getStatus() != null ? dto.getStatus() : dto.getTrangThai();
                Payment.TrangThaiPayment statusEnum = null;
                
                switch (statusValue) {
                    case "DANG_XU_LY":
                        statusEnum = Payment.TrangThaiPayment.DANG_XU_LY;
                        break;
                    case "THANH_CONG":
                        statusEnum = Payment.TrangThaiPayment.THANH_CONG;
                        break;
                    case "THAT_BAI":
                        statusEnum = Payment.TrangThaiPayment.THAT_BAI;
                        break;
                    case "HOAN_TIEN":
                        statusEnum = Payment.TrangThaiPayment.HOAN_TIEN;
                        break;
                    default:
                        // Try to map from display value
                        if ("Đang xử lý".equals(statusValue)) {
                            statusEnum = Payment.TrangThaiPayment.DANG_XU_LY;
                        } else if ("Thành công".equals(statusValue)) {
                            statusEnum = Payment.TrangThaiPayment.THANH_CONG;
                        } else if ("Thất bại".equals(statusValue)) {
                            statusEnum = Payment.TrangThaiPayment.THAT_BAI;
                        } else if ("Hoàn tiền".equals(statusValue)) {
                            statusEnum = Payment.TrangThaiPayment.HOAN_TIEN;
                        }
                }
                
                if (statusEnum != null) {
                    payment.setTrangThai(statusEnum);
                    paymentRepository.save(payment);
                }
            }
            
            return convertToDTO(payment);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // API: Sửa thanh toán
    @PutMapping("/{id}")
    @ResponseBody
    public PaymentDTO updatePayment(@PathVariable Integer id, @RequestBody PaymentDTO dto) {
        Payment payment = paymentRepository.findById(id).orElse(null);
        if (payment == null) return null;
        if (dto.getSoTien() != null) payment.setSoTien(dto.getSoTien());
        if (dto.getAmount() != null) payment.setSoTien(dto.getAmount());
        if (dto.getHinhThucThanhToan() != null) payment.setPhuongThuc(dto.getHinhThucThanhToan());
        if (dto.getPaymentMethod() != null) payment.setPhuongThuc(dto.getPaymentMethod());
        if (dto.getThoiGianThanhToan() != null) payment.setNgayThanhToan(dto.getThoiGianThanhToan());
        if (dto.getPaymentDate() != null) payment.setNgayThanhToan(dto.getPaymentDate());
        // Handle status update - map from display value to enum
        String statusValue = dto.getTrangThai() != null ? dto.getTrangThai() : dto.getStatus();
        if (statusValue != null) {
            Payment.TrangThaiPayment statusEnum = null;
            switch (statusValue) {
                case "Đang xử lý":
                    statusEnum = Payment.TrangThaiPayment.DANG_XU_LY;
                    break;
                case "Thành công":
                    statusEnum = Payment.TrangThaiPayment.THANH_CONG;
                    break;
                case "Thất bại":
                    statusEnum = Payment.TrangThaiPayment.THAT_BAI;
                    break;
                case "Hoàn tiền":
                    statusEnum = Payment.TrangThaiPayment.HOAN_TIEN;
                    break;
                default:
                    // Try to parse as enum value directly
                    try {
                        statusEnum = Payment.TrangThaiPayment.valueOf(statusValue);
                    } catch (IllegalArgumentException e) {
                        // If not a valid enum value, try fromLabel
                        statusEnum = Payment.TrangThaiPayment.fromLabel(statusValue);
                    }
            }
            if (statusEnum != null) {
                payment.setTrangThai(statusEnum);
            }
        }
        paymentRepository.save(payment);
        return convertToDTO(payment);
    }
    
    // API: Xóa thanh toán
    @DeleteMapping("/{id}")
    @ResponseBody
    public Object deletePayment(@PathVariable Integer id) {
        try {
            System.out.println("Deleting payment with ID: " + id);
            
            Payment payment = paymentRepository.findById(id).orElse(null);
            if (payment == null) {
                System.out.println("Payment not found with ID: " + id);
                return java.util.Map.of(
                    "error", true,
                    "message", "Không tìm thấy thanh toán với ID: " + id
                );
            }
            
            paymentRepository.delete(payment);
            System.out.println("Payment deleted successfully");
            
            return java.util.Map.of(
                "success", true,
                "message", "Xóa thanh toán thành công"
            );
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error deleting payment: " + e.getMessage());
            return java.util.Map.of(
                "error", true,
                "message", "Lỗi khi xóa thanh toán: " + e.getMessage()
            );
        }
    }

    // API: Tạo payment hoàn tiền cho booking đã hủy
    @PostMapping("/refund")
    @ResponseBody
    public PaymentDTO createRefundPayment(@RequestBody java.util.Map<String, Object> request) {
        try {
            Integer bookingId = Integer.valueOf(request.get("bookingId").toString());
            java.math.BigDecimal refundAmount = new java.math.BigDecimal(request.get("refundAmount").toString());
            String reason = request.get("reason") != null ? request.get("reason").toString() : "Hoàn tiền do hủy đặt phòng";
            
            Payment refundPayment = adminPaymentService.createRefundPayment(bookingId, refundAmount, reason);
            if (refundPayment == null) return null;
            
            return convertToDTO(refundPayment);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private PaymentDTO convertToDTO(Payment payment) {
        try {
            PaymentDTO dto = new PaymentDTO();
            dto.setId(payment.getId());
            dto.setBookingId(payment.getBooking() != null && payment.getBooking().getId() != null
                ? payment.getBooking().getId()
                : null);

            // Sửa tên trường để khớp với HTML
            dto.setAmount(payment.getSoTien());                    // Thay vì setSoTien
            dto.setSoTien(payment.getSoTien());                    // Đảm bảo cả hai trường đều có giá trị
            dto.setPaymentMethod(payment.getPhuongThuc());         // Thay vì setHinhThucThanhToan
            dto.setHinhThucThanhToan(payment.getPhuongThuc());     // Đảm bảo cả hai trường đều có giá trị
            dto.setPaymentDate(payment.getNgayThanhToan());        // Thay vì setThoiGianThanhToan
            dto.setThoiGianThanhToan(payment.getNgayThanhToan());  // Đảm bảo cả hai trường đều có giá trị
        
            // Map enum to display value
            String statusDisplayValue = "Đang xử lý"; // Default value
            if (payment.getTrangThai() != null) {
                switch (payment.getTrangThai()) {
                    case DANG_XU_LY:
                        statusDisplayValue = "Đang xử lý";
                        break;
                    case THANH_CONG:
                        statusDisplayValue = "Thành công";
                        break;
                    case THAT_BAI:
                        statusDisplayValue = "Thất bại";
                        break;
                    case HOAN_TIEN:
                        statusDisplayValue = "Hoàn tiền";
                        break;
                    default:
                        // Fallback: try to map from value
                        String enumValue = payment.getTrangThai().getValue();
                        if ("Đang xử lý".equals(enumValue)) {
                            statusDisplayValue = "Đang xử lý";
                        } else if ("Thành công".equals(enumValue)) {
                            statusDisplayValue = "Thành công";
                        } else if ("Thất bại".equals(enumValue)) {
                            statusDisplayValue = "Thất bại";
                        } else if ("Hoàn tiền".equals(enumValue)) {
                            statusDisplayValue = "Hoàn tiền";
                        } else {
                            statusDisplayValue = enumValue; // Use original value as fallback
                        }
                }
            }
            
            dto.setStatus(statusDisplayValue);
            dto.setTrangThai(statusDisplayValue);

            dto.setUuidId(payment.getUuidId());
            dto.setCreatedDate(payment.getCreatedDate());

            try {
                if (payment.getBooking() != null && payment.getBooking().getKhachHang() != null) {
                    String ho = payment.getBooking().getKhachHang().getHo() != null ? payment.getBooking().getKhachHang().getHo() : "";
                    String ten = payment.getBooking().getKhachHang().getTen() != null ? payment.getBooking().getKhachHang().getTen() : "";
                    dto.setCustomerName((ho + " " + ten).trim());
                } else {
                    dto.setCustomerName("Không rõ");
                }
            } catch (Exception e) {
                System.out.println("Error setting customer name: " + e.getMessage());
                dto.setCustomerName("Không rõ");
            }
            return dto;
        } catch (Exception e) {
            System.out.println("Error in convertToDTO: " + e.getMessage());
            e.printStackTrace();
            // Return a minimal DTO with error info
            PaymentDTO errorDto = new PaymentDTO();
            errorDto.setId(payment.getId());
            errorDto.setCustomerName("Lỗi tải dữ liệu");
            errorDto.setStatus("Lỗi");
            return errorDto;
        }
    }
}