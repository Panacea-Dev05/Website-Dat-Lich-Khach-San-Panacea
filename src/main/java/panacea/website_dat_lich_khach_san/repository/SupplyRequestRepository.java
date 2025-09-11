package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.SupplyRequest;
import panacea.website_dat_lich_khach_san.infrastructure.Enums.TrangThaiYeuCau;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SupplyRequestRepository extends JpaRepository<SupplyRequest, Integer> {
    
    // Tìm yêu cầu theo nhân viên yêu cầu
    List<SupplyRequest> findByNhanVienYeuCau(Integer nhanVienYeuCau);
    
    // Tìm yêu cầu theo trạng thái
    List<SupplyRequest> findByTrangThai(TrangThaiYeuCau trangThai);
    
    // Tìm yêu cầu theo admin phê duyệt
    List<SupplyRequest> findByAdminPheDuyet(Integer adminPheDuyet);
    
    // Tìm yêu cầu theo vật phẩm
    List<SupplyRequest> findByVatPhamId(Integer vatPhamId);
    
    // Tìm yêu cầu theo khoảng thời gian
    @Query("SELECT sr FROM SupplyRequest sr WHERE sr.ngayYeuCau BETWEEN :startDate AND :endDate")
    List<SupplyRequest> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Tìm yêu cầu theo mức độ ưu tiên
    List<SupplyRequest> findByMucDoUuTien(String mucDoUuTien);
    
    // Tìm yêu cầu chờ duyệt theo mức độ ưu tiên
    @Query("SELECT sr FROM SupplyRequest sr WHERE sr.trangThai = :trangThai ORDER BY " +
           "CASE sr.mucDoUuTien " +
           "WHEN 'Khẩn cấp' THEN 1 " +
           "WHEN 'Cao' THEN 2 " +
           "WHEN 'Bình thường' THEN 3 " +
           "WHEN 'Thấp' THEN 4 " +
           "ELSE 5 END, sr.ngayYeuCau ASC")
    List<SupplyRequest> findByTrangThaiOrderByPriority(@Param("trangThai") TrangThaiYeuCau trangThai);
    
    // Đếm số yêu cầu theo trạng thái
    long countByTrangThai(TrangThaiYeuCau trangThai);
    
    // Đếm số yêu cầu của nhân viên theo trạng thái
    long countByNhanVienYeuCauAndTrangThai(Integer nhanVienYeuCau, TrangThaiYeuCau trangThai);
    
    // Tìm yêu cầu gần đây nhất của nhân viên
    @Query("SELECT sr FROM SupplyRequest sr WHERE sr.nhanVienYeuCau = :nhanVienYeuCau ORDER BY sr.ngayYeuCau DESC")
    List<SupplyRequest> findRecentRequestsByStaff(@Param("nhanVienYeuCau") Integer nhanVienYeuCau);
}