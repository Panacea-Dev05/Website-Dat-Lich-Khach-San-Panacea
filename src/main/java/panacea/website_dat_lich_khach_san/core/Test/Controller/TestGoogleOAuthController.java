package panacea.website_dat_lich_khach_san.core.Test.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.entity.Customer;
import panacea.website_dat_lich_khach_san.repository.CustomerRepository;
import panacea.website_dat_lich_khach_san.infrastructure.Enums.LoaiKhachHang;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/test")
public class TestGoogleOAuthController {
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @PostMapping("/api/test-google-oauth")
    public ResponseEntity<?> testGoogleOAuth(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String name = request.get("name");
            
            System.out.println("[TEST] Testing Google OAuth customer creation");
            System.out.println("[TEST] Email: " + email);
            System.out.println("[TEST] Name: " + name);
            
            // Kiểm tra email đã tồn tại chưa
            Optional<Customer> existingCustomer = customerRepository.findByEmail(email);
            if (existingCustomer.isPresent()) {
                System.out.println("[TEST] Customer already exists with ID: " + existingCustomer.get().getId());
                return ResponseEntity.ok(Map.of(
                    "status", "exists",
                    "message", "Customer already exists",
                    "customerId", existingCustomer.get().getId(),
                    "customerCode", existingCustomer.get().getMaKhachHang()
                ));
            }
            
            // Tạo customer mới với validation tương tự SecurityConfig
            Customer newCustomer = new Customer();
            
            // Validate email
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email không được để trống");
            }
            newCustomer.setEmail(email.trim());
            
            // Xử lý tên với validation
            if (name != null && !name.trim().isEmpty()) {
                String[] nameParts = name.trim().split("\\s+");
                String ho = nameParts[0].length() > 50 ? nameParts[0].substring(0, 50) : nameParts[0];
                newCustomer.setHo(ho);
                
                if (nameParts.length > 1) {
                    String ten = String.join(" ", java.util.Arrays.copyOfRange(nameParts, 1, nameParts.length));
                    ten = ten.length() > 50 ? ten.substring(0, 50) : ten;
                    newCustomer.setTen(ten);
                } else {
                    newCustomer.setTen("User");
                }
            } else {
                newCustomer.setHo("Google");
                newCustomer.setTen("User");
            }
            
            // Validate required fields
            if (newCustomer.getHo() == null || newCustomer.getHo().trim().isEmpty()) {
                newCustomer.setHo("Google");
            }
            if (newCustomer.getTen() == null || newCustomer.getTen().trim().isEmpty()) {
                newCustomer.setTen("User");
            }
            
            // Set other required fields
        newCustomer.setTrangThai(Customer.TrangThaiCustomer.HOAT_DONG);
        newCustomer.setLoaiKhachHang(LoaiKhachHang.CA_NHAN.getDbValue()); // Sử dụng enum value "Cá nhân"
        newCustomer.setMatKhauHash("GOOGLE_OAUTH");
            newCustomer.setDiemTichLuy(0);
            
            // Generate unique customer code
            String customerCode = generateUniqueCustomerCode();
            newCustomer.setMaKhachHang(customerCode);
            
            // Set UUID và timestamps
            newCustomer.setUuidId(java.util.UUID.randomUUID());
            long currentTime = System.currentTimeMillis();
            newCustomer.setCreatedDate(currentTime);
            newCustomer.setLastModifiedDate(currentTime);
            
            System.out.println("[TEST] About to save customer:");
            System.out.println("[TEST] Ho: '" + newCustomer.getHo() + "' (length: " + newCustomer.getHo().length() + ")");
            System.out.println("[TEST] Ten: '" + newCustomer.getTen() + "' (length: " + newCustomer.getTen().length() + ")");
            System.out.println("[TEST] MaKhachHang: '" + newCustomer.getMaKhachHang() + "' (length: " + newCustomer.getMaKhachHang().length() + ")");
            System.out.println("[TEST] TrangThai: " + newCustomer.getTrangThai() + " - DB value: " + newCustomer.getTrangThai().getValue());
            System.out.println("[TEST] DiemTichLuy: " + newCustomer.getDiemTichLuy());
            System.out.println("[TEST] MatKhauHash: " + newCustomer.getMatKhauHash());
            
            Customer savedCustomer = customerRepository.save(newCustomer);
            System.out.println("[TEST] Customer saved successfully with ID: " + savedCustomer.getId());
            
            return ResponseEntity.ok(Map.of(
                "status", "created",
                "message", "Customer created successfully",
                "customerId", savedCustomer.getId(),
                "customerCode", savedCustomer.getMaKhachHang(),
                "email", savedCustomer.getEmail(),
                "ho", savedCustomer.getHo(),
                "ten", savedCustomer.getTen()
            ));
            
        } catch (Exception e) {
            System.err.println("[TEST ERROR] Failed to create customer: " + e.getMessage());
            System.err.println("[TEST ERROR] Exception type: " + e.getClass().getSimpleName());
            if (e.getCause() != null) {
                System.err.println("[TEST ERROR] Root cause: " + e.getCause().getMessage());
            }
            e.printStackTrace();
            
            return ResponseEntity.status(500).body(Map.of(
                "status", "error",
                "message", e.getMessage(),
                "error", e.getClass().getSimpleName(),
                "cause", e.getCause() != null ? e.getCause().getMessage() : "No cause"
            ));
        }
    }
    
    // Helper method to generate unique customer code
    private String generateUniqueCustomerCode() {
        int maxRetries = 5;
        for (int i = 0; i < maxRetries; i++) {
            String code = "KH" + System.currentTimeMillis() + (i > 0 ? "_" + i : "");
            if (!customerRepository.existsByMaKhachHang(code)) {
                return code;
            }
            try {
                Thread.sleep(1); // Wait 1ms to ensure different timestamp
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        throw new RuntimeException("Unable to generate unique customer code after " + maxRetries + " attempts");
    }
}