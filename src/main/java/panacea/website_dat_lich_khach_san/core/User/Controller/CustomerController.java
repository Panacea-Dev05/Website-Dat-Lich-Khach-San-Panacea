package panacea.website_dat_lich_khach_san.core.User.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/khachhang")
public class CustomerController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "KhachHangDashboard";
    }
} 