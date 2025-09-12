package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.QuanLyKhoService;
import panacea.website_dat_lich_khach_san.entity.InventoryManagement;
import panacea.website_dat_lich_khach_san.entity.InventoryTransaction;
import panacea.website_dat_lich_khach_san.entity.Staff;
import panacea.website_dat_lich_khach_san.infrastructure.Enums.LoaiGiaoDich;
import panacea.website_dat_lich_khach_san.repository.StaffRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Optional;

@Controller
@RequestMapping("/nhanvien/quanlykho")
@PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
public class QuanLyKhoController {
    private final QuanLyKhoService quanLyKhoService;
    
    @Autowired
    private StaffRepository staffRepository;
    
    public QuanLyKhoController(QuanLyKhoService quanLyKhoService) {
        this.quanLyKhoService = quanLyKhoService;
    }
    
    @GetMapping("")
    public String view(Model model, Authentication authentication) {
        try {
            // Verify user authentication
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/login";
            }
            
            String staffName = authentication.getName();
            model.addAttribute("staffName", staffName);
            model.addAttribute("items", quanLyKhoService.getAllItems());
            model.addAttribute("transactions", quanLyKhoService.getAllTransactions());
            model.addAttribute("vatPhams", quanLyKhoService.getAllItems());
            model.addAttribute("loaiGiaoDichList", Arrays.asList(LoaiGiaoDich.values()));
            
            // Add status list for items
            List<String> trangThaiList = Arrays.asList("Hoạt động", "Tạm ngưng", "Hết hàng");
            model.addAttribute("trangThaiList", trangThaiList);
            
            return "NhanVien/QuanLyKho";
        } catch (Exception e) {
            // Log the error
            System.err.println("Error in QuanLyKho view: " + e.getMessage());
            e.printStackTrace();
            
            // Add error message to model
            model.addAttribute("error", "Có lỗi xảy ra khi tải dữ liệu: " + e.getMessage());
            model.addAttribute("items", new java.util.ArrayList<>());
            model.addAttribute("transactions", new java.util.ArrayList<>());
            model.addAttribute("vatPhams", new java.util.ArrayList<>());
            model.addAttribute("loaiGiaoDichList", Arrays.asList(LoaiGiaoDich.values()));
            model.addAttribute("trangThaiList", Arrays.asList("Hoạt động", "Tạm ngưng", "Hết hàng"));
            
            return "NhanVien/QuanLyKho";
        }
    }
    
    // API endpoints cho CRUD operations
    
    @GetMapping("/api/items")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllItems() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<InventoryManagement> items = quanLyKhoService.getAllItems();
            response.put("success", true);
            response.put("items", items);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi tải danh sách vật phẩm: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/api/items")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createItem(@RequestBody InventoryManagement item, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Verify user authentication
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }
            
            // Validate input
            try {
                quanLyKhoService.validateItem(item);
            } catch (RuntimeException e) {
                response.put("success", false);
                response.put("message", e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
            InventoryManagement savedItem = quanLyKhoService.saveItem(item);
            response.put("success", true);
            response.put("message", "Thêm vật phẩm thành công");
            response.put("data", savedItem);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi thêm vật phẩm: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/api/items/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> updateItem(@PathVariable Integer id, @RequestBody InventoryManagement item, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Verify user authentication
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }
            
            // Validate input
            try {
                quanLyKhoService.validateItem(item);
            } catch (RuntimeException e) {
                response.put("success", false);
                response.put("message", e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
            item.setId(id);
            InventoryManagement updatedItem = quanLyKhoService.updateItem(item);
            response.put("success", true);
            response.put("message", "Cập nhật vật phẩm thành công");
            response.put("data", updatedItem);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi cập nhật vật phẩm: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/api/items/{id}")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteItem(@PathVariable Integer id, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Verify user authentication and admin role
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }
            
            boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            
            if (!hasAdminRole) {
                response.put("success", false);
                response.put("message", "Chỉ admin mới có quyền xóa vật phẩm");
                return ResponseEntity.status(403).body(response);
            }
            
            quanLyKhoService.deleteItem(id);
            response.put("success", true);
            response.put("message", "Xóa vật phẩm thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi xóa vật phẩm: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PostMapping("/api/transactions")
    @ResponseBody
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> createTransaction(@RequestBody InventoryTransaction transaction, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Verify user authentication
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }
            
            // Validate input
            try {
                quanLyKhoService.validateTransaction(transaction);
            } catch (RuntimeException e) {
                response.put("success", false);
                response.put("message", e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
            // Set transaction creator (using staff ID)
            // Look up staff ID from username
             try {
                 String username = authentication.getName();
                 Optional<Staff> staff = staffRepository.findByTaiKhoan(username);
                 
                 if (staff.isEmpty()) {
                     throw new RuntimeException("Cannot determine staff ID from authentication");
                 }
                 
                 Integer staffId = staff.get().getId();
                 transaction.setNhanVienId(staffId);
             } catch (Exception e) {
                 throw new RuntimeException("Cannot determine staff ID from authentication: " + e.getMessage());
             }
            
            // Check if user is Admin
            boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            
            InventoryTransaction savedTransaction = quanLyKhoService.saveTransaction(transaction, isAdmin);
            response.put("success", true);
            if (isAdmin && transaction.getLoaiGiaoDich() == LoaiGiaoDich.NHAP_KHO) {
                response.put("message", "Thêm giao dịch thành công và đã được tự động phê duyệt");
            } else {
                response.put("message", "Thêm giao dịch thành công");
            }
            response.put("data", savedTransaction);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi thêm giao dịch: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchItems(@RequestParam(required = false) String keyword,
                                                          @RequestParam(required = false) String loaiVatPham) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<InventoryManagement> items = quanLyKhoService.searchItems(keyword, loaiVatPham);
            response.put("success", true);
            response.put("data", items);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi tìm kiếm: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/api/reports/inventory")
    @ResponseBody
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getInventoryReport(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }
            
            Map<String, Object> report = quanLyKhoService.generateInventoryReport();
            response.put("success", true);
            response.put("data", report);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi tạo báo cáo tồn kho: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/api/reports/transactions")
    @ResponseBody
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getTransactionReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }
            
            Map<String, Object> report = quanLyKhoService.generateTransactionReport(startDate, endDate);
            response.put("success", true);
            response.put("data", report);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi tạo báo cáo giao dịch: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/api/reports/expiry")
    @ResponseBody
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getExpiryReport(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }
            
            Map<String, Object> report = quanLyKhoService.generateExpiryReport();
            response.put("success", true);
            response.put("data", report);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi tạo báo cáo hết hạn: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/api/reports/value")
    @ResponseBody
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getValueReport(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }
            
            Map<String, Object> report = quanLyKhoService.generateValueReport();
            response.put("success", true);
            response.put("data", report);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi tạo báo cáo giá trị: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/api/reports/summary")
    @ResponseBody
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSummaryStats(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Verify user authentication
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }
            
            Map<String, Object> stats = quanLyKhoService.generateSummaryStats();
            response.put("success", true);
            response.put("data", stats);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi tạo thống kê tổng hợp: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // API phê duyệt phiếu nhập kho (chỉ Admin)
    @PostMapping("/api/transactions/{id}/approve")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> approveTransaction(@PathVariable Integer id, 
                                                                 @RequestBody Map<String, String> request,
                                                                 Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Verify admin authentication
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }
            
            boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            
            if (!hasAdminRole) {
                response.put("success", false);
                response.put("message", "Chỉ admin mới có quyền phê duyệt phiếu nhập kho");
                return ResponseEntity.status(403).body(response);
            }
            
            // Get admin ID from username
             String username = authentication.getName();
             Optional<Staff> admin = staffRepository.findByTaiKhoan(username);
             
             if (admin.isEmpty()) {
                 throw new RuntimeException("Cannot determine admin ID from authentication");
             }
             
             Integer adminId = admin.get().getId();
            String ghiChu = request.get("ghiChu");
            
            InventoryTransaction approvedTransaction = quanLyKhoService.approveTransaction(id, adminId, ghiChu);
            response.put("success", true);
            response.put("message", "Phê duyệt phiếu nhập kho thành công");
            response.put("data", approvedTransaction);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi phê duyệt: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // API từ chối phiếu nhập kho (chỉ Admin)
    @PostMapping("/api/transactions/{id}/reject")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> rejectTransaction(@PathVariable Integer id, 
                                                               @RequestBody Map<String, String> request,
                                                               Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Verify admin authentication
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }
            
            boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
            
            if (!hasAdminRole) {
                response.put("success", false);
                response.put("message", "Chỉ admin mới có quyền từ chối phiếu nhập kho");
                return ResponseEntity.status(403).body(response);
            }
            
            // Get admin ID from username
             String username = authentication.getName();
             Optional<Staff> admin = staffRepository.findByTaiKhoan(username);
             
             if (admin.isEmpty()) {
                 throw new RuntimeException("Cannot determine admin ID from authentication");
             }
             
             Integer adminId = admin.get().getId();
            String ghiChu = request.get("ghiChu");
            
            InventoryTransaction rejectedTransaction = quanLyKhoService.rejectTransaction(id, adminId, ghiChu);
            response.put("success", true);
            response.put("message", "Từ chối phiếu nhập kho thành công");
            response.put("data", rejectedTransaction);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi từ chối: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // API lấy danh sách giao dịch chờ duyệt (chỉ Admin)
    @GetMapping("/api/transactions/pending")
    @ResponseBody
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPendingTransactions(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Verify admin authentication
            if (authentication == null || !authentication.isAuthenticated()) {
                response.put("success", false);
                response.put("message", "Unauthorized access");
                return ResponseEntity.status(401).body(response);
            }
            
            List<InventoryTransaction> pendingTransactions = quanLyKhoService.getPendingTransactions();
            response.put("success", true);
            response.put("data", pendingTransactions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lấy danh sách giao dịch chờ duyệt: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}