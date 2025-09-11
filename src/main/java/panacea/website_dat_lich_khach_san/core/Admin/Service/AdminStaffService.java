package panacea.website_dat_lich_khach_san.core.Admin.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Staff;
import panacea.website_dat_lich_khach_san.repository.StaffRepository;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.StaffDTO;
import panacea.website_dat_lich_khach_san.repository.RolePermissionRepository;
import panacea.website_dat_lich_khach_san.entity.RolePermission;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminStaffService {
    
    @Autowired
    private StaffRepository staffRepository;
    
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    
    public List<StaffDTO> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public StaffDTO getStaffById(Integer id) {
        if (id == null) {
            return null;
        }
        Optional<Staff> staff = staffRepository.findById(id);
        return staff.map(this::convertToDTO).orElse(null);
    }
    
    public StaffDTO createStaff(StaffDTO staffDTO) {
        // Validate bắt buộc
        if (staffDTO == null) {
            throw new IllegalArgumentException("Dữ liệu nhân viên không hợp lệ");
        }
        if (staffDTO.getMaNhanVien() == null || staffDTO.getMaNhanVien().isBlank()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống");
        }
        if (staffDTO.getEmail() == null || staffDTO.getEmail().isBlank()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (staffDTO.getChucVu() == null || staffDTO.getChucVu().isBlank()) {
            throw new IllegalArgumentException("Chức vụ không được để trống");
        }
        // Check trùng email
        if (staffRepository.findByEmail(staffDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }
        // Check trùng mã nhân viên
        boolean duplicateMa = staffRepository.findAll().stream()
            .anyMatch(s -> staffDTO.getMaNhanVien().equalsIgnoreCase(s.getMaNhanVien()));
        if (duplicateMa) {
            throw new IllegalArgumentException("Mã nhân viên đã tồn tại");
        }
        Staff staff = convertToEntity(staffDTO);
        // Đảm bảo trường mặc định
        if (staff.getTrangThai() == null) {
            staff.setTrangThai(Staff.TrangThaiStaff.HOAT_DONG);
        }
        if (staff.getHoTen() == null || staff.getHoTen().isBlank()) {
            String ho = staffDTO.getHo() != null ? staffDTO.getHo() : "";
            String ten = staffDTO.getTen() != null ? staffDTO.getTen() : "";
            staff.setHoTen((ho + " " + ten).trim());
        }
        Staff savedStaff = staffRepository.save(staff);
        return convertToDTO(savedStaff);
    }
    
    public StaffDTO updateStaff(Integer id, StaffDTO staffDTO) {
        Optional<Staff> existingStaff = staffRepository.findById(id);
        if (existingStaff.isPresent()) {
            Staff staff = existingStaff.get();
            // Validate cơ bản
            if (staffDTO.getMaNhanVien() == null || staffDTO.getMaNhanVien().isBlank()) {
                throw new IllegalArgumentException("Mã nhân viên không được để trống");
            }
            if (staffDTO.getEmail() == null || staffDTO.getEmail().isBlank()) {
                throw new IllegalArgumentException("Email không được để trống");
            }
            if (staffDTO.getChucVu() == null || staffDTO.getChucVu().isBlank()) {
                throw new IllegalArgumentException("Chức vụ không được để trống");
            }
            // Check trùng email với người khác
            staffRepository.findByEmail(staffDTO.getEmail()).ifPresent(other -> {
                if (!other.getId().equals(staff.getId())) {
                    throw new IllegalArgumentException("Email đã tồn tại");
                }
            });
            // Check trùng mã nhân viên với người khác
            boolean duplicateMa = staffRepository.findAll().stream()
                .anyMatch(s -> !s.getId().equals(staff.getId()) && staffDTO.getMaNhanVien().equalsIgnoreCase(s.getMaNhanVien()));
            if (duplicateMa) {
                throw new IllegalArgumentException("Mã nhân viên đã tồn tại");
            }
            staff.setMaNhanVien(staffDTO.getMaNhanVien());
            String ho = staffDTO.getHo() != null ? staffDTO.getHo() : "";
            String ten = staffDTO.getTen() != null ? staffDTO.getTen() : "";
            staff.setHoTen((ho + " " + ten).trim());
            staff.setEmail(staffDTO.getEmail());
            staff.setSoDienThoai(staffDTO.getSoDienThoai());
            staff.setChucVu(staffDTO.getChucVu());
            if (staffDTO.getTrangThai() != null && !staffDTO.getTrangThai().isBlank()) {
                staff.setTrangThai(Staff.TrangThaiStaff.valueOf(staffDTO.getTrangThai()));
            }
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
    
    public List<StaffDTO> getStaffByHotel(Integer hotelId) {
        return staffRepository.findAll().stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public RolePermission assignPermission(Integer staffId, String vaiTro, Integer quyenId) {
        RolePermission rp = new RolePermission();
        rp.setVaiTro(vaiTro);
        rp.setQuyenId(quyenId);
        rp.setTrangThai("Hoạt động");
        return rolePermissionRepository.save(rp);
    }
    
    public StaffDTO deactivateStaff(Integer id) {
        Optional<Staff> opt = staffRepository.findById(id);
        if (opt.isEmpty()) return null;
        Staff staff = opt.get();
        staff.setTrangThai(Staff.TrangThaiStaff.NGHI_VIEC);
        Staff saved = staffRepository.save(staff);
        return convertToDTO(saved);
    }
    
    private StaffDTO convertToDTO(Staff staff) {
        StaffDTO dto = new StaffDTO();
        dto.setId(staff.getId());
        dto.setMaNhanVien(staff.getMaNhanVien());
        String hoTen = staff.getHoTen() != null ? staff.getHoTen() : "";
        String[] nameParts = hoTen.split(" ", 2);
        dto.setHo(nameParts.length > 0 ? nameParts[0] : "");
        dto.setTen(nameParts.length > 1 ? nameParts[1] : "");
        dto.setEmail(staff.getEmail());
        dto.setSoDienThoai(staff.getSoDienThoai());
        dto.setChucVu(staff.getChucVu());
        dto.setTrangThai(staff.getTrangThai() != null ? staff.getTrangThai().name() : null);
        dto.setUuidId(staff.getUuidId());
        dto.setCreatedDate(staff.getCreatedDate());
        dto.setLastModifiedDate(staff.getLastModifiedDate());
        return dto;
    }
    
    private Staff convertToEntity(StaffDTO dto) {
        Staff staff = new Staff();
        staff.setMaNhanVien(dto.getMaNhanVien());
        String ho = dto.getHo() != null ? dto.getHo() : "";
        String ten = dto.getTen() != null ? dto.getTen() : "";
        staff.setHoTen((ho + " " + ten).trim());
        staff.setEmail(dto.getEmail());
        staff.setSoDienThoai(dto.getSoDienThoai());
        staff.setChucVu(dto.getChucVu());
        if (dto.getTrangThai() != null && !dto.getTrangThai().isBlank()) {
            staff.setTrangThai(Staff.TrangThaiStaff.valueOf(dto.getTrangThai()));
        } else {
            staff.setTrangThai(Staff.TrangThaiStaff.HOAT_DONG);
        }
        return staff;
    }
}