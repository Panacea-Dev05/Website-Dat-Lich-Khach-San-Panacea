package panacea.website_dat_lich_khach_san.core.Admin.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.ServiceDTO;
import panacea.website_dat_lich_khach_san.entity.ServiceEntity;
import panacea.website_dat_lich_khach_san.repository.ServiceRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service quản lý dịch vụ khách sạn cho Admin
 * Chức năng: CRUD dịch vụ (spa, massage, ăn uống, giặt ủi, v.v.)
 */
@Service
public class AdminServiceService {
    @Autowired
    private ServiceRepository serviceRepository;

    /**
     * LẤY TẤT CẢ DỊCH VỤ: Lấy danh sách tất cả dịch vụ khách sạn
     * @return List<ServiceDTO> - Danh sách dịch vụ
     */
    public List<ServiceDTO> getAllServices() {
        return serviceRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * LẤY DỊCH VỤ CÓ PHÂN TRANG: Lấy danh sách dịch vụ với phân trang
     * @param pageable - Thông tin phân trang (page, size, sort)
     * @return Page<ServiceDTO> - Dịch vụ có phân trang
     */
    public Page<ServiceDTO> getAllServicesPaged(Pageable pageable) {
        return serviceRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * LẤY DỊCH VỤ THEO ID: Lấy thông tin chi tiết dịch vụ theo ID
     * @param id - ID dịch vụ
     * @return ServiceDTO - Thông tin dịch vụ hoặc null nếu không tìm thấy
     */
    public ServiceDTO getServiceById(Integer id) {
        Optional<ServiceEntity> service = serviceRepository.findById(id);
        return service.map(this::toDTO).orElse(null);
    }

    /**
     * TẠO DỊCH VỤ MỚI: Tạo dịch vụ mới trong hệ thống
     * @param dto - Thông tin dịch vụ cần tạo
     * @return ServiceDTO - Dịch vụ đã được tạo
     */
    public ServiceDTO createService(ServiceDTO dto) {
        ServiceEntity service = toEntity(dto);
        ServiceEntity saved = serviceRepository.save(service);
        return toDTO(saved);
    }

    /**
     * CẬP NHẬT DỊCH VỤ: Cập nhật thông tin dịch vụ theo ID
     * @param id - ID dịch vụ cần cập nhật
     * @param dto - Thông tin dịch vụ mới
     * @return ServiceDTO - Dịch vụ đã cập nhật hoặc null nếu không tìm thấy
     */
    public ServiceDTO updateService(Integer id, ServiceDTO dto) {
        Optional<ServiceEntity> existing = serviceRepository.findById(id);
        if (existing.isPresent()) {
            ServiceEntity service = toEntity(dto);
            service.setId(id);
            ServiceEntity saved = serviceRepository.save(service);
            return toDTO(saved);
        }
        return null;
    }

    /**
     * XÓA DỊCH VỤ: Xóa dịch vụ khỏi hệ thống
     * @param id - ID dịch vụ cần xóa
     * @return boolean - true nếu xóa thành công, false nếu không tìm thấy
     */
    public boolean deleteService(Integer id) {
        if (serviceRepository.existsById(id)) {
            serviceRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * CHUYỂN ĐỔI ENTITY SANG DTO: Chuyển đổi ServiceEntity thành ServiceDTO
     * @param s - ServiceEntity cần chuyển đổi
     * @return ServiceDTO - DTO đã chuyển đổi
     */
    private ServiceDTO toDTO(ServiceEntity s) {
        ServiceDTO dto = new ServiceDTO();
        dto.setId(s.getId());
        dto.setMaDichVu(s.getMaDichVu());
        dto.setTenDichVu(s.getTenDichVu());
        dto.setLoaiDichVu(s.getLoaiDichVu());
        dto.setMoTa(s.getMoTa());
        dto.setDonGia(s.getDonGia());
        dto.setDonViTinh(s.getDonViTinh());
        dto.setTrangThai(s.getTrangThai());
        dto.setUuidId(s.getUuidId());
        dto.setCreatedDate(s.getCreatedDate());
        dto.setLastModifiedDate(s.getLastModifiedDate());

        // Debug logging
        System.out.println("Converting ServiceEntity to DTO:");
        System.out.println("  ID: " + s.getId());
        System.out.println("  MaDichVu: " + s.getMaDichVu());
        System.out.println("  TenDichVu: " + s.getTenDichVu());
        System.out.println("  LoaiDichVu: " + s.getLoaiDichVu());
        System.out.println("  DonViTinh: " + s.getDonViTinh());
        System.out.println("  TrangThai: " + s.getTrangThai());

        return dto;
    }
    /**
     * CHUYỂN ĐỔI DTO SANG ENTITY: Chuyển đổi ServiceDTO thành ServiceEntity
     * @param dto - ServiceDTO cần chuyển đổi
     * @return ServiceEntity - Entity đã chuyển đổi
     */
    private ServiceEntity toEntity(ServiceDTO dto) {
        ServiceEntity s = new ServiceEntity();
        s.setId(dto.getId());
        s.setMaDichVu(dto.getMaDichVu());
        s.setTenDichVu(dto.getTenDichVu());
        s.setLoaiDichVu(dto.getLoaiDichVu());
        s.setMoTa(dto.getMoTa());
        s.setDonGia(dto.getDonGia());
        s.setDonViTinh(dto.getDonViTinh());
        s.setTrangThai(dto.getTrangThai());
        s.setUuidId(dto.getUuidId());
        s.setCreatedDate(dto.getCreatedDate());
        s.setLastModifiedDate(dto.getLastModifiedDate());
        return s;
    }
}