package panacea.website_dat_lich_khach_san.controller;

import panacea.website_dat_lich_khach_san.service.SePayQRGenerator;
import panacea.website_dat_lich_khach_san.service.SePayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Controller test SePay integration
 */
@RestController
@RequestMapping("/test/sepay")
public class SePayTestController {

    @Autowired
    private SePayQRGenerator sePayQRGenerator;
    
    @Autowired
    private SePayService sePayService;

    /**
     * Tạo QR code SePay mẫu
     */
    @GetMapping("/generate-sample")
    public String generateSampleQR() {
        try {
            sePayQRGenerator.generateSampleSePayQR();
            return "SePay QR code sample generated successfully!";
        } catch (Exception e) {
            return "Error generating sample QR: " + e.getMessage();
        }
    }

    /**
     * Test tạo QR code với thông tin tùy chỉnh
     */
    @GetMapping("/generate-custom")
    public String generateCustomQR(
            @RequestParam(defaultValue = "custom-sepay-qr.png") String fileName,
            @RequestParam(defaultValue = "500000") BigDecimal amount,
            @RequestParam(defaultValue = "TEST123") String orderId,
            @RequestParam(defaultValue = "DatPhong_TEST123") String description) {
        
        try {
            sePayQRGenerator.generateCustomSePayQR(fileName, amount, orderId, description);
            return "Custom SePay QR code generated: " + fileName;
        } catch (Exception e) {
            return "Error generating custom QR: " + e.getMessage();
        }
    }

    /**
     * Test tạo link thanh toán SePay
     */
    @GetMapping("/test-payment-url")
    public String testPaymentUrl(
            @RequestParam(defaultValue = "100000") BigDecimal amount,
            @RequestParam(defaultValue = "TEST456") String orderId) {
        
        try {
            String paymentUrl = sePayService.createSePayPaymentUrl(amount, orderId);
            return "SePay Payment URL: " + paymentUrl;
        } catch (Exception e) {
            return "Error creating payment URL: " + e.getMessage();
        }
    }

    /**
     * Lấy thông tin tài khoản ngân hàng
     */
    @GetMapping("/bank-info")
    public Object getBankInfo() {
        return sePayService.getBankAccountInfo();
    }

    /**
     * Test lấy URL ảnh QR từ SePay API
     */
    @GetMapping("/test-qr-url")
    public String testQRImageUrl(
            @RequestParam(defaultValue = "100000") BigDecimal amount,
            @RequestParam(defaultValue = "TEST123") String orderId,
            @RequestParam(defaultValue = "DatPhong_TEST123") String description) {
        
        try {
            String qrImageUrl = sePayService.getSePayQRImageUrl(amount, orderId, description);
            return "SePay QR Image URL: " + qrImageUrl;
        } catch (Exception e) {
            return "Error getting QR image URL: " + e.getMessage();
        }
    }

    /**
     * Test QR code với tiền cọc (50% tổng tiền)
     */
    @GetMapping("/test-deposit-qr")
    public String testDepositQR(
            @RequestParam(defaultValue = "200000") BigDecimal totalAmount,
            @RequestParam(defaultValue = "BOOK123") String orderId) {
        
        try {
            // Tính tiền cọc = 50% tổng tiền
            BigDecimal depositAmount = totalAmount.divide(new BigDecimal("2"), 0, java.math.RoundingMode.HALF_UP);
            String description = "DatPhong_" + orderId;
            
            String qrImageUrl = sePayService.getSePayQRImageUrl(depositAmount, orderId, description);
            
            return String.format(
                "=== TEST DEPOSIT QR ===\n" +
                "Total Amount: %,.0f VNĐ\n" +
                "Deposit Amount (50%%): %,.0f VNĐ\n" +
                "Order ID: %s\n" +
                "Description: %s\n" +
                "QR Image URL: %s\n" +
                "========================",
                totalAmount, depositAmount, orderId, description, qrImageUrl
            );
        } catch (Exception e) {
            return "Error testing deposit QR: " + e.getMessage();
        }
    }
}
