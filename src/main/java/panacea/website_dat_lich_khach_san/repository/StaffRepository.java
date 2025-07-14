package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.Staff;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Integer> {
    
    // Tìm nhân viên theo mã nhân viên
    Optional<Staff> findByMaNhanVien(String maNhanVien);
    
    // Tìm nhân viên theo email
    Optional<Staff> findByEmail(String email);
    
    // Kiểm tra tồn tại theo mã nhân viên
    boolean existsByMaNhanVien(String maNhanVien);
    
    // Kiểm tra tồn tại theo email
    boolean existsByEmail(String email);
    
    // Kiểm tra tồn tại theo mã nhân viên trừ id hiện tại
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Staff s WHERE s.maNhanVien = :maNhanVien AND s.id != :id")
    boolean existsByMaNhanVienAndIdNot(@Param("maNhanVien") String maNhanVien, @Param("id") Integer id);
    
    // Kiểm tra tồn tại theo email trừ id hiện tại
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Staff s WHERE s.email = :email AND s.id != :id")
    boolean existsByEmailAndIdNot(@Param("email") String email, @Param("id") Integer id);
    
    // Tìm nhân viên theo khách sạn
    List<Staff> findByKhachSanId(Integer khachSanId);
    
    // Tìm nhân viên theo trạng thái
    List<Staff> findByTrangThai(Staff.TrangThaiStaff trangThai);
    
    // Tìm nhân viên theo chức vụ
    List<Staff> findByChucVu(String chucVu);
    
    // Tìm kiếm nhân viên theo từ khóa
    @Query("SELECT s FROM Staff s WHERE " +
           "LOWER(s.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.maNhanVien) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(s.chucVu) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Staff> searchStaff(@Param("keyword") String keyword);
    
    // Tìm nhân viên active theo khách sạn
    @Query("SELECT s FROM Staff s WHERE s.khachSanId = :khachSanId AND s.trangThai = :trangThai")
    List<Staff> findActiveStaffByHotel(@Param("khachSanId") Integer khachSanId, 
                                      @Param("trangThai") Staff.TrangThaiStaff trangThai);
    
    // Đếm số nhân viên theo khách sạn
    @Query("SELECT COUNT(s) FROM Staff s WHERE s.khachSanId = :khachSanId")
    Long countStaffByHotel(@Param("khachSanId") Integer khachSanId);
    
    // Đếm số nhân viên active theo khách sạn
    @Query("SELECT COUNT(s) FROM Staff s WHERE s.khachSanId = :khachSanId AND s.trangThai = :trangThai")
    Long countActiveStaffByHotel(@Param("khachSanId") Integer khachSanId, 
                                @Param("trangThai") Staff.TrangThaiStaff trangThai);
}