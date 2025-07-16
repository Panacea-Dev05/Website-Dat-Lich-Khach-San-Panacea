package panacea.website_dat_lich_khach_san.infrastructure.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
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
public class SecurityConfig {
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private CustomerRepository customerRepository;

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
                .logout(logout -> logout.logoutSuccessUrl("/login?logout"));
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
            Customer newCustomer = new Customer();
            newCustomer.setEmail(email);
            newCustomer.setHo(name != null ? name.split(" ")[0] : "");
            newCustomer.setTen(name != null && name.split(" ").length > 1 ? name.substring(name.indexOf(" ") + 1) : "");
            newCustomer.setTrangThai(Customer.TrangThaiCustomer.HOAT_DONG);
            newCustomer.setLoaiKhachHang("CA_NHAN");
            newCustomer.setMatKhauHash(""); // Không cần mật khẩu cho Google login
            // Sinh mã khách hàng tự động
            newCustomer.setMaKhachHang(generateCustomerCode());
            // Log giá trị trạng thái để debug
            System.out.println("[DEBUG] TrangThai insert: " + newCustomer.getTrangThai() + " - DB value: " + newCustomer.getTrangThai().getValue());
            customerRepository.save(newCustomer);

            return new DefaultOAuth2User(
                    List.of(new SimpleGrantedAuthority("ROLE_KHACHHANG")),
                    user.getAttributes(),
                    "email"
            );
        };
    }

    // Thêm hàm sinh mã khách hàng tự động
    private String generateCustomerCode() {
        return "KH" + System.currentTimeMillis();
    }
}