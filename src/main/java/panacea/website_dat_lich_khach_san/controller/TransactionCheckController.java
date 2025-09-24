package panacea.website_dat_lich_khach_san.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.service.SePayService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller để nhân viên kiểm tra giao dịch và truy cập Google Sheets
 */
@Controller
@RequestMapping("/nhan-vien/transaction-check")
public class TransactionCheckController {

    private final SePayService sePayService;
    
    // Link Google Sheets SePay
    private static final String SEPAY_SHEETS_URL = "https://docs.google.com/spreadsheets/d/1CG0W13E-wvMZEkPXp-k4y2cFlpGW2l8Q0dFnoYsUZzg/edit?gid=0#gid=0";

    public TransactionCheckController(SePayService sePayService) {
        this.sePayService = sePayService;
    }

    /**
     * Trang chính để kiểm tra giao dịch
     */
    @GetMapping
    public String transactionCheckPage(Model model) {
        model.addAttribute("sheetsUrl", SEPAY_SHEETS_URL);
        return "NhanVien/TransactionCheck";
    }

    /**
     * Kiểm tra giao dịch theo mã booking
     */
    @PostMapping("/check")
    @ResponseBody
    public Map<String, Object> checkTransaction(@RequestParam String bookingCode) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Giả lập kiểm tra giao dịch (có thể tích hợp với database thực tế)
            boolean transactionFound = checkTransactionInDatabase(bookingCode);
            
            if (transactionFound) {
                result.put("success", true);
                result.put("message", "Giao dịch đã được tìm thấy!");
                result.put("bookingCode", bookingCode);
                result.put("sheetsUrl", SEPAY_SHEETS_URL);
                result.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            } else {
                result.put("success", false);
                result.put("message", "Không tìm thấy giao dịch với mã: " + bookingCode);
            }
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Lỗi khi kiểm tra giao dịch: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Tạo URL QR để kiểm tra giao dịch
     */
    @GetMapping("/generate-qr")
    @ResponseBody
    public Map<String, Object> generateCheckQR(@RequestParam String bookingCode) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Tạo QR code với thông tin kiểm tra giao dịch
            String qrContent = createTransactionCheckQRContent(bookingCode);
            String qrImageUrl = sePayService.getSePayQRImageUrl(
                new BigDecimal("1000"), // Số tiền nhỏ để test
                "CHECK_" + bookingCode,
                "KiemTraGiaoDich_" + bookingCode
            );
            
            result.put("success", true);
            result.put("qrImageUrl", qrImageUrl);
            result.put("qrContent", qrContent);
            result.put("bookingCode", bookingCode);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "Lỗi khi tạo QR code: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * Mở Google Sheets trong tab mới
     */
    @GetMapping("/open-sheets")
    public String openSheets(@RequestParam(required = false) String bookingCode, Model model) {
        model.addAttribute("sheetsUrl", SEPAY_SHEETS_URL);
        model.addAttribute("bookingCode", bookingCode);
        return "NhanVien/OpenSheets";
    }

    /**
     * Giả lập kiểm tra giao dịch trong database
     */
    private boolean checkTransactionInDatabase(String bookingCode) {
        // TODO: Tích hợp với database thực tế để kiểm tra giao dịch
        // Hiện tại giả lập luôn trả về true
        return bookingCode != null && !bookingCode.trim().isEmpty();
    }

    /**
     * Tạo nội dung QR code để kiểm tra giao dịch
     */
    private String createTransactionCheckQRContent(String bookingCode) {
        return String.format(
            "Kiểm tra giao dịch - Mã booking: %s - Thời gian: %s",
            bookingCode,
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        );
    }
}

