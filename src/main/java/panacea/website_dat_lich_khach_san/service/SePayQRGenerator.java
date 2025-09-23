package panacea.website_dat_lich_khach_san.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Service tạo QR code SePay mẫu
 * Dùng để tạo file QR code tĩnh cho SePay
 */
@Service
public class SePayQRGenerator {

    @Autowired
    private SePayService sePayService;

    /**
     * Tạo file QR code SePay mẫu
     */
    public void generateSampleSePayQR() {
        try {
            // Tạo QR code mẫu
            BigDecimal sampleAmount = new BigDecimal("100000"); // 100k VNĐ
            String sampleOrderId = "SAMPLE123";
            String sampleDescription = "DatPhong_SAMPLE123";
            
            byte[] qrCodeBytes = sePayService.generateSePayQRCode(sampleAmount, sampleOrderId, sampleDescription);
            
            if (qrCodeBytes != null) {
                // Lưu vào file
                Path imgPath = Paths.get("src/main/resources/static/img/sepay-qr.png");
                Files.createDirectories(imgPath.getParent());
                
                try (FileOutputStream fos = new FileOutputStream(imgPath.toFile())) {
                    fos.write(qrCodeBytes);
                    System.out.println("SePay QR code sample generated: " + imgPath.toString());
                }
            } else {
                System.err.println("Failed to generate SePay QR code sample");
            }
            
        } catch (Exception e) {
            System.err.println("Error generating SePay QR code sample: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Tạo QR code với thông tin tùy chỉnh
     */
    public void generateCustomSePayQR(String fileName, BigDecimal amount, String orderId, String description) {
        try {
            byte[] qrCodeBytes = sePayService.generateSePayQRCode(amount, orderId, description);
            
            if (qrCodeBytes != null) {
                Path imgPath = Paths.get("src/main/resources/static/img/" + fileName);
                Files.createDirectories(imgPath.getParent());
                
                try (FileOutputStream fos = new FileOutputStream(imgPath.toFile())) {
                    fos.write(qrCodeBytes);
                    System.out.println("Custom SePay QR code generated: " + imgPath.toString());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error generating custom SePay QR code: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
