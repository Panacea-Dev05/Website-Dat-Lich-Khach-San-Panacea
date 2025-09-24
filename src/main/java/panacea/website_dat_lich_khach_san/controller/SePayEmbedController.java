package panacea.website_dat_lich_khach_san.controller;

import panacea.website_dat_lich_khach_san.service.SePayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Controller để test việc nhúng QR code SePay trực tiếp vào HTML
 */
@Controller
@RequestMapping("/sepay-embed")
public class SePayEmbedController {

    @Autowired
    private SePayService sePayService;

    /**
     * Trang test nhúng QR code SePay
     */
    @GetMapping("/test")
    public String testEmbedQR(
            @RequestParam(defaultValue = "100000") BigDecimal amount,
            @RequestParam(defaultValue = "TEST123") String orderId,
            @RequestParam(defaultValue = "DatPhong_TEST123") String description,
            Model model) {
        
        try {
            String qrImageUrl = sePayService.getSePayQRImageUrl(amount, orderId, description);
            
            model.addAttribute("qrImageUrl", qrImageUrl);
            model.addAttribute("amount", amount);
            model.addAttribute("orderId", orderId);
            model.addAttribute("description", description);
            model.addAttribute("bankInfo", sePayService.getBankAccountInfo());
            
            return "sepay-embed-test";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error generating QR code: " + e.getMessage());
            return "sepay-embed-error";
        }
    }

    /**
     * API trả về HTML với QR code nhúng
     */
    @GetMapping("/qr-html")
    @ResponseBody
    public String getQRHtml(
            @RequestParam BigDecimal amount,
            @RequestParam String orderId,
            @RequestParam(required = false) String description) {
        
        try {
            String desc = description != null ? description : "DatPhong_" + orderId;
            String qrImageUrl = sePayService.getSePayQRImageUrl(amount, orderId, desc);
            
            if (qrImageUrl != null) {
                return String.format(
                    "<div style='text-align: center; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>" +
                    "<h3>QR Code Thanh Toán SePay</h3>" +
                    "<img src='%s' width='250' height='250' alt='QR Code SePay' style='border: 1px solid #ccc;'/>" +
                    "<p><b>Số tiền:</b> %,.0f VNĐ</p>" +
                    "<p><b>Mã đơn hàng:</b> %s</p>" +
                    "<p><b>Nội dung:</b> %s</p>" +
                    "<p><b>Tài khoản:</b> %s - %s</p>" +
                    "</div>",
                    qrImageUrl,
                    amount,
                    orderId,
                    desc,
                    sePayService.getBankAccountInfo().get("account"),
                    sePayService.getBankAccountInfo().get("bank")
                );
            } else {
                return "<div style='color: red;'>Error: Không thể tạo QR code</div>";
            }
            
        } catch (Exception e) {
            return "<div style='color: red;'>Error: " + e.getMessage() + "</div>";
        }
    }
}



