package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import panacea.website_dat_lich_khach_san.core.NhanVien.Service.HomeService;

@Controller
@RequestMapping("/nhanvien/trangchu")
public class HomeController {
    private final HomeService homeService;
    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }
    @GetMapping("")
    public String home(Model model) {
        // Truyền tên nhân viên (có thể lấy từ service hoặc hardcode)
        model.addAttribute("staffName", homeService != null ? homeService.getStaffName() : "Nguyễn Văn A");
        model.addAttribute("overviewList", homeService != null ? homeService.getOverviewList() : new java.util.ArrayList<>());
        return "NhanVien/Home";
    }
} 