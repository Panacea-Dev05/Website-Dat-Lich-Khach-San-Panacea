package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("SELECT r FROM Review r WHERE r.trangThai = :trangThai AND r.datPhongId IN (SELECT bd.datPhongId FROM BookingDetail bd WHERE bd.phongId = :roomId)")
    List<Review> findByRoomIdAndTrangThai(@Param("roomId") Integer roomId, @Param("trangThai") Review.TrangThaiReview trangThai);
} 