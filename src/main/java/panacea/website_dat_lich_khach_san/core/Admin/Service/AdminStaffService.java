package panacea.website_dat_lich_khach_san.core.Admin.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import panacea.website_dat_lich_khach_san.entity.Staff;
import panacea.website_dat_lich_khach_san.repository.StaffRepository;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.StaffDTO;
import panacea.website_dat_lich_khach_san.repository.RolePermissionRepository;
import panacea.website_dat_lich_khach_san.entity.RolePermission;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
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
        Optional<Staff> staff = staffRepository.findById(id);
        return staff.map(this::convertToDTO).orElse(null);
    }
    
    public StaffDTO createStaff(StaffDTO staffDTO) {
        // Validate business rules
        validateStaffDTO(staffDTO);
        
        Staff staff = convertToEntity(staffDTO);
        Staff savedStaff = staffRepository.save(staff);
        return convertToDTO(savedStaff);
    }
    
    public StaffDTO updateStaff(Integer id, StaffDTO staffDTO) {
        Optional<Staff> existingStaff = staffRepository.findById(id);
        if (existingStaff.isPresent()) {
            Staff staff = existingStaff.get();
            
            // Update fields
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
            // Check if staff has any dependent records
            if (hasDependendentRecords(id)) {
                // If has dependent records, just deactivate instead of delete
                deactivateStaff(id);
                return true;
            } else {
                staffRepository.deleteById(id);
                return true;
            }
        }
        return false;
    }
    
    public List<StaffDTO> getStaffByHotel(Integer hotelId) {
        return staffRepository.findAll().stream()
            .filter(s -> s.getKhachSanId() != null && s.getKhachSanId().equals(hotelId))
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public List<StaffDTO> searchStaff(String keyword) {
        return staffRepository.findAll().stream()
            .filter(staff -> 
                staff.getHoTen().toLowerCase().contains(keyword.toLowerCase()) ||
                staff.getMaNhanVien().toLowerCase().contains(keyword.toLowerCase()) ||
                staff.getEmail().toLowerCase().contains(keyword.toLowerCase()) ||
                staff.getChucVu().toLowerCase().contains(keyword.toLowerCase())
            )
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
    
    // Validation methods
    public boolean existsByMaNhanVien(String maNhanVien) {
        return staffRepository.existsByMaNhanVien(maNhanVien);
    }
    
    public boolean existsByEmail(String email) {
        return staffRepository.existsByEmail(email);
    }
    
    public boolean existsByMaNhanVienAndIdNot(String maNhanVien, Integer id) {
        return staffRepository.existsByMaNhanVienAndIdNot(maNhanVien, id);
    }
    
    public boolean existsByEmailAndIdNot(String email, Integer id) {
        return staffRepository.existsByEmailAndIdNot(email, id);
    }
    
    // Helper methods
    private void validateStaffDTO(StaffDTO staffDTO) {
        if (staffDTO.getMaNhanVien() == null || staffDTO.getMaNhanVien().trim().isEmpty()) {
            throw new IllegalArgumentException("Mã nhân viên không được để trống");
        }
        if (staffDTO.getHo() == null || staffDTO.getHo().trim().isEmpty()) {
            throw new IllegalArgumentException("Họ không được để trống");
        }
        if (staffDTO.getTen() == null || staffDTO.getTen().trim().isEmpty()) {
            throw new IllegalArgumentException("Tên không được để trống");
        }
        if (staffDTO.getEmail() == null || staffDTO.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email không được để trống");
        }
        if (staffDTO.getChucVu() == null || staffDTO.getChucVu().trim().isEmpty()) {
            throw new IllegalArgumentException("Chức vụ không được để trống");
        }
        
        // Validate email format
        if (!isValidEmail(staffDTO.getEmail())) {
            throw new IllegalArgumentException("Email không hợp lệ");
        }
        
        // Validate phone number format
        if (staffDTO.getSoDienThoai() != null && !isValidPhoneNumber(staffDTO.getSoDienThoai())) {
            throw new IllegalArgumentException("Số điện thoại không hợp lệ");
        }
    }
    
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches("^[0-9]{10,11}$");
    }
    
    private boolean hasDependendentRecords(Integer staffId) {
        // Check if staff has any maintenance schedules or housekeeping logs
        // This would require additional repository calls
        // For now, return false - you can implement this based on your business logic
        return false;
    }
    
    private StaffDTO convertToDTO(Staff staff) {
        StaffDTO dto = new StaffDTO();
        dto.setId(staff.getId());
        dto.setMaNhanVien(staff.getMaNhanVien());
        
        // Split full name into first and last name
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