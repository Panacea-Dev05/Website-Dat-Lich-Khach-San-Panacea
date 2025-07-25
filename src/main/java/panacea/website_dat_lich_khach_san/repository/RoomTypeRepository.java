package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.RoomType;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {

    @Query("SELECT rt FROM RoomType rt WHERE rt.maLoaiPhong = :maLoaiPhong")
    Optional<RoomType> findByMaLoaiPhong(@Param("maLoaiPhong") String maLoaiPhong);

    @Query("SELECT rt FROM RoomType rt WHERE rt.tenLoaiPhong LIKE %:tenLoaiPhong%")
    List<RoomType> findByTenLoaiPhongContaining(@Param("tenLoaiPhong") String tenLoaiPhong);

    @Query("SELECT rt FROM RoomType rt WHERE rt.soGiuong = :soGiuong")
    List<RoomType> findBySoGiuong(@Param("soGiuong") Byte soGiuong);

    @Query("SELECT rt FROM RoomType rt WHERE rt.sucChuaToiDa >= :sucChua")
    List<RoomType> findBySucChuaToiDaGreaterThanEqual(@Param("sucChua") Byte sucChua);

    @Query("SELECT rt FROM RoomType rt WHERE rt.dienTich BETWEEN :minDienTich AND :maxDienTich")
    List<RoomType> findByDienTichBetween(@Param("minDienTich") java.math.BigDecimal minDienTich,
                                         @Param("maxDienTich") java.math.BigDecimal maxDienTich);
} 