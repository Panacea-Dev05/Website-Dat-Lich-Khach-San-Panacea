package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.SupplyRequestService;
import panacea.website_dat_lich_khach_san.entity.SupplyRequest;
import panacea.website_dat_lich_khach_san.entity.Staff;
import panacea.website_dat_lich_khach_san.infrastructure.Enums.TrangThaiYeuCau;
import panacea.website_dat_lich_khach_san.repository.StaffRepository;
import panacea.website_dat_lich_khach_san.core.NhanVien.DTO.SupplyRequestDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/nhanvien/supply-request")
public class SupplyRequestController {

    @Autowired
    private SupplyRequestService supplyRequestService;

    @Autowired
    private StaffRepository staffRepository;

    // Trang quản lý yêu cầu bổ sung
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    @GetMapping("/view")
    @Transactional
    public String view(Model model, Authentication authentication) {
        String username = authentication.getName();
        Optional<Staff> staff = staffRepository.findByTaiKhoan(username);
        
        if (staff.isPresent()) {
            Staff currentStaff = staff.get();
            List<SupplyRequest> requests;
            
            // Admin xem tất cả, Staff chỉ xem của mình
            if (authentication.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                requests = supplyRequestService.getAllRequests();
                model.addAttribute("isAdmin", true);
                model.addAttribute("pendingRequests", supplyRequestService.getPendingRequests());
            } else {
                requests = supplyRequestService.getRequestsByStaff(currentStaff.getId());
                model.addAttribute("isAdmin", false);
            }
            
            model.addAttribute("requests", requests);
            model.addAttribute("currentStaff", currentStaff);
        }
        
        return "NhanVien/SupplyRequest";
    }

    // Tạo yêu cầu bổ sung (Staff)
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    @PostMapping("/api/create")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createRequest(@RequestBody SupplyRequest request, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = authentication.getName();
            Optional<Staff> staff = staffRepository.findByTaiKhoan(username);
            
            if (staff.isEmpty()) {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin nhân viên!");
                return ResponseEntity.badRequest().body(response);
            }
            
            SupplyRequest savedRequest = supplyRequestService.createSupplyRequest(request, staff.get().getId());
            response.put("success", true);
            response.put("message", "Yêu cầu bổ sung đã được tạo thành công!");
            response.put("request", savedRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Phê duyệt yêu cầu (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/approve/{id}")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> approveRequest(@PathVariable Integer id, 
                                                             @RequestBody Map<String, String> requestBody,
                                                             Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = authentication.getName();
            Optional<Staff> admin = staffRepository.findByTaiKhoan(username);
            
            if (admin.isEmpty()) {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin admin!");
                return ResponseEntity.badRequest().body(response);
            }
            
            String ghiChu = requestBody.get("ghiChu");
            SupplyRequest approvedRequest = supplyRequestService.approveRequest(id, admin.get().getId(), ghiChu);
            
            response.put("success", true);
            response.put("message", "Yêu cầu đã được phê duyệt!");
            response.put("request", approvedRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Từ chối yêu cầu (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/reject/{id}")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> rejectRequest(@PathVariable Integer id, 
                                                            @RequestBody Map<String, String> requestBody,
                                                            Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = authentication.getName();
            Optional<Staff> admin = staffRepository.findByTaiKhoan(username);
            
            if (admin.isEmpty()) {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin admin!");
                return ResponseEntity.badRequest().body(response);
            }
            
            String lyDoTuChoi = requestBody.get("lyDoTuChoi");
            SupplyRequest rejectedRequest = supplyRequestService.rejectRequest(id, admin.get().getId(), lyDoTuChoi);
            
            response.put("success", true);
            response.put("message", "Yêu cầu đã bị từ chối!");
            response.put("request", rejectedRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Đánh dấu hoàn thành (Admin only)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/api/complete/{id}")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> completeRequest(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            SupplyRequest completedRequest = supplyRequestService.markAsCompleted(id);
            response.put("success", true);
            response.put("message", "Yêu cầu đã được đánh dấu hoàn thành!");
            response.put("request", completedRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Lấy danh sách yêu cầu theo trạng thái
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/api/status/{status}")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> getRequestsByStatus(@PathVariable String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            TrangThaiYeuCau trangThai = TrangThaiYeuCau.valueOf(status.toUpperCase());
            List<SupplyRequestDTO> requests = supplyRequestService.getRequestsByStatusDTO(trangThai);
            response.put("success", true);
            response.put("requests", requests);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Lấy yêu cầu của nhân viên hiện tại
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    @GetMapping("/api/my-requests")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMyRequests(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            String username = authentication.getName();
            Optional<Staff> staff = staffRepository.findByTaiKhoan(username);
            
            if (staff.isEmpty()) {
                response.put("success", false);
                response.put("message", "Không tìm thấy thông tin nhân viên!");
                return ResponseEntity.badRequest().body(response);
            }
            
            List<SupplyRequestDTO> requests = supplyRequestService.getRequestsByStaffDTO(staff.get().getId());
            response.put("success", true);
            response.put("requests", requests);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}