package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByTrangThaiDatPhong(Booking.TrangThaiDatPhong trangThai);
    Page<Booking> findAll(Pageable pageable);
} 