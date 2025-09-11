package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminStaffService;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.StaffDTO;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    public ResponseEntity<?> getStaff(@PathVariable Integer id) {
        try {
            StaffDTO staffDTO = adminStaffService.getStaffById(id);
            if (staffDTO == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Không tìm thấy nhân viên với ID: " + id));
            }
            return ResponseEntity.ok(staffDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Lỗi khi tải thông tin nhân viên: " + e.getMessage()));
        }
    }
    
    @PostMapping
    @ResponseBody
    public ResponseEntity<?> createStaff(@RequestBody StaffDTO staffDTO) {
        try {
            StaffDTO created = adminStaffService.createStaff(staffDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Lỗi khi tạo nhân viên: " + ex.getMessage()));
        }
    }
    
    @PutMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> updateStaff(@PathVariable Integer id, @RequestBody StaffDTO staffDTO) {
        try {
            StaffDTO updated = adminStaffService.updateStaff(id, staffDTO);
            if (updated == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Không tìm thấy nhân viên"));
            }
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Lỗi khi cập nhật nhân viên: " + ex.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public boolean deleteStaff(@PathVariable Integer id) {
        return adminStaffService.deleteStaff(id);
    }
}