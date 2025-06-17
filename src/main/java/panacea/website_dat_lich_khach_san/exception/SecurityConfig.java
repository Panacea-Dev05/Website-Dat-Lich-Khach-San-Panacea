// package panacea.website_dat_lich_khach_san.exception;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
// import org.springframework.security.core.authority.SimpleGrantedAuthority;
// import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
// import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
// import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
// import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
// import org.springframework.security.oauth2.core.user.OAuth2User;
// import org.springframework.security.web.SecurityFilterChain;
// import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
// import panacea.website_dat_lich_khach_san.entity.Staff;
// import panacea.website_dat_lich_khach_san.repository.StaffRepository;

// import java.util.Collections;
// import java.util.List;
// import java.util.Optional;

// @Configuration
// @EnableWebSecurity
// public class SecurityConfig {

//     @Autowired
//     private StaffRepository nhanVienRepository;

//     @Bean
//     public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//         http
//                 .authorizeHttpRequests(auth -> auth
//                         .requestMatchers("/css/**", "/js/**", "/images/**", "/login", "/oauth2/**").permitAll()
//                         .requestMatchers("/admin/**").hasRole("ADMIN")
//                         .requestMatchers("/chukhachsan/**").hasRole("HOTEL_OWNER")
//                         .requestMatchers("/nhanvien/**").hasRole("EMPLOYEE")
//                         .requestMatchers("/khachhang/**").hasRole("CUSTOMER")
//                         .anyRequest().authenticated()
//                 )
//                 .oauth2Login(oauth2 -> oauth2
//                         .loginPage("/login")
//                         .userInfoEndpoint(userInfo -> userInfo
//                                 .userService(oauth2UserService())
//                         )
//                         .successHandler(authenticationSuccessHandler())
//                         .defaultSuccessUrl("/home", true)
//                 )
//                 .logout(logout -> logout
//                         .logoutSuccessUrl("/login")
//                         .permitAll()
//                 )
//                 .headers(headers ->
//                         headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
//                 )
//                 .csrf(csrf -> csrf.disable());

//         return http.build();
//     }

//     @Bean
//     public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
//         DefaultOAuth2UserService delegate = new DefaultOAuth2UserService();
//         return request -> {
//             OAuth2User user = delegate.loadUser(request);
//             String email = user.getAttribute("email");

//             Optional<Staff> nhanVienOpt = nhanVienRepository.findByEmail(email);
//             if (nhanVienOpt.isEmpty()) {
//                 // Nếu không tìm thấy trong DB, không cho truy cập hệ thống
//                 return new DefaultOAuth2User(Collections.emptyList(), user.getAttributes(), "email");
//             }

//             Staff nhanVien = nhanVienOpt.get();
//             String chucVu = nhanVien.getChucVu().toLowerCase(); // ví dụ: admin, chukhachsan

//             List<SimpleGrantedAuthority> authorities = switch (chucVu) {
//                 case "admin" -> List.of(new SimpleGrantedAuthority("ROLE_ADMIN"));
//                 case "chukhachsan" -> List.of(new SimpleGrantedAuthority("ROLE_HOTEL_OWNER"));
//                 case "nhanvien" -> List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"));
//                 case "khachhang" -> List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER"));
//                 default -> Collections.emptyList();
//             };

//             return new DefaultOAuth2User(authorities, user.getAttributes(), "email");
//         };
//     }

//     @Bean
//     public AuthenticationSuccessHandler authenticationSuccessHandler() {
//         return (request, response, authentication) -> {
//             // Bạn có thể redirect theo role ở đây nếu cần
//             response.sendRedirect("/home"); // chuyển hướng sau đăng nhập
//         };
//     }
// }
