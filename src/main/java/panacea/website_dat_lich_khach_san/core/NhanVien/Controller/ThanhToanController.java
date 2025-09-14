package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.ThanhToanService;
import panacea.website_dat_lich_khach_san.entity.Payment;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.PaymentDTO;
import panacea.website_dat_lich_khach_san.entity.Booking;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/nhanvien/thanhtoan")
@CrossOrigin(origins = "*")
public class ThanhToanController {
    
    @Autowired
    private ThanhToanService thanhToanService;
    
    public ThanhToanController(ThanhToanService thanhToanService) {
        this.thanhToanService = thanhToanService;
    }
    
    @GetMapping("")
    public String view(Model model) {
        try {
            model.addAttribute("staffName", thanhToanService.getStaffName());
            
            List<Payment> payments = thanhToanService.getAllPayments();
            model.addAttribute("payments", payments);
            
            List<Booking> bookings = thanhToanService.getActiveBookings();
            model.addAttribute("bookings", bookings);
            
            // Thêm map để tính toán tổng tiền cần thanh toán cho mỗi booking
            Map<Integer, BigDecimal> bookingTotalAmounts = new java.util.HashMap<>();
            for (Booking booking : bookings) {
                BigDecimal totalAmount = thanhToanService.calculateTotalPaymentAmount(booking.getId());
                bookingTotalAmounts.put(booking.getId(), totalAmount);
            }
            model.addAttribute("bookingTotalAmounts", bookingTotalAmounts);
            
            model.addAttribute("phuongThucList", thanhToanService.getPaymentMethods());
            return "NhanVien/ThanhToan";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Có lỗi xảy ra khi tải trang thanh toán");
            return "NhanVien/ThanhToan";
        }
    }
    
