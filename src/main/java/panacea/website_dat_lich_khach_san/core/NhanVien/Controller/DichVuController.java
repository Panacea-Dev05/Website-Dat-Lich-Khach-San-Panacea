
package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.DichVuService;

@Controller
@RequestMapping("/nhanvien/dichvu")
public class DichVuController {
    private final DichVuService dichVuService;
    public DichVuController(DichVuService dichVuService) {
        this.dichVuService = dichVuService;
    }
    @GetMapping("")
    public String view(Model model) {
        model.addAttribute("staffName", dichVuService.getStaffName());
        model.addAttribute("serviceDetails", dichVuService.getAllServiceDetails());
        return "NhanVien/DichVu";
    }
}
