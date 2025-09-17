package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import java.time.LocalDateTime;
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
import java.util.Map;
import java.math.BigDecimal;

import panacea.website_dat_lich_khach_san.entity.Payment;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.PaymentDTO;
import panacea.website_dat_lich_khach_san.repository.PaymentRepository;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.BookingDTO;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminBookingService;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminPaymentService;

// Controller quản lý thanh toán cho Admin
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
    
    // HIỂN THỊ TRANG QUẢN LÝ THANH TOÁN: Hiển thị danh sách thanh toán, booking có thể thanh toán và booking đã hủy
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
    
    // API LẤY THÔNG TIN THANH TOÁN: Lấy chi tiết thông tin thanh toán theo ID
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


    // API CẬP NHẬT THANH TOÁN: Cập nhật thông tin thanh toán (số tiền, phương thức, trạng thái, ngày thanh toán)
    @PutMapping("/{id}")
    @ResponseBody
    public Object updatePayment(@PathVariable Integer id, @RequestBody Map<String, Object> requestData) {
        try {
            System.out.println("Updating payment with ID: " + id);
            System.out.println("Received request data: " + requestData);
            
            if (requestData == null || requestData.isEmpty()) {
                return java.util.Map.of(
                    "error", true,
                    "message", "Dữ liệu yêu cầu không hợp lệ"
                );
            }
            
            Payment payment = paymentRepository.findById(id).orElse(null);
            if (payment == null) {
                System.out.println("Payment not found with ID: " + id);
                return java.util.Map.of(
                    "error", true,
                    "message", "Không tìm thấy thanh toán với ID: " + id
                );
            }
            
            // Update amount
            Object amountObj = requestData.get("amount");
            if (amountObj != null) {
                BigDecimal amount = new BigDecimal(amountObj.toString());
                payment.setSoTien(amount);
                System.out.println("Updated amount to: " + amount);
            }
            
            // Update payment method
            Object paymentMethodObj = requestData.get("paymentMethod");
            if (paymentMethodObj != null) {
                String paymentMethod = paymentMethodObj.toString();
                payment.setPhuongThuc(paymentMethod);
                System.out.println("Updated payment method to: " + paymentMethod);
            }
            
            // Update payment date
            Object paymentDateObj = requestData.get("paymentDate");
            if (paymentDateObj != null) {
                String paymentDateStr = paymentDateObj.toString();
                try {
                    LocalDateTime paymentDate = LocalDateTime.parse(paymentDateStr);
                    payment.setNgayThanhToan(paymentDate);
                    System.out.println("Updated payment date to: " + paymentDate);
                } catch (Exception e) {
                    System.out.println("Error parsing payment date: " + paymentDateStr);
                }
            }
            
            // Handle status update - map from display value to enum
            Object statusObj = requestData.get("status");
            if (statusObj != null) {
                String statusValue = statusObj.toString();
                System.out.println("Updating status from: " + payment.getTrangThai() + " to: " + statusValue);
                
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
                        // Try to parse as enum value directly
                        try {
                            statusEnum = Payment.TrangThaiPayment.valueOf(statusValue);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Could not parse status: " + statusValue);
                        }
                }
                
                if (statusEnum != null) {
                    payment.setTrangThai(statusEnum);
                    System.out.println("Status updated to: " + statusEnum);
                } else {
                    System.out.println("Could not map status: " + statusValue);
                }
            }
            
            paymentRepository.save(payment);
            System.out.println("Payment saved successfully");
            
            PaymentDTO result = convertToDTO(payment);
            System.out.println("Converted DTO status: " + result.getStatus());
            return result;
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error updating payment: " + e.getMessage());
            return java.util.Map.of(
                "error", true,
                "message", "Lỗi khi cập nhật thanh toán: " + e.getMessage()
            );
        }
    }
    
    // API XÓA THANH TOÁN: Xóa thanh toán theo ID
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

    // API LẤY THÔNG TIN HOÀN TIỀN: Lấy thông tin hoàn tiền cho booking đã hủy (tính phí hủy và số tiền hoàn)
    @GetMapping("/refund-info/{bookingId}")
    @ResponseBody
    public Object getRefundInfo(@PathVariable Integer bookingId) {
        try {
            Booking booking = bookingRepository.findById(bookingId).orElse(null);
            if (booking == null) {
                return java.util.Map.of(
                    "error", true,
                    "message", "Không tìm thấy booking với ID: " + bookingId
                );
            }
            
            // Kiểm tra booking đã hủy chưa
            if (booking.getTrangThaiDatPhong() != Booking.TrangThaiDatPhong.DA_HUY) {
                return java.util.Map.of(
                    "error", true,
                    "message", "Chỉ có thể hoàn tiền cho booking đã hủy"
                );
            }
            
            // Kiểm tra đã hoàn tiền chưa
            if (adminPaymentService.hasRefunded(bookingId)) {
                return java.util.Map.of(
                    "error", true,
                    "message", "Booking này đã được hoàn tiền trước đó"
                );
            }
            
            java.math.BigDecimal totalPaid = adminPaymentService.getTotalPaidAmount(bookingId);
            java.math.BigDecimal calculatedRefund = adminPaymentService.calculateRefundAmount(bookingId);
            
            // Phí hủy = 20% số tiền cọc (tổng đã thanh toán)
            java.math.BigDecimal cancellationFee = totalPaid.multiply(new java.math.BigDecimal("0.20"));
            
            return java.util.Map.of(
                "bookingId", bookingId,
                "maDatPhong", booking.getMaDatPhong(),
                "customerName", booking.getKhachHang() != null ? 
                    (booking.getKhachHang().getHo() + " " + booking.getKhachHang().getTen()).trim() : "Không rõ",
                "totalPaid", totalPaid,
                "cancellationFee", cancellationFee,
                "calculatedRefund", calculatedRefund,
                "cancellationPolicy", booking.getCancellationPolicy() != null ? booking.getCancellationPolicy().name() : "MODERATE",
                "ngayHuy", booking.getNgayHuy(),
                "lyDoHuy", booking.getLyDoHuy()
            );
            
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Map.of(
                "error", true,
                "message", "Lỗi khi lấy thông tin hoàn tiền: " + e.getMessage()
            );
        }
    }
    
    // API TẠO THANH TOÁN HOÀN TIỀN: Tạo payment hoàn tiền cho booking đã hủy với số tiền đã tính toán
    @PostMapping("/refund")
    @ResponseBody
    public Object createRefundPayment(@RequestBody java.util.Map<String, Object> request) {
        try {
            if (request == null || request.isEmpty()) {
                return java.util.Map.of(
                    "error", true,
                    "message", "Dữ liệu yêu cầu không hợp lệ"
                );
            }
            
            Integer bookingId = Integer.valueOf(request.get("bookingId").toString());
            java.math.BigDecimal refundAmount = new java.math.BigDecimal(request.get("refundAmount").toString());
            String reason = request.get("reason") != null ? request.get("reason").toString() : "Hoàn tiền do hủy đặt phòng";
            
            Payment refundPayment = adminPaymentService.createRefundPayment(bookingId, refundAmount, reason);
            return convertToDTO(refundPayment);
            
        } catch (RuntimeException e) {
            e.printStackTrace();
            return java.util.Map.of(
                "error", true,
                "message", e.getMessage()
            );
        } catch (Exception e) {
            e.printStackTrace();
            return java.util.Map.of(
                "error", true,
                "message", "Lỗi khi tạo thanh toán hoàn tiền: " + e.getMessage()
            );
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