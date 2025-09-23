package panacea.website_dat_lich_khach_san.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Cấu hình SePay - Hệ thống thanh toán QR Code
 * SePay cho phép tạo QR code để nhận thanh toán từ các ví điện tử
 */
@Configuration
@ConfigurationProperties(prefix = "sepay")
public class SePayConfig {
    
    // Thông tin tài khoản ngân hàng
    private String bankAccount = "1392005882005";
    private String bankName = "MBBank";
    private String accountHolder = "Panacea Hotel";
    
    // Cấu hình API SePay (nếu có)
    private String apiUrl = "https://api.sepay.vn";
    private String apiKey = "";
    private String secretKey = "";
    
    // Cấu hình QR Code
    private int qrCodeSize = 250;
    private String qrCodeFormat = "PNG";
    
    // Cấu hình callback
    private String returnUrl = "https://b813094b7291.ngrok-free.app/sepay/return";
    private String notifyUrl = "https://b813094b7291.ngrok-free.app/sepay/notify";
    
    // Getters and Setters
    public String getBankAccount() {
        return bankAccount;
    }
    
    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }
    
    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
    public String getAccountHolder() {
        return accountHolder;
    }
    
    public void setAccountHolder(String accountHolder) {
        this.accountHolder = accountHolder;
    }
    
    public String getApiUrl() {
        return apiUrl;
    }
    
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public String getSecretKey() {
        return secretKey;
    }
    
    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
    
    public int getQrCodeSize() {
        return qrCodeSize;
    }
    
    public void setQrCodeSize(int qrCodeSize) {
        this.qrCodeSize = qrCodeSize;
    }
    
    public String getQrCodeFormat() {
        return qrCodeFormat;
    }
    
    public void setQrCodeFormat(String qrCodeFormat) {
        this.qrCodeFormat = qrCodeFormat;
    }
    
    public String getReturnUrl() {
        return returnUrl;
    }
    
    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }
    
    public String getNotifyUrl() {
        return notifyUrl;
    }
    
    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }
}
