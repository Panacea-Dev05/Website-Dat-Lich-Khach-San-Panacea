package panacea.website_dat_lich_khach_san.service;

import panacea.website_dat_lich_khach_san.config.VNPayConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayService {

    @Autowired
    private VNPayConfig vnPayConfig;

    public String createPaymentUrl(long amount, String orderId) {
        try {
            // Sử dụng VNPAY Demo URL thay vì sandbox thật
            String demoUrl = "https://sandbox.vnpayment.vn/tryitnow/Home/CreateOrder";
            
            // Tạo parameters cho demo
            Map<String, String> demoParams = new HashMap<>();
            demoParams.put("amount", String.valueOf(amount));
            demoParams.put("orderInfo", "Thanh toan don hang: " + orderId);
            demoParams.put("orderId", orderId);
            
            // Tạo query string
            StringBuilder query = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : demoParams.entrySet()) {
                if (!first) {
                    query.append('&');
                }
                query.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
                first = false;
            }
            
            String finalUrl = demoUrl + "?" + query.toString();
            
            // Debug log
            System.out.println("=== VNPAY DEMO DEBUG ===");
            System.out.println("Demo URL: " + finalUrl);
            System.out.println("Amount: " + amount);
            System.out.println("Order ID: " + orderId);
            System.out.println("========================");
            
            return finalUrl;
        } catch (Exception e) {
            throw new RuntimeException("Error create payment url", e);
        }
    }
}
