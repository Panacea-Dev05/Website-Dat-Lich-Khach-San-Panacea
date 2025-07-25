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

@Service
public class AdminServiceService {
    @Autowired
    private ServiceRepository serviceRepository;

    public List<ServiceDTO> getAllServices() {
        return serviceRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public Page<ServiceDTO> getAllServicesPaged(Pageable pageable) {
        return serviceRepository.findAll(pageable).map(this::toDTO);
    }

    public ServiceDTO getServiceById(Integer id) {
        Optional<ServiceEntity> service = serviceRepository.findById(id);
        return service.map(this::toDTO).orElse(null);
    }

    public ServiceDTO createService(ServiceDTO dto) {
        ServiceEntity service = toEntity(dto);
        ServiceEntity saved = serviceRepository.save(service);
        return toDTO(saved);
    }

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

    public boolean deleteService(Integer id) {
        if (serviceRepository.existsById(id)) {
            serviceRepository.deleteById(id);
            return true;
        }
        return false;
    }

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
        return dto;
    }
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