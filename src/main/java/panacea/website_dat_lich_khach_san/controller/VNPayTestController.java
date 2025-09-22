package panacea.website_dat_lich_khach_san.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import panacea.website_dat_lich_khach_san.service.VNPayService;

@RestController
@RequestMapping("/test-vnpay")
public class VNPayTestController {

    @Autowired(required = false)
    private VNPayService vnPayService;

    @GetMapping("/create-link")
    public String createVNPayLink(@RequestParam(defaultValue = "100000") long amount,
                                  @RequestParam(defaultValue = "TEST123") String orderId) {
        if (vnPayService == null) {
            return "VNPayService not available";
        }
        
        try {
            String vnpayUrl = vnPayService.createPaymentUrl(amount, orderId);
            return "VNPAY Demo URL: " + vnpayUrl;
        } catch (Exception e) {
            return "Error creating VNPAY URL: " + e.getMessage();
        }
    }
    
    @GetMapping("/test-email")
    public String testEmailVNPay() {
        if (vnPayService == null) {
            return "VNPayService not available";
        }
        
        try {
            // Test với thông tin giống như trong Gmail
            String orderId = "BOOK1758550147736";
            long amount = 100000; // 100,000 VNĐ
            
            String vnpayUrl = vnPayService.createPaymentUrl(amount, orderId);
            
            return "<html>" +
                "<body style=\"font-family: Arial, sans-serif; text-align: center; padding: 50px;\">" +
                    "<h2>Test VNPAY Demo Link</h2>" +
                    "<p><strong>Order ID:</strong> " + orderId + "</p>" +
                    "<p><strong>Amount:</strong> " + String.format("%,d", amount) + " VNĐ</p>" +
                    "<p><strong>VNPAY Demo URL:</strong></p>" +
                    "<p style=\"word-break: break-all; background-color: #f5f5f5; padding: 10px; border-radius: 5px;\">" + vnpayUrl + "</p>" +
                    "<br>" +
                    "<a href=\"" + vnpayUrl + "\" target=\"_blank\" " +
                    "style=\"display: inline-block; background-color: #ff6b35; color: white; padding: 15px 40px; text-decoration: none; border-radius: 50px; font-weight: bold; font-size: 18px; box-shadow: 0 4px 15px rgba(255, 107, 53, 0.3);\">" +
                    "🚀 Test VNPAY Demo</a>" +
                "</body>" +
                "</html>";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
