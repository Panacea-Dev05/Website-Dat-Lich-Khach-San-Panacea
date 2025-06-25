package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/system")
public class AdminSystemController {
    
    @GetMapping
    public String systemSettings(Model model) {
        // Thêm các thông tin cài đặt hệ thống vào model
        model.addAttribute("systemName", "Panacea Hotel Management System");
        model.addAttribute("version", "1.0.0");
        model.addAttribute("maintenanceMode", false);
        return "Admin/view/CaiDatHeThong";
    }
    
    @PostMapping("/maintenance")
    @ResponseBody
    public String toggleMaintenanceMode(@RequestParam boolean enabled) {
        // Logic để bật/tắt chế độ bảo trì
        return "Maintenance mode " + (enabled ? "enabled" : "disabled");
    }
    
    @PostMapping("/backup")
    @ResponseBody
    public String createBackup() {
        // Logic để tạo backup
        return "Backup created successfully";
    }
} 