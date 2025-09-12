package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.SupplyRequest;
import panacea.website_dat_lich_khach_san.entity.InventoryManagement;
import panacea.website_dat_lich_khach_san.entity.Staff;
import panacea.website_dat_lich_khach_san.infrastructure.Enums.TrangThaiYeuCau;
import panacea.website_dat_lich_khach_san.repository.SupplyRequestRepository;
import panacea.website_dat_lich_khach_san.repository.InventoryManagementRepository;
import panacea.website_dat_lich_khach_san.repository.StaffRepository;
import panacea.website_dat_lich_khach_san.core.NhanVien.DTO.SupplyRequestDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplyRequestService {

    @Autowired
    private SupplyRequestRepository supplyRequestRepository;

    @Autowired
    private InventoryManagementRepository inventoryRepository;

    @Autowired
    private StaffRepository staffRepository;

    // Tạo yêu cầu bổ sung (Staff)
    public SupplyRequest createSupplyRequest(SupplyRequest request, Integer staffId) {
        validateSupplyRequest(request);
        
        // Kiểm tra vật phẩm tồn tại
        Optional<InventoryManagement> item = inventoryRepository.findById(request.getVatPhamId());
        if (item.isEmpty()) {
            throw new RuntimeException("Vật phẩm không tồn tại!");
        }
        
        // Kiểm tra nhân viên tồn tại
        Optional<Staff> staff = staffRepository.findById(staffId);
        if (staff.isEmpty()) {
            throw new RuntimeException("Nhân viên không tồn tại!");
        }
        
        request.setNhanVienYeuCau(staffId);
        request.setTrangThai(TrangThaiYeuCau.CHO_DUYET);
        request.setNgayYeuCau(LocalDateTime.now());
        
        return supplyRequestRepository.save(request);
    }
    
    // Phê duyệt yêu cầu (Admin)
    public SupplyRequest approveRequest(Integer requestId, Integer adminId, String ghiChu) {
        Optional<SupplyRequest> requestOpt = supplyRequestRepository.findById(requestId);
        if (requestOpt.isEmpty()) {
            throw new RuntimeException("Yêu cầu không tồn tại!");
        }
        
        SupplyRequest request = requestOpt.get();
        if (request.getTrangThai() != TrangThaiYeuCau.CHO_DUYET) {
            throw new RuntimeException("Yêu cầu đã được xử lý!");
        }
        
        request.setTrangThai(TrangThaiYeuCau.DA_DUYET);
        request.setAdminPheDuyet(adminId);
        request.setNgayPheDuyet(LocalDateTime.now());
        request.setGhiChuAdmin(ghiChu);
        
        return supplyRequestRepository.save(request);
    }
    
    // Từ chối yêu cầu (Admin)
    public SupplyRequest rejectRequest(Integer requestId, Integer adminId, String lyDoTuChoi) {
        Optional<SupplyRequest> requestOpt = supplyRequestRepository.findById(requestId);
        if (requestOpt.isEmpty()) {
            throw new RuntimeException("Yêu cầu không tồn tại!");
        }
        
        SupplyRequest request = requestOpt.get();
        if (request.getTrangThai() != TrangThaiYeuCau.CHO_DUYET) {
            throw new RuntimeException("Yêu cầu đã được xử lý!");
        }
        
        request.setTrangThai(TrangThaiYeuCau.TU_CHOI);
        request.setAdminPheDuyet(adminId);
        request.setNgayPheDuyet(LocalDateTime.now());
        request.setGhiChuAdmin(lyDoTuChoi);
        
        return supplyRequestRepository.save(request);
    }
    
    // Đánh dấu đã thực hiện (Admin)
    public SupplyRequest markAsCompleted(Integer requestId) {
        Optional<SupplyRequest> requestOpt = supplyRequestRepository.findById(requestId);
        if (requestOpt.isEmpty()) {
            throw new RuntimeException("Yêu cầu không tồn tại!");
        }
        
        SupplyRequest request = requestOpt.get();
        if (request.getTrangThai() != TrangThaiYeuCau.DA_DUYET) {
            throw new RuntimeException("Yêu cầu chưa được phê duyệt!");
        }
        
        request.setTrangThai(TrangThaiYeuCau.DA_THUC_HIEN);
        return supplyRequestRepository.save(request);
    }
    
    // Lấy tất cả yêu cầu
    public List<SupplyRequest> getAllRequests() {
        return supplyRequestRepository.findAll();
    }
    
    // Lấy yêu cầu theo nhân viên
    public List<SupplyRequest> getRequestsByStaff(Integer staffId) {
        return supplyRequestRepository.findByNhanVienYeuCau(staffId);
    }
    
    // Lấy yêu cầu theo trạng thái
    public List<SupplyRequest> getRequestsByStatus(TrangThaiYeuCau status) {
        return supplyRequestRepository.findByTrangThai(status);
    }
    
    // Lấy yêu cầu chờ duyệt
    public List<SupplyRequest> getPendingRequests() {
        return supplyRequestRepository.findByTrangThai(TrangThaiYeuCau.CHO_DUYET);
    }
    
    // Validation
    private void validateSupplyRequest(SupplyRequest request) {
        if (request.getVatPhamId() == null) {
            throw new RuntimeException("Vật phẩm không được để trống!");
        }
        
        if (request.getSoLuongYeuCau() == null || request.getSoLuongYeuCau() <= 0) {
            throw new RuntimeException("Số lượng yêu cầu phải lớn hơn 0!");
        }
        
        if (request.getLyDoYeuCau() == null || request.getLyDoYeuCau().trim().isEmpty()) {
            throw new RuntimeException("Lý do yêu cầu không được để trống!");
        }
        
        if (request.getLyDoYeuCau().length() > 500) {
            throw new RuntimeException("Lý do yêu cầu không được vượt quá 500 ký tự!");
        }
    }
    
    // Chuyển đổi entity sang DTO
    private SupplyRequestDTO convertToDTO(SupplyRequest request) {
        SupplyRequestDTO dto = new SupplyRequestDTO();
        dto.setId(request.getId());
        dto.setVatPhamId(request.getVatPhamId());
        dto.setSoLuongYeuCau(request.getSoLuongYeuCau());
        dto.setLyDoYeuCau(request.getLyDoYeuCau());
        dto.setNhanVienYeuCau(request.getNhanVienYeuCau());
        dto.setTrangThai(request.getTrangThai());
        dto.setAdminPheDuyet(request.getAdminPheDuyet());
        dto.setNgayYeuCau(request.getNgayYeuCau());
        dto.setNgayPheDuyet(request.getNgayPheDuyet());
        dto.setGhiChuAdmin(request.getGhiChuAdmin());
        dto.setMucDoUuTien(request.getMucDoUuTien());
        dto.setUuidId(request.getUuidId());
        dto.setCreatedDate(request.getCreatedDate());
        dto.setLastModifiedDate(request.getLastModifiedDate());
        
        // Lấy thông tin vật phẩm
        if (request.getVatPhamId() != null) {
            Optional<InventoryManagement> item = inventoryRepository.findById(request.getVatPhamId());
            if (item.isPresent()) {
                dto.setTenVatPham(item.get().getTenVatPham());
                dto.setSoLuongTon(item.get().getSoLuongTon() != null ? item.get().getSoLuongTon().intValue() : 0);
            }
        }
        
        // Lấy thông tin nhân viên yêu cầu
        if (request.getNhanVienYeuCau() != null) {
            Optional<Staff> staff = staffRepository.findById(request.getNhanVienYeuCau());
            if (staff.isPresent()) {
                dto.setTenNhanVienYeuCau(staff.get().getHoTen());
            }
        }
        
        // Lấy thông tin admin phê duyệt
        if (request.getAdminPheDuyet() != null) {
            Optional<Staff> admin = staffRepository.findById(request.getAdminPheDuyet());
            if (admin.isPresent()) {
                dto.setTenAdminPheDuyet(admin.get().getHoTen());
            }
        }
        
        return dto;
    }
    
    // Các method trả về DTO
    public List<SupplyRequestDTO> getAllRequestsDTO() {
        return getAllRequests().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<SupplyRequestDTO> getRequestsByStaffDTO(Integer staffId) {
        return getRequestsByStaff(staffId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<SupplyRequestDTO> getRequestsByStatusDTO(TrangThaiYeuCau status) {
        return getRequestsByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<SupplyRequestDTO> getPendingRequestsDTO() {
        return getPendingRequests().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}