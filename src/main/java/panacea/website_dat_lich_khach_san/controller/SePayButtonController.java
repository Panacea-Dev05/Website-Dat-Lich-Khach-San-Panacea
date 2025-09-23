package panacea.website_dat_lich_khach_san.controller;

import panacea.website_dat_lich_khach_san.service.SePayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * Controller để test nút QR SePay
 */
@Controller
@RequestMapping("/sepay-button")
public class SePayButtonController {

    @Autowired
    private SePayService sePayService;

    /**
     * Trang test nút QR
     */
    @GetMapping("/test")
    public String testButton(
            @RequestParam(defaultValue = "50000") BigDecimal amount,
            @RequestParam(defaultValue = "BOOK1758620982318") String orderId,
            @RequestParam(defaultValue = "DatPhong_BOOK1758620982318") String description,
            Model model) {
        
        // Tạo URL SePay
        String qrUrl = sePayService.getSePayQRImageUrl(amount, orderId, description);
        
        // Fallback nếu URL lỗi
        if (qrUrl == null || qrUrl.isEmpty()) {
            qrUrl = "https://qr.sepay.vn/img?acc=1392005882005&bank=MBBank&amount=" + 
                   amount + "&des=" + description + "&template=compact";
        }
        
        model.addAttribute("qrUrl", qrUrl);
        model.addAttribute("amount", amount);
        model.addAttribute("orderId", orderId);
        model.addAttribute("description", description);
        
        return "sepay-button-test";
    }

    /**
     * API trả về URL QR cho JavaScript
     */
    @GetMapping("/qr-url")
    @ResponseBody
    public String getQRUrl(
            @RequestParam BigDecimal amount,
            @RequestParam String orderId,
            @RequestParam(required = false) String description) {
        
        if (description == null) {
            description = "DatPhong_" + orderId;
        }
        
        String qrUrl = sePayService.getSePayQRImageUrl(amount, orderId, description);
        
        // Fallback nếu URL lỗi
        if (qrUrl == null || qrUrl.isEmpty()) {
            qrUrl = "https://qr.sepay.vn/img?acc=1392005882005&bank=MBBank&amount=" + 
                   amount + "&des=" + description + "&template=compact";
        }
        
        return qrUrl;
    }
}
