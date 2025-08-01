package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import panacea.website_dat_lich_khach_san.entity.BookingDetail;
@Repository

public interface BookingDetailRepository extends JpaRepository<BookingDetail, Integer> {
    // Find all BookingDetails by Booking id
    java.util.List<BookingDetail> findByDatPhongId(Integer datPhongId);
}
