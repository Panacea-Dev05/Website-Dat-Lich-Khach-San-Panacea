package panacea.website_dat_lich_khach_san.core.Admin.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Staff;
import panacea.website_dat_lich_khach_san.repository.StaffRepository;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.StaffDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminStaffService {
    
    @Autowired
    private StaffRepository staffRepository;
    
    public List<StaffDTO> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public StaffDTO getStaffById(Integer id) {
        Optional<Staff> staff = staffRepository.findById(id);
        return staff.map(this::convertToDTO).orElse(null);
    }
    
    public StaffDTO createStaff(StaffDTO staffDTO) {
        Staff staff = convertToEntity(staffDTO);
        Staff savedStaff = staffRepository.save(staff);
        return convertToDTO(savedStaff);
    }
    
    public StaffDTO updateStaff(Integer id, StaffDTO staffDTO) {
        Optional<Staff> existingStaff = staffRepository.findById(id);
        if (existingStaff.isPresent()) {
            Staff staff = existingStaff.get();
            staff.setMaNhanVien(staffDTO.getMaNhanVien());
            staff.setHoTen(staffDTO.getHo() + " " + staffDTO.getTen());
            staff.setEmail(staffDTO.getEmail());
            staff.setSoDienThoai(staffDTO.getSoDienThoai());
            staff.setChucVu(staffDTO.getChucVu());
            staff.setTrangThai(Staff.TrangThaiStaff.valueOf(staffDTO.getTrangThai()));
            Staff savedStaff = staffRepository.save(staff);
            return convertToDTO(savedStaff);
        }
        return null;
    }
    
    public boolean deleteStaff(Integer id) {
        if (staffRepository.existsById(id)) {
            staffRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private StaffDTO convertToDTO(Staff staff) {
        StaffDTO dto = new StaffDTO();
        dto.setId(staff.getId());
        dto.setMaNhanVien(staff.getMaNhanVien());
        String[] nameParts = staff.getHoTen().split(" ", 2);
        dto.setHo(nameParts.length > 0 ? nameParts[0] : "");
        dto.setTen(nameParts.length > 1 ? nameParts[1] : "");
        dto.setEmail(staff.getEmail());
        dto.setSoDienThoai(staff.getSoDienThoai());
        dto.setChucVu(staff.getChucVu());
        dto.setTrangThai(staff.getTrangThai().name());
        dto.setHotelId(staff.getKhachSanId());
        dto.setUuidId(staff.getUuidId());
        dto.setCreatedDate(staff.getCreatedDate());
        dto.setLastModifiedDate(staff.getLastModifiedDate());
        return dto;
    }
    
    private Staff convertToEntity(StaffDTO dto) {
        Staff staff = new Staff();
        staff.setMaNhanVien(dto.getMaNhanVien());
        staff.setHoTen(dto.getHo() + " " + dto.getTen());
        staff.setEmail(dto.getEmail());
        staff.setSoDienThoai(dto.getSoDienThoai());
        staff.setChucVu(dto.getChucVu());
        staff.setKhachSanId(dto.getHotelId());
        staff.setTrangThai(Staff.TrangThaiStaff.valueOf(dto.getTrangThai()));
        return staff;
    }
} 