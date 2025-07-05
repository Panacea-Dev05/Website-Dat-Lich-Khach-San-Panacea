
package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.QuanLyKhoService;

@Controller
@RequestMapping("/nhanvien/quanlykho")
public class QuanLyKhoController {
    private final QuanLyKhoService quanLyKhoService;
    public QuanLyKhoController(QuanLyKhoService quanLyKhoService) {
        this.quanLyKhoService = quanLyKhoService;
    }
    @GetMapping("")
    public String view(Model model) {
        model.addAttribute("staffName", quanLyKhoService.getStaffName());
        model.addAttribute("items", quanLyKhoService.getAllItems());
        model.addAttribute("transactions", quanLyKhoService.getAllTransactions());
        return "NhanVien/QuanLyKho";
    }
}
