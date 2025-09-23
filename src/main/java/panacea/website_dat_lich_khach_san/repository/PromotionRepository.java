package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.Promotion;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionRepository extends JpaRepository<Promotion, Integer> {
    
    /**
     * Tìm khuyến mãi theo mã
     */
    Optional<Promotion> findByMaKhuyenMai(String maKhuyenMai);
    
    /**
     * Tìm khuyến mãi đang hoạt động trong khoảng thời gian
     */
    @Query("SELECT p FROM Promotion p WHERE p.trangThai = :trangThai " +
           "AND p.ngayBatDau <= :ngayKetThuc AND p.ngayKetThuc >= :ngayBatDau")
    List<Promotion> findByTrangThaiAndNgayBatDauLessThanEqualAndNgayKetThucGreaterThanEqual(
        @Param("trangThai") Promotion.TrangThaiPromotion trangThai,
        @Param("ngayKetThuc") LocalDate ngayKetThuc,
        @Param("ngayBatDau") LocalDate ngayBatDau);
}
