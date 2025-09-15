package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.entity.InventoryManagement;
import panacea.website_dat_lich_khach_san.entity.InventoryTransaction;
import panacea.website_dat_lich_khach_san.entity.Staff;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.QuanLyKhoService;
import panacea.website_dat_lich_khach_san.infrastructure.Enums.LoaiGiaoDich;
import panacea.website_dat_lich_khach_san.repository.StaffRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin/inventory")
@PreAuthorize("hasRole('ADMIN')")
public class AdminInventoryController {

    @Autowired
    private QuanLyKhoService quanLyKhoService;
    
    @Autowired
    private StaffRepository staffRepository;

    @GetMapping
    public String inventoryManagement(Model model) {
        try {
            List<InventoryManagement> items = quanLyKhoService.getAllItems();
            List<InventoryTransaction> transactions = quanLyKhoService.getAllTransactions();
            
            // Calculate statistics
            int totalItems = items.size();
            int lowStockItems = (int) items.stream()
                .filter(item -> item.getSoLuongTon() != null && item.getSoLuongTon() < 15)
                .count();
            int outOfStockItems = (int) items.stream()
                .filter(item -> item.getSoLuongTon() == 0)
                .count();
            double totalValue = items.stream()
                .mapToDouble(item -> item.getSoLuongTon() * (item.getGiaNhap() != null ? item.getGiaNhap().doubleValue() : 0))
                .sum();
            
            model.addAttribute("items", items);
            model.addAttribute("transactions", transactions);
            model.addAttribute("totalItems", totalItems);
            model.addAttribute("lowStockItems", lowStockItems);
            model.addAttribute("outOfStockItems", outOfStockItems);
            model.addAttribute("totalValue", totalValue);
            
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
        
        return "Admin/view/QuanLyKho";
    }

    @PostMapping("/items")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createItem(@RequestBody InventoryManagement item) {
        Map<String, Object> response = new HashMap<>();
        try {
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
            response.put("message", "Tạo vật phẩm thành công");
            response.put("data", savedItem);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi tạo vật phẩm: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/items/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateItem(@PathVariable Integer id, @RequestBody InventoryManagement item) {
        Map<String, Object> response = new HashMap<>();
        try {
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

    @DeleteMapping("/items/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteItem(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
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

    @GetMapping("/items/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getItem(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<InventoryManagement> allItems = quanLyKhoService.getAllItems();
            InventoryManagement item = allItems.stream()
                .filter(i -> i.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vật phẩm với ID: " + id));
            response.put("success", true);
            response.put("data", item);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Không tìm thấy vật phẩm: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/transactions")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> createTransaction(@RequestBody InventoryTransaction transaction, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Set transaction creator (Admin ID)
            if (authentication != null && authentication.isAuthenticated()) {
                try {
                    String username = authentication.getName();
                    Optional<Staff> admin = staffRepository.findByTaiKhoan(username);
                    
                    if (admin.isEmpty()) {
                        throw new RuntimeException("Cannot determine admin ID from authentication");
                    }
                    
                    Integer adminId = admin.get().getId();
                    transaction.setNhanVienId(adminId);
                } catch (Exception e) {
                    throw new RuntimeException("Cannot determine admin ID from authentication");
                }
            }
            
            // Admin always auto-approves their transactions
            InventoryTransaction savedTransaction = quanLyKhoService.saveTransaction(transaction, true);
            response.put("success", true);
            if (transaction.getLoaiGiaoDich() == LoaiGiaoDich.NHAP_KHO) {
                response.put("message", "Thực hiện giao dịch thành công và đã được tự động phê duyệt");
            } else {
                response.put("message", "Thực hiện giao dịch thành công");
            }
            response.put("data", savedTransaction);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi thực hiện giao dịch: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }



    @GetMapping("/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchItems(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String status) {
        Map<String, Object> response = new HashMap<>();
        try {
            List<InventoryManagement> items = quanLyKhoService.searchItems(keyword, category);
            response.put("success", true);
            response.put("data", items);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi tìm kiếm: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/reports/inventory")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getInventoryReport() {
        Map<String, Object> response = new HashMap<>();
        try {
            Map<String, Object> report = quanLyKhoService.generateInventoryReport();
            response.put("success", true);
            response.put("data", report);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi tạo báo cáo: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/reports/transactions")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getTransactionReport(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        Map<String, Object> response = new HashMap<>();
        try {
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

    @GetMapping("/export/items")
    public ResponseEntity<byte[]> exportItems() {
        try {
            // Export functionality not implemented in service
            byte[] excelData = new byte[0]; // Placeholder
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=inventory_items.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/export/transactions")
    public ResponseEntity<byte[]> exportTransactions(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            // Export functionality not implemented in service
            byte[] excelData = new byte[0]; // Placeholder
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=inventory_transactions.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excelData);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/low-stock")
    @ResponseBody
    public ResponseEntity<?> getLowStockItems() {
        try {
            List<InventoryManagement> lowStockItems = quanLyKhoService.getLowStockItems();
            int count = quanLyKhoService.getLowStockItemsCount();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("items", lowStockItems);
            response.put("count", count);
            response.put("message", count > 0 ? 
                String.format("Có %d vật phẩm sắp hết hàng (tồn kho dưới 15)", count) : 
                "Không có vật phẩm nào sắp hết hàng");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", true, 
                "message", "Lỗi khi tải danh sách vật phẩm sắp hết hàng: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/expired")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getExpiredItems() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Filter expired items from all items
            List<InventoryManagement> allItems = quanLyKhoService.getAllItems();
            java.time.LocalDate today = java.time.LocalDate.now();
            List<InventoryManagement> expiredItems = allItems.stream()
                .filter(item -> item.getHanSuDung() != null && item.getHanSuDung().isBefore(today))
                .collect(java.util.stream.Collectors.toList());
            response.put("success", true);
            response.put("data", expiredItems);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi khi lấy danh sách vật phẩm hết hạn: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}