
package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.DichVuService;
import panacea.website_dat_lich_khach_san.entity.Service;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.ServiceDTO;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/nhanvien/dichvu")
public class DichVuController {
    private final DichVuService dichVuService;
    public DichVuController(DichVuService dichVuService) {
        this.dichVuService = dichVuService;
    }

    // Trang giao diện dịch vụ cho nhân viên
    @GetMapping("")
    public String view(Model model) {
        model.addAttribute("staffName", dichVuService.getStaffName());
        model.addAttribute("serviceDetails", dichVuService.getAllServiceDetails());
        return "NhanVien/DichVu";
    }

    // ========== RESTful API CRUD cho Service ==========
    @RestController
    @RequestMapping("/api/nhanvien/dichvu")
    public static class DichVuRestController {
        private final DichVuService dichVuService;
        public DichVuRestController(DichVuService dichVuService) {
            this.dichVuService = dichVuService;
        }

        @GetMapping("")
        public List<ServiceDTO> getAllServices() {
            return dichVuService.getAllServices().stream().map(this::toDTO).collect(Collectors.toList());
        }

        @GetMapping("/{id}")
        public ResponseEntity<ServiceDTO> getServiceById(@PathVariable Integer id) {
            Service service = dichVuService.getServiceById(id);
            if (service == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(toDTO(service));
        }

        @PostMapping("")
        public ResponseEntity<ServiceDTO> createService(@RequestBody ServiceDTO dto) {
            Service created = dichVuService.createService(toEntity(dto));
            return ResponseEntity.status(HttpStatus.CREATED).body(toDTO(created));
        }

        @PutMapping("/{id}")
        public ResponseEntity<ServiceDTO> updateService(@PathVariable Integer id, @RequestBody ServiceDTO dto) {
            Service updated = dichVuService.updateService(id, toEntity(dto));
            if (updated == null) return ResponseEntity.notFound().build();
            return ResponseEntity.ok(toDTO(updated));
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<Void> deleteService(@PathVariable Integer id) {
            boolean deleted = dichVuService.deleteService(id);
            if (!deleted) return ResponseEntity.notFound().build();
            return ResponseEntity.noContent().build();
        }

        // ========== Mapping ===========
        private ServiceDTO toDTO(Service s) {
            if (s == null) return null;
            ServiceDTO dto = new ServiceDTO();
            dto.setId(s.getId());
            dto.setMaDichVu(s.getMaDichVu());
            dto.setTenDichVu(s.getTenDichVu());
            dto.setLoaiDichVu(s.getLoaiDichVu());
            dto.setMoTa(s.getMoTa());
            dto.setDonGia(s.getDonGia());
            dto.setDonViTinh(s.getDonViTinh());
            dto.setKhachSanId(s.getKhachSanId());
            dto.setTrangThai(s.getTrangThai());
            dto.setUuidId(s.getUuidId());
            dto.setCreatedDate(s.getCreatedDate());
            dto.setLastModifiedDate(s.getLastModifiedDate());
            return dto;
        }
        private Service toEntity(ServiceDTO dto) {
            if (dto == null) return null;
            Service s = new Service();
            s.setId(dto.getId());
            s.setMaDichVu(dto.getMaDichVu());
            s.setTenDichVu(dto.getTenDichVu());
            s.setLoaiDichVu(dto.getLoaiDichVu());
            s.setMoTa(dto.getMoTa());
            s.setDonGia(dto.getDonGia());
            s.setDonViTinh(dto.getDonViTinh());
            s.setKhachSanId(dto.getKhachSanId());
            s.setTrangThai(dto.getTrangThai());
            s.setUuidId(dto.getUuidId());
            s.setCreatedDate(dto.getCreatedDate());
            s.setLastModifiedDate(dto.getLastModifiedDate());
            return s;
        }
    }
}
