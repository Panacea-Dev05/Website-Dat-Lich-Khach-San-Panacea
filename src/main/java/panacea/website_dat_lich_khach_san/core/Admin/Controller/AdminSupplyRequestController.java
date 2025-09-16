package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.SupplyRequestService;
import panacea.website_dat_lich_khach_san.entity.SupplyRequest;
import panacea.website_dat_lich_khach_san.infrastructure.Enums.TrangThaiYeuCau;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.QuanLyKhoService;
import panacea.website_dat_lich_khach_san.entity.InventoryManagement;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

// Controller quản lý yêu cầu cung cấp vật tư cho Admin
@Controller
@RequestMapping("/admin/supply-requests")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSupplyRequestController {
    
    @Autowired
    private SupplyRequestService supplyRequestService;
    
    @Autowired
    private QuanLyKhoService quanLyKhoService;
    
    @GetMapping
    @Transactional
    public String supplyRequestManagement(Model model) {
        List<SupplyRequest> allRequests = supplyRequestService.getAllRequests();
        List<SupplyRequest> pendingRequests = supplyRequestService.getPendingRequests();
        List<SupplyRequest> approvedRequests = supplyRequestService.getRequestsByStatus(TrangThaiYeuCau.DA_DUYET);
        List<SupplyRequest> rejectedRequests = supplyRequestService.getRequestsByStatus(TrangThaiYeuCau.TU_CHOI);
        List<SupplyRequest> completedRequests = supplyRequestService.getRequestsByStatus(TrangThaiYeuCau.DA_THUC_HIEN);
        List<InventoryManagement> inventoryItems = quanLyKhoService.getAllItems();
        
        model.addAttribute("allRequests", allRequests);
        model.addAttribute("pendingRequests", pendingRequests);
        model.addAttribute("approvedRequests", approvedRequests);
        model.addAttribute("rejectedRequests", rejectedRequests);
        model.addAttribute("completedRequests", completedRequests);
        model.addAttribute("inventoryItems", inventoryItems);
        model.addAttribute("statusList", Arrays.asList(TrangThaiYeuCau.values()));
        
        return "Admin/view/QuanLyYeuCauBoSung";
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    @Transactional
    public SupplyRequest getRequest(@PathVariable Integer id) {
        return supplyRequestService.getAllRequests().stream()
                .filter(request -> request.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    @PostMapping("/{id}/approve")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> approveRequest(
            @PathVariable Integer id,
            @RequestParam(required = false, defaultValue = "") String ghiChu) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            SupplyRequest approvedRequest = supplyRequestService.approveRequest(id, 1, ghiChu); // Admin ID = 1
            response.put("success", true);
            response.put("message", "Yêu cầu đã được phê duyệt thành công");
            response.put("request", approvedRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi phê duyệt yêu cầu: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/{id}/reject")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> rejectRequest(
            @PathVariable Integer id,
            @RequestParam String lyDoTuChoi) {
        
        Map<String, Object> response = new HashMap<>();
        try {
            SupplyRequest rejectedRequest = supplyRequestService.rejectRequest(id, 1, lyDoTuChoi); // Admin ID = 1
            response.put("success", true);
            response.put("message", "Yêu cầu đã bị từ chối");
            response.put("request", rejectedRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi từ chối yêu cầu: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/{id}/complete")
    @ResponseBody
    @Transactional
    public ResponseEntity<Map<String, Object>> markAsCompleted(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            SupplyRequest completedRequest = supplyRequestService.markAsCompleted(id);
            response.put("success", true);
            response.put("message", "Yêu cầu đã được đánh dấu hoàn thành");
            response.put("request", completedRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi đánh dấu hoàn thành: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/status/{status}")
    @ResponseBody
    @Transactional
    public List<SupplyRequest> getRequestsByStatus(@PathVariable TrangThaiYeuCau status) {
        return supplyRequestService.getRequestsByStatus(status);
    }
    
    @GetMapping("/statistics")
    @ResponseBody
    @Transactional
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        List<SupplyRequest> allRequests = supplyRequestService.getAllRequests();
        
        stats.put("total", allRequests.size());
        stats.put("pending", supplyRequestService.getPendingRequests().size());
        stats.put("approved", supplyRequestService.getRequestsByStatus(TrangThaiYeuCau.DA_DUYET).size());
        stats.put("rejected", supplyRequestService.getRequestsByStatus(TrangThaiYeuCau.TU_CHOI).size());
        stats.put("completed", supplyRequestService.getRequestsByStatus(TrangThaiYeuCau.DA_THUC_HIEN).size());
        
        return stats;
    }
}