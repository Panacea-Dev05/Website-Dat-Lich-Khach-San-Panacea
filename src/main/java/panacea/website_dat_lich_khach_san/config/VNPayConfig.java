package panacea.website_dat_lich_khach_san.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class VNPayConfig {

    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.payUrl}")
    private String vnp_PayUrl;

    @Value("${vnpay.returnUrl}")
    private String vnp_ReturnUrl;

    @Value("${vnpay.ipnUrl}")
    private String vnp_IpnUrl;

    public String getTmnCode() { return vnp_TmnCode; }
    public String getHashSecret() { return vnp_HashSecret; }
    public String getPayUrl() { return vnp_PayUrl; }
    public String getReturnUrl() { return vnp_ReturnUrl; }
    public String getIpnUrl() { return vnp_IpnUrl; }
}
