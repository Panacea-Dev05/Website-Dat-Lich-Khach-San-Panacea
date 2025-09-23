package panacea.website_dat_lich_khach_san.controller;

import panacea.website_dat_lich_khach_san.service.SePayService;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Controller xử lý thanh toán SePay
 * SePay là hệ thống tạo QR code để nhận thanh toán từ các ví điện tử
 */
@RestController
@RequestMapping("/sepay")
public class SePayController {

    @Autowired
    private SePayService sePayService;
    
    @Autowired
    private BookingRepository bookingRepository;

    /**
     * API tạo QR code thanh toán SePay
     * @param amount - Số tiền thanh toán
     * @param orderId - Mã đơn hàng
     * @param description - Mô tả thanh toán
     * @return ResponseEntity - QR code image hoặc error
     */
    @GetMapping("/qr")
    public ResponseEntity<byte[]> generateQRCode(
            @RequestParam BigDecimal amount,
            @RequestParam String orderId,
            @RequestParam(required = false) String description) {
        
        try {
            String desc = description != null ? description : "DatPhong_" + orderId;
            byte[] qrCodeBytes = sePayService.generateSePayQRCode(amount, orderId, desc);
            
            if (qrCodeBytes != null) {
                return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Content-Disposition", "inline; filename=\"sepay-qr.png\"")
                    .body(qrCodeBytes);
            } else {
                return ResponseEntity.badRequest().build();
            }
            
        } catch (Exception e) {
            System.err.println("Error generating SePay QR code: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * API tạo link thanh toán SePay
     * @param amount - Số tiền
     * @param orderId - Mã đơn hàng
     * @return String - Link thanh toán
     */
    @GetMapping("/payment-url")
    public String createPaymentUrl(
            @RequestParam BigDecimal amount,
            @RequestParam String orderId) {
        
        try {
            return sePayService.createSePayPaymentUrl(amount, orderId);
        } catch (Exception e) {
            System.err.println("Error creating SePay payment URL: " + e.getMessage());
            return "#";
        }
    }

    /**
     * URL khách hàng quay lại sau khi thanh toán thành công
     * @param orderId - Mã đơn hàng
     * @param amount - Số tiền đã thanh toán
     * @param status - Trạng thái thanh toán
     * @return String - Trang thông báo kết quả
     */
    @GetMapping("/return")
    public String returnUrl(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) BigDecimal amount,
            @RequestParam(required = false) String status) {
        
        try {
            System.out.println("=== SEPAY RETURN CALLBACK ===");
            System.out.println("Order ID: " + orderId);
            System.out.println("Amount: " + amount);
            System.out.println("Status: " + status);
            System.out.println("=============================");
            
            // TODO: Xử lý cập nhật trạng thái booking
            if (orderId != null && "success".equals(status)) {
                // Tìm booking theo orderId
                Optional<Booking> bookingOpt = bookingRepository.findByMaDatPhong(orderId);
                if (bookingOpt.isPresent()) {
                    Booking booking = bookingOpt.get();
                    // Cập nhật trạng thái thanh toán
                    // booking.setTrangThaiThanhToan(Booking.TrangThaiThanhToan.DA_THANH_TOAN);
                    // bookingRepository.save(booking);
                    
                    System.out.println("Booking updated: " + booking.getId());
                }
            }
            
            // Trả về trang thông báo
            return "<!DOCTYPE html>" +
                   "<html><head><title>SePay Payment Result</title></head>" +
                   "<body style='font-family: Arial, sans-serif; text-align: center; padding: 50px;'>" +
                   "<h1>Kết quả thanh toán SePay</h1>" +
                   "<p>Mã đơn hàng: " + (orderId != null ? orderId : "N/A") + "</p>" +
                   "<p>Số tiền: " + (amount != null ? amount + " VNĐ" : "N/A") + "</p>" +
                   "<p>Trạng thái: " + (status != null ? status : "N/A") + "</p>" +
                   "<p>Cảm ơn bạn đã sử dụng dịch vụ của Panacea Hotel!</p>" +
                   "</body></html>";
                   
        } catch (Exception e) {
            System.err.println("Error processing SePay return: " + e.getMessage());
            return "<!DOCTYPE html>" +
                   "<html><head><title>SePay Payment Error</title></head>" +
                   "<body style='font-family: Arial, sans-serif; text-align: center; padding: 50px;'>" +
                   "<h1>Lỗi xử lý thanh toán</h1>" +
                   "<p>Đã xảy ra lỗi khi xử lý thanh toán. Vui lòng liên hệ với chúng tôi.</p>" +
                   "</body></html>";
        }
    }

    /**
     * URL nhận thông báo từ SePay (webhook)
     * @param requestBody - Dữ liệu callback từ SePay
     * @return ResponseEntity - Kết quả xử lý
     */
    @PostMapping("/notify")
    public ResponseEntity<String> notifyUrl(@RequestBody Map<String, Object> requestBody) {
        try {
            System.out.println("=== SEPAY NOTIFY CALLBACK ===");
            System.out.println("Request Body: " + requestBody);
            System.out.println("=============================");
            
            // TODO: Xử lý webhook từ SePay
            // Kiểm tra chữ ký xác thực
            String signature = (String) requestBody.get("signature");
            // Convert Map<String, Object> to Map<String, String>
            Map<String, String> stringMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : requestBody.entrySet()) {
                stringMap.put(entry.getKey(), entry.getValue().toString());
            }
            
            if (sePayService.verifySePayCallback(signature, stringMap)) {
                // Xử lý thanh toán thành công
                String orderId = (String) requestBody.get("orderId");
                BigDecimal amount = new BigDecimal(requestBody.get("amount").toString());
                
                // Cập nhật trạng thái booking
                Optional<Booking> bookingOpt = bookingRepository.findByMaDatPhong(orderId);
                if (bookingOpt.isPresent()) {
                    Booking booking = bookingOpt.get();
                    // TODO: Cập nhật trạng thái thanh toán
                    System.out.println("Payment confirmed for booking: " + booking.getId() + ", amount: " + amount);
                }
                
                return ResponseEntity.ok("OK");
            } else {
                System.err.println("Invalid SePay callback signature");
                return ResponseEntity.badRequest().body("Invalid signature");
            }
            
        } catch (Exception e) {
            System.err.println("Error processing SePay notify: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error");
        }
    }

