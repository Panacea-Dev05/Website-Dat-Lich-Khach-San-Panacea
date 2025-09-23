package panacea.website_dat_lich_khach_san.controller;

import panacea.website_dat_lich_khach_san.service.SePayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URL;
import java.io.InputStream;

/**
 * Controller để debug và test SePay URL
 */
@RestController
@RequestMapping("/debug/sepay")
public class SePayDebugController {

    @Autowired
    private SePayService sePayService;

    /**
     * Test URL SePay với thông tin cụ thể
     */
    @GetMapping("/test-url")
    public String testSePayUrl(
            @RequestParam(defaultValue = "50000") BigDecimal amount,
            @RequestParam(defaultValue = "BOOK1758620982318") String orderId,
            @RequestParam(defaultValue = "DatPhong_BOOK1758620982318") String description) {
        
        try {
            // Tạo URL SePay
            String sepayUrl = sePayService.getSePayQRImageUrl(amount, orderId, description);
            
            StringBuilder result = new StringBuilder();
            result.append("=== SEPAY URL DEBUG ===\n");
            result.append("Amount: ").append(amount).append(" VNĐ\n");
            result.append("Order ID: ").append(orderId).append("\n");
            result.append("Description: ").append(description).append("\n");
            result.append("Generated URL: ").append(sepayUrl).append("\n\n");
            
            if (sepayUrl != null && !sepayUrl.isEmpty()) {
                // Test URL
                boolean urlWorks = testUrl(sepayUrl);
                result.append("URL Test: ").append(urlWorks ? "✅ WORKS" : "❌ FAILED").append("\n");
                
                if (urlWorks) {
                    result.append("✅ URL is accessible and returns data\n");
                } else {
                    result.append("❌ URL is not accessible or returns no data\n");
                    result.append("Possible issues:\n");
                    result.append("- SePay API is down\n");
                    result.append("- Invalid parameters\n");
                    result.append("- Network connectivity issues\n");
                }
            } else {
                result.append("❌ Failed to generate URL\n");
            }
            
            result.append("\n=== FALLBACK TEST ===\n");
            // Test fallback QR generation
            byte[] fallbackQR = sePayService.generateSePayQRCodeFallback(amount, orderId, description);
            if (fallbackQR != null) {
                result.append("✅ Fallback QR generation works (").append(fallbackQR.length).append(" bytes)\n");
                result.append("Base64 data URL: data:image/png;base64,").append(java.util.Base64.getEncoder().encodeToString(fallbackQR).substring(0, 50)).append("...\n");
            } else {
                result.append("❌ Fallback QR generation failed\n");
            }
            
            return result.toString();
            
        } catch (Exception e) {
            return "Error testing SePay URL: " + e.getMessage();
        }
    }

    /**
     * Test URL accessibility
     */
    private boolean testUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            try (InputStream inputStream = url.openStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);
                return bytesRead > 0;
            }
        } catch (Exception e) {
            System.err.println("URL test error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Test với URL SePay trực tiếp
     */
    @GetMapping("/test-direct-url")
    public String testDirectSePayUrl() {
        try {
            // Test URL trực tiếp từ SePay
            String directUrl = "https://qr.sepay.vn/img?acc=1392005882005&bank=MBBank&amount=50000&des=DatPhong_BOOK1758620982318&template=compact";
            
            StringBuilder result = new StringBuilder();
            result.append("=== DIRECT SEPAY URL TEST ===\n");
            result.append("Testing URL: ").append(directUrl).append("\n\n");
            
            boolean urlWorks = testUrl(directUrl);
            result.append("Direct URL Test: ").append(urlWorks ? "✅ WORKS" : "❌ FAILED").append("\n");
            
            if (!urlWorks) {
                result.append("\n❌ SePay API appears to be down or not responding\n");
                result.append("Recommendation: Use fallback QR generation with ZXing\n");
            }
            
            return result.toString();
            
        } catch (Exception e) {
            return "Error testing direct SePay URL: " + e.getMessage();
        }
    }

    /**
     * Generate fallback QR code
     */
    @GetMapping("/fallback-qr")
    public String generateFallbackQR(
            @RequestParam(defaultValue = "50000") BigDecimal amount,
            @RequestParam(defaultValue = "BOOK1758620982318") String orderId,
            @RequestParam(defaultValue = "DatPhong_BOOK1758620982318") String description) {
        
        try {
            byte[] qrBytes = sePayService.generateSePayQRCodeFallback(amount, orderId, description);
            
            if (qrBytes != null) {
                String base64QR = java.util.Base64.getEncoder().encodeToString(qrBytes);
                return String.format(
                    "<div style='text-align: center; padding: 20px;'>" +
                    "<h3>Fallback QR Code (ZXing)</h3>" +
                    "<img src='data:image/png;base64,%s' width='250' height='250' alt='QR Code'/>" +
                    "<p><b>Số tiền:</b> %,.0f VNĐ</p>" +
                    "<p><b>Mã đơn hàng:</b> %s</p>" +
                    "<p><b>Nội dung:</b> %s</p>" +
                    "</div>",
                    base64QR, amount, orderId, description
                );
            } else {
                return "<div style='color: red;'>Error generating fallback QR code</div>";
            }
            
        } catch (Exception e) {
            return "<div style='color: red;'>Error: " + e.getMessage() + "</div>";
        }
    }
}
