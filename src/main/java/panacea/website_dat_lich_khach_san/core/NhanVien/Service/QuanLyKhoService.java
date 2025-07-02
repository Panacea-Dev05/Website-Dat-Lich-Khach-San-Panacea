package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.InventoryManagement;
import panacea.website_dat_lich_khach_san.entity.InventoryTransaction;
import panacea.website_dat_lich_khach_san.repository.InventoryManagementRepository;
import panacea.website_dat_lich_khach_san.repository.InventoryTransactionRepository;

import java.util.List;

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
        return inventoryManagementRepository.findAll();
    }

    public List<InventoryTransaction> getAllTransactions() {
        return inventoryTransactionRepository.findAll();
    }
} 