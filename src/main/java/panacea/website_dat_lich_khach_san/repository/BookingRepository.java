package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.Booking;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByTrangThaiDatPhong(Booking.TrangThaiDatPhong trangThai);
    List<Booking> findByTrangThaiDatPhongNot(Booking.TrangThaiDatPhong trangThai);
    List<Booking> findByKhachHang(panacea.website_dat_lich_khach_san.entity.Customer khachHang);
    @Override
    Page<Booking> findAll(Pageable pageable);
    Optional<Booking> findByMaDatPhong(String maDatPhong);
    
    // Thêm method để tìm booking theo room type ID
    @Query("SELECT b FROM Booking b WHERE b.roomType.id = :roomTypeId")
    List<Booking> findByRoomTypeId(@Param("roomTypeId") Integer roomTypeId);
    
    // Thêm method để tìm booking theo khách hàng ID
    @Query("SELECT b FROM Booking b WHERE b.khachHang.id = :khachHangId")
    List<Booking> findByKhachHangId(@Param("khachHangId") Integer khachHangId);
    
    // Tính tổng doanh thu từ booking đã xác nhận
    @Query("SELECT SUM(b.tongThanhToan) FROM Booking b WHERE b.trangThaiDatPhong = 'DA_XAC_NHAN'")
    BigDecimal sumTotalRevenue();
    
    // Tính tổng doanh thu theo khoảng thời gian
    @Query("SELECT SUM(b.tongThanhToan) FROM Booking b WHERE b.trangThaiDatPhong = 'DA_XAC_NHAN' " +
           "AND DATE(b.ngayDat) BETWEEN :startDate AND :endDate")
    BigDecimal sumRevenueByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Tính tổng doanh thu theo tháng
    @Query("SELECT SUM(b.tongThanhToan) FROM Booking b WHERE b.trangThaiDatPhong = 'DA_XAC_NHAN' " +
           "AND YEAR(b.ngayDat) = :year AND MONTH(b.ngayDat) = :month")
    BigDecimal sumRevenueByMonth(@Param("month") int month, @Param("year") int year);
    
    // Lấy doanh thu theo tháng trong năm
    @Query("SELECT MONTH(b.ngayDat) as month, SUM(b.tongThanhToan) as revenue " +
           "FROM Booking b WHERE b.trangThaiDatPhong = 'DA_XAC_NHAN' AND YEAR(b.ngayDat) = :year " +
           "GROUP BY MONTH(b.ngayDat) ORDER BY MONTH(b.ngayDat)")
    List<Object[]> getMonthlyRevenueByYear(@Param("year") int year);
    
    // Đếm số booking theo trạng thái
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.trangThaiDatPhong = :trangThai")
    long countByTrangThaiDatPhong(@Param("trangThai") Booking.TrangThaiDatPhong trangThai);
    
    // Đếm số booking theo loại phòng
    @Query("SELECT b.roomType.tenLoaiPhong, COUNT(b) FROM Booking b WHERE b.trangThaiDatPhong = 'DA_XAC_NHAN' " +
           "GROUP BY b.roomType.tenLoaiPhong ORDER BY COUNT(b) DESC")
    List<Object[]> countBookingsByRoomType();
    
    // Đếm số booking trong khoảng thời gian
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.trangThaiDatPhong = 'DA_XAC_NHAN' " +
           "AND DATE(b.ngayDat) BETWEEN :startDate AND :endDate")
    long countBookingsByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    // Lấy booking theo ngày nhận phòng để tính occupancy
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.trangThaiDatPhong = 'DA_XAC_NHAN' " +
           "AND b.ngayNhanPhong = :date")
    long countBookingsByCheckInDate(@Param("date") LocalDate date);
    
    // Lấy booking trong khoảng ngày nhận phòng
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.trangThaiDatPhong = 'DA_XAC_NHAN' " +
           "AND b.ngayNhanPhong BETWEEN :startDate AND :endDate")
    long countBookingsByCheckInDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}