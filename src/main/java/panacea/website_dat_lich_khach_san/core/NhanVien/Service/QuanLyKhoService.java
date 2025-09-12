package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.InventoryManagement;
import panacea.website_dat_lich_khach_san.entity.InventoryTransaction;
import panacea.website_dat_lich_khach_san.repository.InventoryManagementRepository;
import panacea.website_dat_lich_khach_san.repository.InventoryTransactionRepository;
import panacea.website_dat_lich_khach_san.infrastructure.Enums.LoaiGiaoDich;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

@Service
public class QuanLyKhoService {
    @Autowired
    private InventoryManagementRepository inventoryManagementRepository;
    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    public String getStaffName() {
        return "Nguyễn Văn A";
    }

    public List<InventoryManagement> getAllItems() {
        try {
            return inventoryManagementRepository.findAll();
        } catch (Exception e) {
            // Log the error and return empty list to prevent 500 error
            System.err.println("Error loading items: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<InventoryTransaction> getAllTransactions() {
        try {
            return inventoryTransactionRepository.findAllWithInventoryItem();
        } catch (Exception e) {
            // Log the error and return empty list to prevent 500 error
            System.err.println("Error loading transactions: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    // CRUD operations cho InventoryManagement
    public InventoryManagement saveItem(InventoryManagement item) {
        validateItem(item);
        return inventoryManagementRepository.save(item);
    }
    
    public InventoryManagement updateItem(InventoryManagement item) {
        validateItem(item);
        Optional<InventoryManagement> existingItem = inventoryManagementRepository.findById(item.getId());
        if (existingItem.isEmpty()) {
            throw new RuntimeException("Không tìm thấy vật phẩm với ID: " + item.getId());
        }
        return inventoryManagementRepository.save(item);
    }
    
    public void deleteItem(Integer id) {
        Optional<InventoryManagement> existingItem = inventoryManagementRepository.findById(id);
        if (existingItem.isEmpty()) {
            throw new RuntimeException("Không tìm thấy vật phẩm với ID: " + id);
        }
        inventoryManagementRepository.deleteById(id);
    }
    
    public InventoryTransaction saveTransaction(InventoryTransaction transaction) {
        validateTransaction(transaction);
        
        // Cập nhật số lượng tồn kho
        Optional<InventoryManagement> itemOpt = inventoryManagementRepository.findById(transaction.getVatPhamId());
        if (itemOpt.isPresent()) {
            InventoryManagement item = itemOpt.get();
            Short newQuantity = (short) (item.getSoLuongTon() + transaction.getSoLuong());
            if (newQuantity < 0) {
                throw new RuntimeException("Số lượng tồn kho không đủ");
            }
            item.setSoLuongTon(newQuantity);
            inventoryManagementRepository.save(item);
        }
        
        if (transaction.getNgayGiaoDich() == null) {
            transaction.setNgayGiaoDich(LocalDateTime.now());
        }
        
        return inventoryTransactionRepository.save(transaction);
    }
    
    public List<InventoryManagement> searchItems(String keyword, String loaiVatPham) {
        List<InventoryManagement> allItems = inventoryManagementRepository.findAll();
        
        return allItems.stream()
                .filter(item -> {
                    boolean matchKeyword = keyword == null || keyword.trim().isEmpty() ||
                            item.getTenVatPham().toLowerCase().contains(keyword.toLowerCase()) ||
                            (item.getMaVatPham() != null && item.getMaVatPham().toLowerCase().contains(keyword.toLowerCase()));
                    
                    boolean matchType = loaiVatPham == null || loaiVatPham.trim().isEmpty() ||
                            (item.getLoaiVatPham() != null && item.getLoaiVatPham().equals(loaiVatPham));
                    
                    return matchKeyword && matchType;
                })
                .collect(Collectors.toList());
    }
    
    public void validateItem(InventoryManagement item) {
        if (item.getTenVatPham() == null || item.getTenVatPham().trim().isEmpty()) {
            throw new RuntimeException("Tên vật phẩm không được để trống");
        }
        
        if (item.getMaVatPham() == null || item.getMaVatPham().trim().isEmpty()) {
            throw new RuntimeException("Mã vật phẩm không được để trống");
        }
        
        if (item.getSoLuongTon() != null && item.getSoLuongTon() < 0) {
            throw new RuntimeException("Số lượng tồn không được âm");
        }
        
        if (item.getGiaNhap() != null && item.getGiaNhap().doubleValue() < 0) {
            throw new RuntimeException("Giá nhập không được âm");
        }
    }
    
    public void validateTransaction(InventoryTransaction transaction) {
        if (transaction.getVatPhamId() == null) {
            throw new RuntimeException("Vật phẩm không được để trống");
        }
        
        if (transaction.getSoLuong() == null || transaction.getSoLuong() == 0) {
            throw new RuntimeException("Số lượng không được để trống hoặc bằng 0");
        }
        
        Optional<InventoryManagement> itemOpt = inventoryManagementRepository.findById(transaction.getVatPhamId());
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Không tìm thấy vật phẩm với ID: " + transaction.getVatPhamId());
        }
    }
    
    // Report generation methods
    public Map<String, Object> generateInventoryReport() {
        Map<String, Object> report = new HashMap<>();
        List<InventoryManagement> allItems = inventoryManagementRepository.findAll();
        
        List<Map<String, Object>> inventoryData = allItems.stream().map(item -> {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("maVatPham", item.getMaVatPham());
            itemData.put("tenVatPham", item.getTenVatPham());
            itemData.put("loaiVatPham", item.getLoaiVatPham());
            itemData.put("soLuongTon", item.getSoLuongTon());
            
            String status = "Bình thường";
            if (item.getSoLuongTon() == 0) {
                status = "Hết hàng";
            } else if (item.getSoLuongTon() < 10) {
                status = "Sắp hết";
            }
            itemData.put("trangThai", status);
            
            return itemData;
        }).collect(Collectors.toList());
        
        report.put("items", inventoryData);
        report.put("totalItems", allItems.size());
        report.put("lowStockCount", allItems.stream().mapToInt(item -> item.getSoLuongTon() < 10 ? 1 : 0).sum());
        report.put("outOfStockCount", allItems.stream().mapToInt(item -> item.getSoLuongTon() == 0 ? 1 : 0).sum());
        
        return report;
    }
    
    public Map<String, Object> generateTransactionReport(String startDate, String endDate) {
        Map<String, Object> report = new HashMap<>();
        List<InventoryTransaction> allTransactions = inventoryTransactionRepository.findAllWithInventoryItem();
        
        // Filter by date if provided
        List<InventoryTransaction> filteredTransactions = allTransactions;
        if (startDate != null && endDate != null) {
            try {
                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);
                
                filteredTransactions = allTransactions.stream()
                    .filter(transaction -> {
                        LocalDate transactionDate = transaction.getNgayGiaoDich().toLocalDate();
                        return !transactionDate.isBefore(start) && !transactionDate.isAfter(end);
                    })
                    .collect(Collectors.toList());
            } catch (Exception e) {
                // If date parsing fails, use all transactions
            }
        }
        
        List<Map<String, Object>> transactionData = filteredTransactions.stream().map(transaction -> {
            Map<String, Object> data = new HashMap<>();
            data.put("ngayGiaoDich", transaction.getNgayGiaoDich().toString());
            data.put("vatPhamId", transaction.getVatPhamId());
            data.put("loaiGiaoDich", transaction.getLoaiGiaoDich());
            data.put("soLuong", transaction.getSoLuong());
            data.put("ghiChu", transaction.getLyDo());
            return data;
        }).collect(Collectors.toList());
        
        report.put("transactions", transactionData);
        report.put("totalTransactions", filteredTransactions.size());
        
        // Calculate transaction summary
        int inCount = (int) filteredTransactions.stream().filter(t -> LoaiGiaoDich.NHAP_KHO.equals(t.getLoaiGiaoDich())).count();
        int outCount = (int) filteredTransactions.stream().filter(t -> LoaiGiaoDich.XUAT_KHO.equals(t.getLoaiGiaoDich())).count();
        
        report.put("inboundCount", inCount);
        report.put("outboundCount", outCount);
        
        return report;
    }
    
    public Map<String, Object> generateExpiryReport() {
        Map<String, Object> report = new HashMap<>();
        List<InventoryManagement> allItems = inventoryManagementRepository.findAll();
        
        // Filter items with expiry dates
        List<Map<String, Object>> expiryData = allItems.stream()
            .filter(item -> item.getHanSuDung() != null)
            .map(item -> {
                Map<String, Object> itemData = new HashMap<>();
                itemData.put("maVatPham", item.getMaVatPham());
                itemData.put("tenVatPham", item.getTenVatPham());
                itemData.put("hanSuDung", item.getHanSuDung().toString());
                itemData.put("soLuongTon", item.getSoLuongTon());
                
                // Calculate days until expiry
                LocalDate today = LocalDate.now();
                LocalDate expiryDate = item.getHanSuDung();
                long daysUntilExpiry = java.time.temporal.ChronoUnit.DAYS.between(today, expiryDate);
                itemData.put("soNgayConLai", daysUntilExpiry);
                
                // Determine status
                String status = "Bình thường";
                if (daysUntilExpiry < 0) {
                    status = "Đã hết hạn";
                } else if (daysUntilExpiry <= 7) {
                    status = "Sắp hết hạn";
                } else if (daysUntilExpiry <= 30) {
                    status = "Cần chú ý";
                }
                itemData.put("trangThai", status);
                
                return itemData;
            })
            .collect(Collectors.toList());
        
        // Calculate summary statistics
        long expiredCount = expiryData.stream().filter(item -> (Long) item.get("soNgayConLai") < 0).count();
        long expiringSoonCount = expiryData.stream().filter(item -> {
            Long days = (Long) item.get("soNgayConLai");
            return days >= 0 && days <= 7;
        }).count();
        long needAttentionCount = expiryData.stream().filter(item -> {
            Long days = (Long) item.get("soNgayConLai");
            return days > 7 && days <= 30;
        }).count();
        
        report.put("items", expiryData);
        report.put("expiredCount", expiredCount);
        report.put("expiringSoonCount", expiringSoonCount);
        report.put("needAttentionCount", needAttentionCount);
        report.put("totalItemsWithExpiry", expiryData.size());
        
        return report;
    }
    
    public Map<String, Object> generateValueReport() {
        Map<String, Object> report = new HashMap<>();
        List<InventoryManagement> allItems = inventoryManagementRepository.findAll();
        
        // Group items by category and calculate values
        Map<String, Map<String, Object>> categoryStats = new HashMap<>();
        double totalValue = 0;
        
        for (InventoryManagement item : allItems) {
            String category = item.getLoaiVatPham() != null ? item.getLoaiVatPham() : "Khác";
            int stock = item.getSoLuongTon();
            double price = item.getGiaNhap() != null ? item.getGiaNhap().doubleValue() : 0;
            double value = stock * price;
            
            totalValue += value;
            
            categoryStats.computeIfAbsent(category, k -> {
                Map<String, Object> stats = new HashMap<>();
                stats.put("soLuong", 0);
                stats.put("giaTriUocTinh", 0.0);
                return stats;
            });
            
            Map<String, Object> stats = categoryStats.get(category);
            stats.put("soLuong", (Integer) stats.get("soLuong") + stock);
            stats.put("giaTriUocTinh", (Double) stats.get("giaTriUocTinh") + value);
        }
        
        // Calculate percentages and format data
        List<Map<String, Object>> valueData = new ArrayList<>();
        for (Map.Entry<String, Map<String, Object>> entry : categoryStats.entrySet()) {
            Map<String, Object> categoryData = new HashMap<>();
            categoryData.put("loaiVatPham", entry.getKey());
            categoryData.put("soLuong", entry.getValue().get("soLuong"));
            categoryData.put("giaTriUocTinh", entry.getValue().get("giaTriUocTinh"));
            
            double percentage = totalValue > 0 ? ((Double) entry.getValue().get("giaTriUocTinh") / totalValue) * 100 : 0;
            categoryData.put("tyLe", Math.round(percentage * 100.0) / 100.0); // Round to 2 decimal places
            
            valueData.add(categoryData);
        }
        
        // Sort by value descending
        valueData.sort((a, b) -> Double.compare((Double) b.get("giaTriUocTinh"), (Double) a.get("giaTriUocTinh")));
        
        report.put("categories", valueData);
        report.put("totalValue", totalValue);
        report.put("totalCategories", categoryStats.size());
        
        return report;
    }
    
    public Map<String, Object> generateSummaryStats() {
        Map<String, Object> summary = new HashMap<>();
        List<InventoryManagement> allItems = inventoryManagementRepository.findAll();
        List<InventoryTransaction> allTransactions = inventoryTransactionRepository.findAllWithInventoryItem();
        
        // Basic counts
        summary.put("totalItems", allItems.size());
        summary.put("totalStock", allItems.stream().mapToInt(InventoryManagement::getSoLuongTon).sum());
        summary.put("lowStockItems", allItems.stream().mapToInt(item -> item.getSoLuongTon() < 10 ? 1 : 0).sum());
        
        // Recent transactions (last 7 days)
        LocalDateTime weekAgo = LocalDateTime.now().minusDays(7);
        int recentTransactions = (int) allTransactions.stream()
            .filter(transaction -> transaction.getNgayGiaoDich().isAfter(weekAgo))
            .count();
        summary.put("recentTransactions", recentTransactions);
        
        // Total value
        double totalValue = allItems.stream()
            .mapToDouble(item -> item.getSoLuongTon() * (item.getGiaNhap() != null ? item.getGiaNhap().doubleValue() : 0))
            .sum();
        summary.put("totalValue", totalValue);
        
        return summary;
    }
}