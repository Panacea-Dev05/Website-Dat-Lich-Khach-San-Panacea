package panacea.website_dat_lich_khach_san.infrastructure.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import panacea.website_dat_lich_khach_san.entity.Staff;
import panacea.website_dat_lich_khach_san.entity.Customer;
import panacea.website_dat_lich_khach_san.repository.StaffRepository;
import panacea.website_dat_lich_khach_san.repository.CustomerRepository;
import panacea.website_dat_lich_khach_san.infrastructure.Enums.LoaiKhachHang;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpMethod;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.AuthenticationEntryPoint;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(
            googleClientRegistration(),
            facebookClientRegistration()
        );
    }

    private ClientRegistration googleClientRegistration() {
        return ClientRegistration.withRegistrationId("google")
            .clientId("185058313445-gfgbd1f7hpkl424fpgor3if3l0os83hr.apps.googleusercontent.com")
            .clientSecret("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")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("http://localhost:8080/login/oauth2/code/google")
            .scope("profile", "email")
            .authorizationUri("https://accounts.google.com/o/oauth2/auth")
            .tokenUri("https://oauth2.googleapis.com/token")
            .userInfoUri("https://www.googleapis.com/oauth2/v2/userinfo")
            .userNameAttributeName("email")
            .clientName("Google")
            .build();
    }

    private ClientRegistration facebookClientRegistration() {
        return ClientRegistration.withRegistrationId("facebook")
            .clientId("781851997739362")
            .clientSecret("038c7ff0cea9c2418f18c12132b3267e")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("http://localhost:8080/login/oauth2/code/facebook")
            .scope("email", "public_profile")
            .authorizationUri("https://www.facebook.com/v12.0/dialog/oauth")
            .tokenUri("https://graph.facebook.com/v12.0/oauth/access_token")
            .userInfoUri("https://graph.facebook.com/me?fields=id,name,email,picture")
            .userNameAttributeName("id")
            .clientName("Facebook")
            .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.sameOrigin()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/css/**",
                                "/js/**",
                                "/img/**",      // <-- Thêm dòng này
                                "/images/**",
                                "/fonts/**",
                                "/vendor/**",
                                "/assets/**",
                                "/KhachHang/**",
                                "/login",
                                "/oauth2/**"
                        ).permitAll()
                        .requestMatchers("/khachhang/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/nhanvien/**").hasRole("NHANVIEN")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(eh -> eh.authenticationEntryPoint(new AuthenticationEntryPoint() {
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, org.springframework.security.core.AuthenticationException authException) throws IOException {
                        String accept = request.getHeader("Accept");
                        String xhr = request.getHeader("X-Requested-With");
                        if ((accept != null && accept.contains("application/json")) ||
                            (xhr != null && xhr.equalsIgnoreCase("XMLHttpRequest"))) {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        } else {
                            response.sendRedirect("/login");
                        }
                    }
                }))
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(oauth2UserService()))
                        .defaultSuccessUrl("/api/auth/current-role", true)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/khachhang")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );
        return http.build();
    }

    @Bean
    public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
        DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
        return request -> {
            OAuth2User user = delegate.loadUser(request);
            String email = user.getAttribute("email");
            String name = user.getAttribute("name");

            // 1. Check staff
            Optional<Staff> staffOpt = staffRepository.findByEmail(email);
            if (staffOpt.isPresent()) {
                Staff staff = staffOpt.get();
                String role = staff.getQuyenHan().name().toUpperCase();
                String mappedRole = switch (role) {
                    case "ADMIN" -> "ROLE_ADMIN";
                    case "MANAGER", "STAFF", "VIEWER" -> "ROLE_NHANVIEN";
                    default -> "ROLE_NHANVIEN";
                };
                return new DefaultOAuth2User(
                        List.of(new SimpleGrantedAuthority(mappedRole)),
                        user.getAttributes(),
                        "email"
                );
            }

            // 2. Check customer
            Optional<Customer> customerOpt = customerRepository.findByEmail(email);
            if (customerOpt.isPresent()) {
                return new DefaultOAuth2User(
                        List.of(new SimpleGrantedAuthority("ROLE_KHACHHANG")),
                        user.getAttributes(),
                        "email"
                );
            }

            // 3. Nếu chưa có, tạo mới customer
            try {
                Customer newCustomer = new Customer();
                
                // Validate email
                if (email == null || email.trim().isEmpty()) {
                    throw new IllegalArgumentException("Email không được để trống");
                }
                newCustomer.setEmail(email.trim());
                
                // Xử lý tên an toàn hơn với validation
                if (name != null && !name.trim().isEmpty()) {
                    String[] nameParts = name.trim().split("\\s+");
                    String ho = nameParts[0].length() > 50 ? nameParts[0].substring(0, 50) : nameParts[0];
                    newCustomer.setHo(ho);
                    
                    if (nameParts.length > 1) {
                        String ten = String.join(" ", java.util.Arrays.copyOfRange(nameParts, 1, nameParts.length));
                        ten = ten.length() > 50 ? ten.substring(0, 50) : ten;
                        newCustomer.setTen(ten);
                    } else {
                        newCustomer.setTen("User"); // Tên mặc định
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
                newCustomer.setMatKhauHash("GOOGLE_OAUTH"); // Đánh dấu tài khoản Google OAuth
                newCustomer.setDiemTichLuy(0); // Đảm bảo điểm tích lũy được set
                
                // Sinh mã khách hàng tự động với retry logic
                String customerCode = generateUniqueCustomerCode();
                newCustomer.setMaKhachHang(customerCode);
                
                // Set UUID và timestamps (sẽ được set trong @PrePersist nhưng đảm bảo)
                newCustomer.setUuidId(java.util.UUID.randomUUID());
                long currentTime = System.currentTimeMillis();
                newCustomer.setCreatedDate(currentTime);
                newCustomer.setLastModifiedDate(currentTime);
                
                // Log chi tiết để debug
                // Log customer creation without sensitive information
                System.out.println("[INFO] Creating new Google OAuth customer with ID: " + newCustomer.getMaKhachHang());
                System.out.println("[INFO] Customer status: " + newCustomer.getTrangThai());
                
                // Validate before save
                validateCustomerBeforeSave(newCustomer);
                
                Customer savedCustomer = customerRepository.save(newCustomer);
                System.out.println("[INFO] Customer saved successfully with ID: " + savedCustomer.getId());
                
            } catch (Exception e) {
                // Log error without sensitive information
                System.err.println("[ERROR] Failed to create Google OAuth customer account");
                System.err.println("[ERROR] Exception type: " + e.getClass().getSimpleName());
                // Only log stack trace in development environment
                // e.printStackTrace(); // Comment out for production
                throw new RuntimeException("Failed to create customer account. Please contact support.", e);
            }

            return new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority("ROLE_KHACHHANG")),
                    user.getAttributes(),
                    "email"
            );
        };
    }

    // Thêm hàm sinh mã khách hàng tự động với retry logic
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
    
    // Validate customer before save
    private void validateCustomerBeforeSave(Customer customer) {
        if (customer.getEmail() == null || customer.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (customer.getHo() == null || customer.getHo().trim().isEmpty()) {
            throw new IllegalArgumentException("Ho (first name) is required");
        }
        if (customer.getTen() == null || customer.getTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Ten (last name) is required");
        }
        if (customer.getMaKhachHang() == null || customer.getMaKhachHang().trim().isEmpty()) {
            throw new IllegalArgumentException("MaKhachHang (customer code) is required");
        }
        if (customer.getMatKhauHash() == null || customer.getMatKhauHash().trim().isEmpty()) {
            throw new IllegalArgumentException("MatKhauHash (password hash) is required");
        }
        if (customer.getTrangThai() == null) {
            throw new IllegalArgumentException("TrangThai (status) is required");
        }
        
        // Check length constraints
        if (customer.getMaKhachHang().length() > 20) {
            throw new IllegalArgumentException("MaKhachHang too long (max 20 characters)");
        }
        if (customer.getHo().length() > 50) {
            throw new IllegalArgumentException("Ho too long (max 50 characters)");
        }
        if (customer.getTen().length() > 50) {
            throw new IllegalArgumentException("Ten too long (max 50 characters)");
        }
        if (customer.getEmail().length() > 100) {
            throw new IllegalArgumentException("Email too long (max 100 characters)");
        }
        if (customer.getMatKhauHash().length() > 255) {
            throw new IllegalArgumentException("MatKhauHash too long (max 255 characters)");
        }
    }
}