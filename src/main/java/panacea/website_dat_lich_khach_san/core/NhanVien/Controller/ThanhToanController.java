
package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.ThanhToanService;

@Controller
@RequestMapping("/nhanvien/thanhtoan")
public class ThanhToanController {
    private final ThanhToanService thanhToanService;
    public ThanhToanController(ThanhToanService thanhToanService) {
        this.thanhToanService = thanhToanService;
    }
    @GetMapping("")
    public String view(Model model) {
        model.addAttribute("staffName", thanhToanService.getStaffName());
        model.addAttribute("payments", thanhToanService.getAllPayments());
        return "NhanVien/ThanhToan";
    }
}
