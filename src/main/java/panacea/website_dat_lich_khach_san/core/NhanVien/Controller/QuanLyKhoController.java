
package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.QuanLyKhoService;
import panacea.website_dat_lich_khach_san.entity.InventoryManagement;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.InventoryManagementDTO;
import panacea.website_dat_lich_khach_san.entity.InventoryTransaction;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.InventoryTransactionDTO;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/nhanvien/quanlykho")
public class QuanLyKhoController {
    private final QuanLyKhoService quanLyKhoService;
    public QuanLyKhoController(QuanLyKhoService quanLyKhoService) {
        this.quanLyKhoService = quanLyKhoService;
    }

    // Trang giao diện quản lý kho cho nhân viên
    @GetMapping("")
    public String view(Model model) {
        model.addAttribute("staffName", quanLyKhoService.getStaffName());
        model.addAttribute("items", quanLyKhoService.getAllItems());
        model.addAttribute("transactions", quanLyKhoService.getAllTransactions());
        return "NhanVien/QuanLyKho";
    }

    // ========== RESTful API CRUD cho InventoryManagement ==========
    @RestController
    @RequestMapping("/api/nhanvien/quanlykho")
    public static class QuanLyKhoRestController {
        private final QuanLyKhoService quanLyKhoService;
        public QuanLyKhoRestController(QuanLyKhoService quanLyKhoService) {
            this.quanLyKhoService = quanLyKhoService;
        }

        @GetMapping("")
        public List<InventoryManagementDTO> getAllItems() {
            return quanLyKhoService.getAllItems().stream().map(this::toDTO).collect(Collectors.toList());
        }

        @GetMapping("/{id}")
        public ResponseEntity<InventoryManagementDTO> getItemById(@PathVariable Integer id) {
            InventoryManagement item = quanLyKhoService.getItemById(id);
            if (item == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(toDTO(item));
        }

        @PostMapping("")
        public ResponseEntity<InventoryManagementDTO> createItem(@Valid @RequestBody InventoryManagementDTO dto) {
            InventoryManagement created = quanLyKhoService.createItem(toEntity(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
        }

        @PutMapping("/{id}")
        public ResponseEntity<InventoryManagementDTO> updateItem(@PathVariable Integer id, @Valid @RequestBody InventoryManagementDTO dto) {
            InventoryManagement updated = quanLyKhoService.updateItem(id, toEntity(dto));
            if (updated == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(toDTO(updated));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteItem(@PathVariable Integer id) {
            boolean deleted = quanLyKhoService.deleteItem(id);
            if (!deleted) return ResponseEntity.notFound().build();
            return ResponseEntity.noContent().build();
        }

        // ========== RESTful API CRUD cho InventoryTransaction ==========
        @GetMapping("/transactions")
        public List<InventoryTransactionDTO> getAllTransactions() {
            return quanLyKhoService.getAllTransactions().stream().map(this::toTransactionDTO).collect(Collectors.toList());
        }
        @GetMapping("/transactions/{id}")
        public ResponseEntity<InventoryTransactionDTO> getTransactionById(@PathVariable Integer id) {
            InventoryTransaction tran = quanLyKhoService.getTransactionById(id);
            if (tran == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(toTransactionDTO(tran));
        }
        @PostMapping("/transactions")
        public ResponseEntity<InventoryTransactionDTO> createTransaction(@Valid @RequestBody InventoryTransactionDTO dto) {
            InventoryTransaction created = quanLyKhoService.createTransaction(toTransactionEntity(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(toTransactionDTO(created));
        }
        @PutMapping("/transactions/{id}")
        public ResponseEntity<InventoryTransactionDTO> updateTransaction(@PathVariable Integer id, @Valid @RequestBody InventoryTransactionDTO dto) {
            InventoryTransaction updated = quanLyKhoService.updateTransaction(id, toTransactionEntity(dto));
            if (updated == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(toTransactionDTO(updated));
        }
        @DeleteMapping("/transactions/{id}")
        public ResponseEntity<Void> deleteTransaction(@PathVariable Integer id) {
            boolean deleted = quanLyKhoService.deleteTransaction(id);
            if (!deleted) return ResponseEntity.notFound().build();
            return ResponseEntity.noContent().build();
        }

        // ========== Mapping ===========
        private InventoryManagementDTO toDTO(InventoryManagement e) {
            if (e == null) return null;
            InventoryManagementDTO dto = new InventoryManagementDTO();
            dto.setId(e.getId());
            dto.setKhachSanId(e.getKhachSanId());
            dto.setTenVatPham(e.getTenVatPham());
            dto.setMaVatPham(e.getMaVatPham());
            dto.setLoaiVatPham(e.getLoaiVatPham());
            dto.setDonViTinh(e.getDonViTinh());
            dto.setSoLuongTon(e.getSoLuongTon());
            dto.setSoLuongToiThieu(e.getSoLuongToiThieu());
            dto.setGiaNhap(e.getGiaNhap());
            dto.setNhaCungCap(e.getNhaCungCap());
            dto.setNgayNhapCuoi(e.getNgayNhapCuoi());
            dto.setHanSuDung(e.getHanSuDung());
            dto.setViTriKho(e.getViTriKho());
            dto.setTrangThai(e.getTrangThai());
            dto.setUuidId(e.getUuidId());
            return dto;
        }
        private InventoryManagement toEntity(InventoryManagementDTO dto) {
            if (dto == null) return null;
            InventoryManagement e = new InventoryManagement();
            e.setId(dto.getId());
            e.setKhachSanId(dto.getKhachSanId());
            e.setTenVatPham(dto.getTenVatPham());
            e.setMaVatPham(dto.getMaVatPham());
            e.setLoaiVatPham(dto.getLoaiVatPham());
            e.setDonViTinh(dto.getDonViTinh());
            e.setSoLuongTon(dto.getSoLuongTon());
            e.setSoLuongToiThieu(dto.getSoLuongToiThieu());
            e.setGiaNhap(dto.getGiaNhap());
            e.setNhaCungCap(dto.getNhaCungCap());
            e.setNgayNhapCuoi(dto.getNgayNhapCuoi());
            e.setHanSuDung(dto.getHanSuDung());
            e.setViTriKho(dto.getViTriKho());
            e.setTrangThai(dto.getTrangThai());
            e.setUuidId(dto.getUuidId());
            return e;
        }
        // ========== Mapping cho InventoryTransaction ==========
        private InventoryTransactionDTO toTransactionDTO(InventoryTransaction e) {
            if (e == null) return null;
            return new InventoryTransactionDTO(
                e.getId(),
                e.getVatPhamId(),
                e.getLoaiGiaoDich(),
                e.getSoLuong(),
                e.getGiaTri(),
                e.getLyDo(),
                e.getNhanVienId(),
                e.getPhongId(),
                e.getNgayGiaoDich(),
                e.getSoChungTu(),
                e.getUuidId()
            );
        }
        private InventoryTransaction toTransactionEntity(InventoryTransactionDTO dto) {
            if (dto == null) return null;
            return new InventoryTransaction(
                dto.getId(),
                dto.getVatPhamId(),
                dto.getLoaiGiaoDich(),
                dto.getSoLuong(),
                dto.getGiaTri(),
                dto.getLyDo(),
                dto.getNhanVienId(),
                dto.getPhongId(),
                dto.getNgayGiaoDich(),
                dto.getSoChungTu(),
                dto.getUuidId(),
                null, null, null
            );
        }
    }
}