    /**
     * API lấy thông tin tài khoản ngân hàng
     * @return Map - Thông tin tài khoản
     */
    @GetMapping("/bank-info")
    public Map<String, String> getBankInfo() {
        return sePayService.getBankAccountInfo();
    }

    /**
     * API test tạo QR code
     * @return ResponseEntity - QR code test
     */
    @GetMapping("/test-qr")
    public ResponseEntity<byte[]> testQRCode() {
        try {
            BigDecimal testAmount = new BigDecimal("100000"); // 100k VNĐ
            String testOrderId = "TEST" + System.currentTimeMillis();
            String testDescription = "Test SePay QR Code";
            
            byte[] qrCodeBytes = sePayService.generateSePayQRCode(testAmount, testOrderId, testDescription);
            
            if (qrCodeBytes != null) {
                return ResponseEntity.ok()
                    .header("Content-Type", "image/png")
                    .header("Content-Disposition", "inline; filename=\"sepay-test-qr.png\"")
                    .body(qrCodeBytes);
            } else {
                return ResponseEntity.badRequest().build();
            }
            
        } catch (Exception e) {
            System.err.println("Error generating test SePay QR code: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * API lấy URL ảnh QR từ SePay API
     * @param amount - Số tiền
     * @param orderId - Mã đơn hàng
     * @param description - Mô tả
     * @return String - URL ảnh QR
     */
    @GetMapping("/qr-url")
    public String getQRImageUrl(
            @RequestParam BigDecimal amount,
            @RequestParam String orderId,
            @RequestParam(required = false) String description) {
        
        try {
            String desc = description != null ? description : "DatPhong_" + orderId;
            String qrImageUrl = sePayService.getSePayQRImageUrl(amount, orderId, desc);
            
            if (qrImageUrl != null) {
                return qrImageUrl;
            } else {
                return "Error generating QR image URL";
            }
            
        } catch (Exception e) {
            System.err.println("Error getting SePay QR image URL: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }
}
