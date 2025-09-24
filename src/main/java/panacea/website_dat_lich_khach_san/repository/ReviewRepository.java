package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Tìm đánh giá theo booking ID và khách hàng ID
    List<Review> findByDatPhongIdAndKhachHangId(Integer datPhongId, Integer khachHangId);
    
    // Tìm đánh giá theo danh sách booking ID và trạng thái
    List<Review> findByDatPhongIdInAndTrangThai(List<Integer> datPhongIds, Review.TrangThaiReview trangThai);
    
    // Tìm đánh giá theo khách hàng ID
    List<Review> findByKhachHangId(Integer khachHangId);
    
    // Tìm đánh giá theo booking ID
    List<Review> findByDatPhongId(Integer datPhongId);
    
    // Tìm đánh giá theo trạng thái
    List<Review> findByTrangThai(Review.TrangThaiReview trangThai);
    
    // Tìm đánh giá theo khách hàng ID và trạng thái
    List<Review> findByKhachHangIdAndTrangThai(Integer khachHangId, Review.TrangThaiReview trangThai);
    
    // Tìm đánh giá theo danh sách booking ID (tất cả trạng thái)
    List<Review> findByDatPhongIdIn(List<Integer> datPhongIds);
} 