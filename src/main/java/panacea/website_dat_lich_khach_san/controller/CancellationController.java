package panacea.website_dat_lich_khach_san.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.CancellationInfoDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.CancellationRequestDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.CancellationResponseDTO;
import panacea.website_dat_lich_khach_san.service.CancellationService;

@RestController
@RequestMapping("/api/cancellation")
@CrossOrigin(origins = "*")
public class CancellationController {

    @Autowired
    private CancellationService cancellationService;

    /**
     * Lấy thông tin hủy đặt phòng
     * GET /api/cancellation/info/{bookingId}
     */
    @GetMapping("/info/{bookingId}")
    public ResponseEntity<?> getCancellationInfo(@PathVariable Long bookingId) {
        try {
            CancellationInfoDTO info = cancellationService.getCancellationInfo(bookingId);
            return ResponseEntity.ok(info);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Thực hiện hủy đặt phòng
     * POST /api/cancellation/cancel
     */
    @PostMapping("/cancel")
    public ResponseEntity<?> cancelBooking(@RequestBody CancellationRequestDTO request) {
        try {
            // Validate request
            if (request.getBookingId() == null) {
                return ResponseEntity.badRequest().body(new ErrorResponse("Booking ID không được để trống"));
            }
            
            CancellationResponseDTO response = cancellationService.cancelBooking(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Hủy đặt phòng với booking ID (phương thức đơn giản)
     * POST /api/cancellation/cancel/{bookingId}
     */
    @PostMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBookingById(
            @PathVariable Long bookingId,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) String customerEmail) {
        try {
            CancellationRequestDTO request = new CancellationRequestDTO();
            request.setBookingId(bookingId);
            request.setCancellationReason(reason != null ? reason : "Khách hàng yêu cầu hủy");
            request.setCustomerEmail(customerEmail);
            
            CancellationResponseDTO response = cancellationService.cancelBooking(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    /**
     * Class để trả về lỗi
     */
    public static class ErrorResponse {
        private boolean success = false;
        private String message;
        private String timestamp;

        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = java.time.LocalDateTime.now().toString();
        }

        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        
        public String getTimestamp() { return timestamp; }
        public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
    }
}