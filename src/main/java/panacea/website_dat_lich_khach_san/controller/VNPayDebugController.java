package panacea.website_dat_lich_khach_san.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.service.VNPayService;
import panacea.website_dat_lich_khach_san.config.VNPayConfig;

@RestController
@RequestMapping("/debug")
public class VNPayDebugController {

    @Autowired
    private VNPayService vnPayService;
    
    @Autowired
    private VNPayConfig vnPayConfig;

    /**
     * Test tạo URL thanh toán VNPAY với debug info
     */
    @GetMapping("/vnpay-test")
    public String testVNPay(@RequestParam(defaultValue = "100000") long amount,
                           @RequestParam(defaultValue = "TEST123") String orderId) {
        try {
            System.out.println("=== VNPAY TEST DEBUG ===");
            System.out.println("Amount: " + amount);
            System.out.println("OrderId: " + orderId);
            System.out.println("TmnCode: " + vnPayConfig.getTmnCode());
            System.out.println("HashSecret: " + vnPayConfig.getHashSecret());
            System.out.println("ReturnUrl: " + vnPayConfig.getReturnUrl());
            System.out.println("IpnUrl: " + vnPayConfig.getIpnUrl());
            System.out.println("========================");
            
            String paymentUrl = vnPayService.createPaymentUrl(amount, orderId);
            
            return String.format("""
                <html>
                <body style="font-family: Arial, sans-serif; padding: 20px;">
                    <h2>VNPAY Debug Test</h2>
                    <p><strong>Amount:</strong> %d VND</p>
                    <p><strong>Order ID:</strong> %s</p>
                    <p><strong>Payment URL:</strong></p>
                    <textarea style="width: 100%%; height: 100px; font-size: 12px;">%s</textarea>
                    <br><br>
                    <a href="%s" target="_blank" style="background-color: #ff6b35; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;">
                        Test Payment
                    </a>
                </body>
                </html>
                """, amount, orderId, paymentUrl, paymentUrl);
                
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    /**
     * Kiểm tra cấu hình VNPAY
     */
    @GetMapping("/vnpay-config")
    public String checkConfig() {
        return String.format("""
            <html>
            <body style="font-family: Arial, sans-serif; padding: 20px;">
                <h2>VNPAY Configuration</h2>
                <table border="1" style="border-collapse: collapse; width: 100%%;">
                    <tr><td><strong>TmnCode:</strong></td><td>%s</td></tr>
                    <tr><td><strong>HashSecret:</strong></td><td>%s</td></tr>
                    <tr><td><strong>PayUrl:</strong></td><td>%s</td></tr>
                    <tr><td><strong>ReturnUrl:</strong></td><td>%s</td></tr>
                    <tr><td><strong>IpnUrl:</strong></td><td>%s</td></tr>
                </table>
            </body>
            </html>
            """, 
            vnPayConfig.getTmnCode(),
            vnPayConfig.getHashSecret(),
            vnPayConfig.getPayUrl(),
            vnPayConfig.getReturnUrl(),
            vnPayConfig.getIpnUrl()
        );
    }
}