    // API: Tạo thanh toán tại quầy (nhân viên chỉ được tạo thanh toán cash)
    @PostMapping("/create")
    @ResponseBody
    public ResponseEntity<?> createPayment(@RequestBody Map<String, Object> request) {
        try {
            System.out.println("Received request: " + request);
            // Validate input data
            if (request.get("bookingId") == null || request.get("bookingId").toString().isEmpty()) {
                Map<String, Object> errorResponse = new java.util.HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "Vui lòng chọn booking");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            if (request.get("soTien") == null || request.get("soTien").toString().isEmpty()) {
                Map<String, Object> errorResponse = new java.util.HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "Vui lòng nhập số tiền");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            // Validate số tiền
            BigDecimal soTien;
            try {
                soTien = new BigDecimal(request.get("soTien").toString());
                if (soTien.compareTo(BigDecimal.ZERO) <= 0) {
                    Map<String, Object> errorResponse = new java.util.HashMap<>();
                    errorResponse.put("error", true);
                    errorResponse.put("message", "Số tiền phải lớn hơn 0");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
                }
            } catch (NumberFormatException e) {
                Map<String, Object> errorResponse = new java.util.HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "Số tiền không hợp lệ");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            if (request.get("phuongThuc") == null || request.get("phuongThuc").toString().isEmpty()) {
                Map<String, Object> errorResponse = new java.util.HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "Vui lòng chọn phương thức thanh toán");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            Integer bookingId = Integer.valueOf(request.get("bookingId").toString());
            // soTien đã được validate ở trên, sử dụng lại biến đã validate
            String phuongThuc = request.get("phuongThuc").toString();
            String noiDung = request.get("noiDung") != null ? request.get("noiDung").toString() : "";
            String maGiaoDich = request.get("maGiaoDich") != null ? request.get("maGiaoDich").toString() : "";
            
            // Kiểm tra booking có tồn tại và trạng thái
            List<Booking> activeBookings = thanhToanService.getActiveBookings();
            boolean isBookingValid = activeBookings.stream()
                .anyMatch(b -> b.getId().equals(bookingId));
            
            if (!isBookingValid) {
                Map<String, Object> errorResponse = new java.util.HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "Booking đã bị hủy hoặc không hợp lệ để thanh toán");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            Payment payment = thanhToanService.createCashPayment(bookingId, soTien, phuongThuc, noiDung, maGiaoDich);
            if (payment == null) {
                Map<String, Object> errorResponse = new java.util.HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "Không thể tạo thanh toán. Booking có thể đã bị hủy.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            // Convert to DTO to avoid circular reference
            PaymentDTO paymentDTO = convertToDTO(payment);
            Map<String, Object> successResponse = new java.util.HashMap<>();
            successResponse.put("success", true);
            successResponse.put("payment", paymentDTO);
            return ResponseEntity.ok(successResponse);
        } catch (NumberFormatException e) {
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Dữ liệu số không hợp lệ");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    // API: Xác nhận thanh toán (nhân viên chỉ được xác nhận thanh toán cash)
    @PutMapping("/confirm/{id}")
    @ResponseBody
    public ResponseEntity<?> confirmPayment(@PathVariable Integer id) {
        try {
            Payment payment = thanhToanService.confirmCashPayment(id);
            if (payment == null) {
                Map<String, Object> errorResponse = new java.util.HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "Không thể xác nhận thanh toán");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            // Convert to DTO to avoid circular reference
            PaymentDTO paymentDTO = convertToDTO(payment);
            Map<String, Object> successResponse = new java.util.HashMap<>();
            successResponse.put("success", true);
            successResponse.put("payment", paymentDTO);
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    // API: In hóa đơn
    @GetMapping("/invoice/{id}")
    @ResponseBody
    public ResponseEntity<?> printInvoice(@PathVariable Integer id) {
        try {
            String invoice = thanhToanService.generateInvoice(id);
            if (invoice == null) {
                Map<String, Object> errorResponse = new java.util.HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "Không thể tạo hóa đơn");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            Map<String, Object> successResponse = new java.util.HashMap<>();
            successResponse.put("success", true);
            successResponse.put("invoice", invoice);
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    // API: Lấy chi tiết thanh toán
    @GetMapping("/detail/{id}")
    @ResponseBody
    public ResponseEntity<?> getPaymentDetail(@PathVariable Integer id) {
        try {
            Payment payment = thanhToanService.getPaymentById(id);
            if (payment == null) {
                Map<String, Object> errorResponse = new java.util.HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "Không tìm thấy thanh toán");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }
            
            // Convert to DTO to avoid circular reference
            PaymentDTO paymentDTO = convertToDTO(payment);
            return ResponseEntity.ok(paymentDTO);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }
    
    @GetMapping("/booking/{bookingId}/status")
    @ResponseBody
    public ResponseEntity<?> getBookingPaymentStatus(@PathVariable Integer bookingId) {
        try {
            Map<String, Object> paymentStatus = thanhToanService.getBookingPaymentStatus(bookingId);
            if (paymentStatus == null) {
                Map<String, Object> errorResponse = new java.util.HashMap<>();
                errorResponse.put("error", true);
                errorResponse.put("message", "Booking không tồn tại");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }
            Map<String, Object> successResponse = new java.util.HashMap<>();
            successResponse.put("success", true);
            successResponse.put("data", paymentStatus);
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new java.util.HashMap<>();
            errorResponse.put("error", true);
            errorResponse.put("message", "Lỗi khi lấy trạng thái thanh toán: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // Helper method to convert Payment entity to PaymentDTO
    private PaymentDTO convertToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
         dto.setSoTien(payment.getSoTien());
         dto.setAmount(payment.getSoTien());
         dto.setHinhThucThanhToan(payment.getPhuongThuc());
         dto.setPaymentMethod(payment.getPhuongThuc());
         dto.setThoiGianThanhToan(payment.getNgayThanhToan());
         dto.setPaymentDate(payment.getNgayThanhToan());
         
         String statusValue = payment.getTrangThai() != null ? payment.getTrangThai().getValue() : null;
         dto.setTrangThai(statusValue);
         dto.setStatus(statusValue);
         
         dto.setUuidId(payment.getUuidId());
         dto.setCreatedDate(payment.getCreatedDate());
         
         if (payment.getBooking() != null) {
            dto.setBookingId(payment.getBooking().getId());
        }
         return dto;
     }
}