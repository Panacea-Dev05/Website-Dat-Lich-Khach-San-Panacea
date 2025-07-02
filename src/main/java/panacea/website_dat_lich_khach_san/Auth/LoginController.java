package panacea.website_dat_lich_khach_san.Auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class LoginController {
    @GetMapping("/login")
    public ModelAndView login(@AuthenticationPrincipal OAuth2User principal) {
        if (principal != null) {
            String role = principal.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("");
            if (role.equals("ROLE_ADMIN")) return new ModelAndView("redirect:/admin/bookings");

            if (role.equals("ROLE_KHACHHANG")) return new ModelAndView("redirect:/khachhang");

            if (role.equals("ROLE_NHANVIEN")) return new ModelAndView("redirect:/nhanvien/trangchu");

        }
        return new ModelAndView("Login/login24");
    }
}
