package panacea.website_dat_lich_khach_san.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import panacea.website_dat_lich_khach_san.config.SePayConfig;

/**
 * Service xử lý thanh toán SePay
 * SePay là hệ thống tạo QR code để nhận thanh toán từ các ví điện tử
 */
@Service
public class SePayService {

    @Autowired
    private SePayConfig sePayConfig;

    /**
     * Tạo QR code cho thanh toán SePay sử dụng API thực tế
     * @param amount - Số tiền cần thanh toán
     * @param orderId - Mã đơn hàng
     * @param description - Mô tả thanh toán
     * @return byte[] - Dữ liệu ảnh QR code PNG
     */
    public byte[] generateSePayQRCode(BigDecimal amount, String orderId, String description) {
        try {
            // Sử dụng API thực tế của SePay
            String qrImageUrl = createSePayQRImageUrl(amount, orderId, description);
            
            // Download ảnh QR từ API SePay
            byte[] qrImageBytes = downloadQRImageFromSePay(qrImageUrl);
            
            if (qrImageBytes != null) {
                System.out.println("=== SEPAY QR CODE GENERATED (API) ===");
                System.out.println("Amount: " + amount);
                System.out.println("Order ID: " + orderId);
                System.out.println("Description: " + description);
                System.out.println("QR Image URL: " + qrImageUrl);
                System.out.println("Image Size: " + qrImageBytes.length + " bytes");
                System.out.println("=====================================");
                
                return qrImageBytes;
            } else {
                // Fallback: tạo QR code bằng ZXing nếu API không hoạt động
                return generateSePayQRCodeFallback(amount, orderId, description);
            }
            
        } catch (Exception e) {
            System.err.println("Error generating SePay QR code: " + e.getMessage());
            e.printStackTrace();
            // Fallback: tạo QR code bằng ZXing
            return generateSePayQRCodeFallback(amount, orderId, description);
        }
    }

    /**
     * Tạo nội dung QR code theo format SePay
     * Format: bank://transfer?account=1392005882005&amount=100000&note=DatPhong_BOOK123456
     */
    private String createSePayQRContent(BigDecimal amount, String orderId, String description) {
        DecimalFormat df = new DecimalFormat("#");
        String amountStr = df.format(amount.multiply(new BigDecimal("1000"))); // Chuyển sang đơn vị xu
        
        StringBuilder qrContent = new StringBuilder();
        qrContent.append("bank://transfer");
        qrContent.append("?account=").append(sePayConfig.getBankAccount());
        qrContent.append("&amount=").append(amountStr);
        qrContent.append("&note=").append(description);
        
        return qrContent.toString();
    }

    /**
     * Tạo link thanh toán SePay (nếu có API)
     * @param amount - Số tiền
     * @param orderId - Mã đơn hàng
     * @return String - Link thanh toán
     */
    public String createSePayPaymentUrl(BigDecimal amount, String orderId) {
        try {
            // Nếu có API SePay thật, sử dụng API
            if (sePayConfig.getApiKey() != null && !sePayConfig.getApiKey().isEmpty()) {
                return createSePayApiUrl(amount, orderId);
            }
            
            // Fallback: tạo link demo
            return createSePayDemoUrl(amount, orderId);
            
        } catch (Exception e) {
            System.err.println("Error creating SePay payment URL: " + e.getMessage());
            return "#";
        }
    }

    /**
     * Tạo link API SePay thật
     */
    private String createSePayApiUrl(BigDecimal amount, String orderId) {
        // TODO: Implement real SePay API integration
        // Đây là placeholder cho việc tích hợp API thật
        return sePayConfig.getApiUrl() + "/payment/create?amount=" + amount + "&orderId=" + orderId;
    }

    /**
     * Tạo link demo SePay
     */
    private String createSePayDemoUrl(BigDecimal amount, String orderId) {
        // Tạo link demo để test
        String demoUrl = "https://sepay.vn/demo/payment";
        String description = "DatPhong_" + orderId;
        
        return demoUrl + "?account=" + sePayConfig.getBankAccount() + 
               "&amount=" + amount + 
               "&note=" + description +
               "&bank=" + sePayConfig.getBankName();
    }

