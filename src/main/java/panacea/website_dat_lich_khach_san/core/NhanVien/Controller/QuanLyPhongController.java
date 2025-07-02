package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.QuanLyPhongService;

@Controller
@RequestMapping("/nhanvien/quanlyphong")
public class QuanLyPhongController {
    private final QuanLyPhongService quanLyPhongService;
    public QuanLyPhongController(QuanLyPhongService quanLyPhongService) {
        this.quanLyPhongService = quanLyPhongService;
    }
    @GetMapping("")
    public String view(Model model) {
        model.addAttribute("staffName", quanLyPhongService.getStaffName());
        model.addAttribute("rooms", quanLyPhongService.getAllRooms());
        return "NhanVien/QuanLyPhong";
    }
} 