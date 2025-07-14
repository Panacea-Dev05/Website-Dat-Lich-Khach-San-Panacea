package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminStaffService;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.StaffDTO;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/staff")
public class AdminStaffController {
    
    @Autowired
    private AdminStaffService adminStaffService;
    
    @GetMapping
    public String staffManagement(Model model) {
        try {
            List<StaffDTO> staffList = adminStaffService.getAllStaff();
            List<String> staffRoles = Arrays.asList(
                "Nhân viên lễ tân", 
                "Nhân viên buồng phòng", 
                "Nhân viên bảo trì", 
                "Quản lý"
            );
            List<String> staffStatuses = Arrays.asList("HOAT_DONG", "NGHI_VIEC");
            
            model.addAttribute("staffList", staffList);
            model.addAttribute("staffRoles", staffRoles);
            model.addAttribute("staffStatuses", staffStatuses);
            
            return "Admin/view/QuanLyNhanVien";
        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải danh sách nhân viên: " + e.getMessage());
            return "Admin/view/QuanLyNhanVien";
        }
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public ResponseEntity<StaffDTO> getStaff(@PathVariable Integer id) {
        try {
            StaffDTO staff = adminStaffService.getStaffById(id);
            if (staff != null) {
                return ResponseEntity.ok(staff);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    @ResponseBody
    public ResponseEntity<StaffDTO> createStaff(@RequestBody StaffDTO staffDTO) {
        try {
            // Kiểm tra mã nhân viên đã tồn tại
            if (adminStaffService.existsByMaNhanVien(staffDTO.getMaNhanVien())) {
                return ResponseEntity.badRequest().build();
            }
            // Kiểm tra email đã tồn tại
            if (adminStaffService.existsByEmail(staffDTO.getEmail())) {
                return ResponseEntity.badRequest().build();
            }
            StaffDTO createdStaff = adminStaffService.createStaff(staffDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdStaff);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<StaffDTO> updateStaff(@PathVariable Integer id, @RequestBody StaffDTO staffDTO) {
        try {
            // Kiểm tra mã nhân viên đã tồn tại (trừ nhân viên hiện tại)
            if (adminStaffService.existsByMaNhanVienAndIdNot(staffDTO.getMaNhanVien(), id)) {
                return ResponseEntity.badRequest().build();
            }
            // Kiểm tra email đã tồn tại (trừ nhân viên hiện tại)
            if (adminStaffService.existsByEmailAndIdNot(staffDTO.getEmail(), id)) {
                return ResponseEntity.badRequest().build();
            }
            StaffDTO updatedStaff = adminStaffService.updateStaff(id, staffDTO);
            if (updatedStaff != null) {
                return ResponseEntity.ok(updatedStaff);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Boolean> deleteStaff(@PathVariable Integer id) {
        try {
            boolean deleted = adminStaffService.deleteStaff(id);
            if (deleted) {
                return ResponseEntity.ok(true);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping("/{id}/deactivate")
    @ResponseBody
    public ResponseEntity<StaffDTO> deactivateStaff(@PathVariable Integer id) {
        try {
            StaffDTO deactivatedStaff = adminStaffService.deactivateStaff(id);
            if (deactivatedStaff != null) {
                return ResponseEntity.ok(deactivatedStaff);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<List<StaffDTO>> searchStaff(@RequestParam String keyword) {
        try {
            List<StaffDTO> staffList = adminStaffService.searchStaff(keyword);
            return ResponseEntity.ok(staffList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}