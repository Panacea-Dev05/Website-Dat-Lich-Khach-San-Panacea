package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.Booking;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByTrangThaiDatPhong(Booking.TrangThaiDatPhong trangThai);
    List<Booking> findByTrangThaiDatPhongNot(Booking.TrangThaiDatPhong trangThai);
    List<Booking> findByKhachHang(panacea.website_dat_lich_khach_san.entity.Customer khachHang);
    Page<Booking> findAll(Pageable pageable);
    Optional<Booking> findByMaDatPhong(String maDatPhong);
    
    // Thêm method để tìm booking theo room type ID
    @Query("SELECT b FROM Booking b WHERE b.roomType.id = :roomTypeId")
    List<Booking> findByRoomTypeId(@Param("roomTypeId") Integer roomTypeId);
    
    // Thêm method để tìm booking theo khách hàng ID
    @Query("SELECT b FROM Booking b WHERE b.khachHang.id = :khachHangId")
    List<Booking> findByKhachHangId(@Param("khachHangId") Integer khachHangId);
}