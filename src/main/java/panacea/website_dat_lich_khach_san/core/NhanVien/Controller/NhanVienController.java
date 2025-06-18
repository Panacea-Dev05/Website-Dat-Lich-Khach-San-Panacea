package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/nhanvien")
public class NhanVienController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "NhanVienDashboard";
    }
} 