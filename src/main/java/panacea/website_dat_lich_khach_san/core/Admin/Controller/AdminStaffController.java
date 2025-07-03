package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminStaffService;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.StaffDTO;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/staff")
public class AdminStaffController {
    
    @Autowired
    private AdminStaffService adminStaffService;
    
    @GetMapping
    public String staffManagement(Model model) {
        List<StaffDTO> staffList = adminStaffService.getAllStaff();
        List<String> staffRoles = Arrays.asList("Nhân viên lễ tân", "Nhân viên buồng phòng", "Nhân viên bảo trì", "Quản lý");
        List<String> staffStatuses = Arrays.asList("HOAT_DONG", "NGHI_VIEC");
        model.addAttribute("staffList", staffList);
        model.addAttribute("staffRoles", staffRoles);
        model.addAttribute("staffStatuses", staffStatuses);
        return "Admin/view/QuanLyNhanVien";
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public StaffDTO getStaff(@PathVariable Integer id) {
        return adminStaffService.getStaffById(id);
    }
    
    @PostMapping
    @ResponseBody
    public StaffDTO createStaff(@RequestBody StaffDTO staffDTO) {
        return adminStaffService.createStaff(staffDTO);
    }
    
    @PutMapping("/{id}")
    @ResponseBody
    public StaffDTO updateStaff(@PathVariable Integer id, @RequestBody StaffDTO staffDTO) {
        return adminStaffService.updateStaff(id, staffDTO);
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public boolean deleteStaff(@PathVariable Integer id) {
        return adminStaffService.deleteStaff(id);
    }
} 