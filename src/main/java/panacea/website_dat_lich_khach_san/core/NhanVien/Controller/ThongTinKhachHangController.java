package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import panacea.website_dat_lich_khach_san.core.NhanVien.Service.ThongTinKhachHangService;

@Controller
@RequestMapping("/nhanvien/thongtinkhachhang")
public class ThongTinKhachHangController {
    private final ThongTinKhachHangService thongTinKhachHangService;
    public ThongTinKhachHangController(ThongTinKhachHangService thongTinKhachHangService) {
        this.thongTinKhachHangService = thongTinKhachHangService;
    }
    @GetMapping("")
    public String view(Model model) {
        java.util.List<panacea.website_dat_lich_khach_san.entity.Customer> dsKhachHang;
        try {
            dsKhachHang = thongTinKhachHangService.getAllCustomers();
            if (dsKhachHang == null) dsKhachHang = new java.util.ArrayList<>();
        } catch (Exception e) {
            dsKhachHang = new java.util.ArrayList<>();
        }
        model.addAttribute("dsKhachHang", dsKhachHang);
        model.addAttribute("staffName", thongTinKhachHangService.getStaffName());
        model.addAttribute("preferences", thongTinKhachHangService.getAllPreferences());
        model.addAttribute("gioiTinhs", java.util.List.of("Nam", "Nữ", "Khác"));
        model.addAttribute("loaiPhongList", java.util.List.of("Standard", "Deluxe", "Suite"));
        model.addAttribute("tangList", java.util.List.of("Thấp", "Trung bình", "Cao"));
        model.addAttribute("hutThuocList", java.util.List.of("Không hút thuốc", "Hút thuốc"));
        return "NhanVien/ThongTinKhachHang";
    }
} 