package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.InventoryManagement;
import panacea.website_dat_lich_khach_san.entity.InventoryTransaction;
import panacea.website_dat_lich_khach_san.repository.InventoryManagementRepository;
import panacea.website_dat_lich_khach_san.repository.InventoryTransactionRepository;

import java.util.List;

@Service
public class    QuanLyKhoService {
    @Autowired
    private InventoryManagementRepository inventoryManagementRepository;
    @Autowired
    private InventoryTransactionRepository inventoryTransactionRepository;

    public String getStaffName() {
        return "Nguyễn Văn A";
    }

    // ================= CRUD cho InventoryManagement =================
    public List<InventoryManagement> getAllItems() {
        return inventoryManagementRepository.findAll();
    }
    public InventoryManagement getItemById(Integer id) {
        return inventoryManagementRepository.findById(id).orElse(null);
    }
    public InventoryManagement createItem(InventoryManagement item) {
        return inventoryManagementRepository.save(item);
    }
    public InventoryManagement updateItem(Integer id, InventoryManagement updated) {
        return inventoryManagementRepository.findById(id).map(item -> {
            item.setKhachSanId(updated.getKhachSanId());
            item.setTenVatPham(updated.getTenVatPham());
            item.setMaVatPham(updated.getMaVatPham());
            item.setLoaiVatPham(updated.getLoaiVatPham());
            item.setDonViTinh(updated.getDonViTinh());
            item.setSoLuongTon(updated.getSoLuongTon());
            item.setSoLuongToiThieu(updated.getSoLuongToiThieu());
            item.setGiaNhap(updated.getGiaNhap());
            item.setNhaCungCap(updated.getNhaCungCap());
            item.setNgayNhapCuoi(updated.getNgayNhapCuoi());
            item.setHanSuDung(updated.getHanSuDung());
            item.setViTriKho(updated.getViTriKho());
            item.setTrangThai(updated.getTrangThai());
            item.setUuidId(updated.getUuidId());
            return inventoryManagementRepository.save(item);
        }).orElse(null);
    }
    public boolean deleteItem(Integer id) {
        if (inventoryManagementRepository.existsById(id)) {
            inventoryManagementRepository.deleteById(id);
            return true;
        }
        return false;
    }
    // ================= CRUD cho InventoryTransaction =================
    public List<InventoryTransaction> getAllTransactions() {
        return inventoryTransactionRepository.findAll();
    }
    public InventoryTransaction getTransactionById(Integer id) {
        return inventoryTransactionRepository.findById(id).orElse(null);
    }
    public InventoryTransaction createTransaction(InventoryTransaction transaction) {
        return inventoryTransactionRepository.save(transaction);
    }
    public InventoryTransaction updateTransaction(Integer id, InventoryTransaction updated) {
        return inventoryTransactionRepository.findById(id).map(tran -> {
            tran.setVatPhamId(updated.getVatPhamId());
            tran.setLoaiGiaoDich(updated.getLoaiGiaoDich());
            tran.setSoLuong(updated.getSoLuong());
            tran.setGiaTri(updated.getGiaTri());
            tran.setLyDo(updated.getLyDo());
            tran.setNhanVienId(updated.getNhanVienId());
            tran.setPhongId(updated.getPhongId());
            tran.setNgayGiaoDich(updated.getNgayGiaoDich());
            tran.setSoChungTu(updated.getSoChungTu());
            tran.setUuidId(updated.getUuidId());
            return inventoryTransactionRepository.save(tran);
        }).orElse(null);
    }
    public boolean deleteTransaction(Integer id) {
        if (inventoryTransactionRepository.existsById(id)) {
            inventoryTransactionRepository.deleteById(id);
            return true;
        }
        return false;
    }
} 