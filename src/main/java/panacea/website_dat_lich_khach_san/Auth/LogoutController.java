package panacea.website_dat_lich_khach_san.Auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Controller xử lý đăng xuất tùy chỉnh
@Controller
public class LogoutController {
    
    @GetMapping("/custom-logout")
    public String customLogout(HttpServletRequest request, HttpServletResponse response, 
                              Authentication authentication,
                              @RequestParam(value = "returnUrl", defaultValue = "/khachhang") String returnUrl) {
        
        // Thực hiện logout
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        
        // Chuyển hướng về trang được chỉ định hoặc trang chủ khách hàng
        return "redirect:" + returnUrl;
    }
}