    /**
     * Xác thực callback từ SePay
     * @param signature - Chữ ký xác thực
     * @param data - Dữ liệu callback
     * @return boolean - true nếu hợp lệ
     */
    public boolean verifySePayCallback(String signature, Map<String, String> data) {
        try {
            // TODO: Implement signature verification
            // Đây là placeholder cho việc xác thực chữ ký thật
            return true;
        } catch (Exception e) {
            System.err.println("Error verifying SePay callback: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy thông tin tài khoản ngân hàng
     */
    public Map<String, String> getBankAccountInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("account", sePayConfig.getBankAccount());
        info.put("bank", sePayConfig.getBankName());
        info.put("holder", sePayConfig.getAccountHolder());
        return info;
    }

    /**
     * Tạo nội dung chuyển khoản cho khách hàng
     */
    public String createTransferContent(String orderId) {
        return "DatPhong_" + orderId;
    }

    /**
     * Tạo URL để lấy ảnh QR từ API SePay thực tế
     * Format: https://qr.sepay.vn/img?acc=1392005882005&bank=MBBank&amount=100000&des=DatPhong_BOOK123&template=compact&download=1
     */
    private String createSePayQRImageUrl(BigDecimal amount, String orderId, String description) {
        try {
            StringBuilder url = new StringBuilder("https://qr.sepay.vn/img");
            url.append("?acc=").append(URLEncoder.encode(sePayConfig.getBankAccount(), StandardCharsets.UTF_8));
            url.append("&bank=").append(URLEncoder.encode(sePayConfig.getBankName(), StandardCharsets.UTF_8));
            url.append("&amount=").append(amount.toString());
            url.append("&des=").append(URLEncoder.encode(description, StandardCharsets.UTF_8));
            url.append("&template=compact"); // Template compact cho email
            // Không cần &download=1 vì chúng ta nhúng trực tiếp vào HTML
            
            String finalUrl = url.toString();
            System.out.println("=== SEPAY QR URL GENERATED ===");
            System.out.println("URL: " + finalUrl);
            System.out.println("Amount: " + amount);
            System.out.println("Order ID: " + orderId);
            System.out.println("Description: " + description);
            System.out.println("===============================");
            
            return finalUrl;
        } catch (Exception e) {
            System.err.println("Error creating SePay QR image URL: " + e.getMessage());
            return null;
        }
    }

    /**
     * Download ảnh QR từ API SePay
     */
    private byte[] downloadQRImageFromSePay(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            try (InputStream inputStream = url.openStream();
                 ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                
                byte[] imageBytes = outputStream.toByteArray();
                System.out.println("Downloaded QR image from SePay API: " + imageBytes.length + " bytes");
                return imageBytes;
                
            }
        } catch (IOException e) {
            System.err.println("Error downloading QR image from SePay: " + e.getMessage());
            return null;
        }
    }

    /**
     * Fallback: Tạo QR code bằng ZXing nếu API SePay không hoạt động
     */
    public byte[] generateSePayQRCodeFallback(BigDecimal amount, String orderId, String description) {
        try {
            // Tạo nội dung QR code theo format SePay
            String qrContent = createSePayQRContent(amount, orderId, description);
            
            // Tạo QR code bằng ZXing
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, 1);
            
            BitMatrix bitMatrix = qrCodeWriter.encode(
                qrContent, 
                BarcodeFormat.QR_CODE, 
                sePayConfig.getQrCodeSize(), 
                sePayConfig.getQrCodeSize(), 
                hints
            );
            
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, sePayConfig.getQrCodeFormat(), pngOutputStream);
            
            System.out.println("=== SEPAY QR CODE GENERATED (FALLBACK) ===");
            System.out.println("Amount: " + amount);
            System.out.println("Order ID: " + orderId);
            System.out.println("Description: " + description);
            System.out.println("QR Content: " + qrContent);
            System.out.println("===========================================");
            
            return pngOutputStream.toByteArray();
            
        } catch (Exception e) {
            System.err.println("Error generating SePay QR code fallback: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy URL ảnh QR từ SePay API (để hiển thị trực tiếp trong HTML)
     */
    public String getSePayQRImageUrl(BigDecimal amount, String orderId, String description) {
        String url = createSePayQRImageUrl(amount, orderId, description);
        
        // Test URL trước khi trả về
        if (url != null && !url.isEmpty()) {
            if (testSePayUrl(url)) {
                return url;
            } else {
                System.err.println("SePay URL test failed: " + url);
                return null;
            }
        }
        
        return url;
    }

    /**
     * Test xem URL SePay có hoạt động không
     */
    private boolean testSePayUrl(String url) {
        try {
            URL testUrl = new URL(url);
            try (InputStream inputStream = testUrl.openStream()) {
                // Đọc một vài byte để kiểm tra
                byte[] buffer = new byte[1024];
                int bytesRead = inputStream.read(buffer);
                return bytesRead > 0;
            }
        } catch (Exception e) {
            System.err.println("SePay URL test error: " + e.getMessage());
            return false;
        }
    }
}
