package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.QuanLyDatPhongService;

@Controller
@RequestMapping("/nhanvien/quanlydatphong")
public class QuanLyDatPhongController {
    private final QuanLyDatPhongService quanLyDatPhongService;
    public QuanLyDatPhongController(QuanLyDatPhongService quanLyDatPhongService) {
        this.quanLyDatPhongService = quanLyDatPhongService;
    }
    @GetMapping("")
    public String view(Model model) {
        model.addAttribute("staffName", quanLyDatPhongService.getStaffName());
        model.addAttribute("bookings", quanLyDatPhongService.getAllBookings());
        return "NhanVien/QuanLyDatPhong";
    }
} 