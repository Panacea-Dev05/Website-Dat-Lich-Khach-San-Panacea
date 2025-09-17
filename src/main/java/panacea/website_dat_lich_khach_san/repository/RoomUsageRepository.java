package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.RoomUsage;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomUsageRepository extends JpaRepository<RoomUsage, Integer> {

    // Tìm lịch sử sử dụng theo số phòng
    @Query("SELECT ru FROM RoomUsage ru WHERE ru.soPhong = :soPhong ORDER BY ru.ngaySuDung DESC")
    List<RoomUsage> findBySoPhongOrderByNgaySuDungDesc(@Param("soPhong") String soPhong);

    // Tìm lịch sử sử dụng theo khoảng thời gian
    @Query("SELECT ru FROM RoomUsage ru WHERE ru.ngaySuDung BETWEEN :startDate AND :endDate ORDER BY ru.ngaySuDung DESC")
    List<RoomUsage> findByNgaySuDungBetweenOrderByNgaySuDungDesc(
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);

    // Tìm lịch sử sử dụng theo số phòng và khoảng thời gian
    @Query("SELECT ru FROM RoomUsage ru WHERE ru.soPhong = :soPhong AND ru.ngaySuDung BETWEEN :startDate AND :endDate ORDER BY ru.ngaySuDung DESC")
    List<RoomUsage> findBySoPhongAndNgaySuDungBetweenOrderByNgaySuDungDesc(
            @Param("soPhong") String soPhong,
            @Param("startDate") LocalDateTime startDate, 
            @Param("endDate") LocalDateTime endDate);

    // Tìm lịch sử sử dụng theo loại vật phẩm
    @Query("SELECT ru FROM RoomUsage ru WHERE ru.loaiSuDung = :loaiSuDung ORDER BY ru.ngaySuDung DESC")
    List<RoomUsage> findByLoaiSuDungOrderByNgaySuDungDesc(@Param("loaiSuDung") String loaiSuDung);

    // Tìm lịch sử sử dụng theo tên vật phẩm
    @Query("SELECT ru FROM RoomUsage ru WHERE ru.tenVatPham LIKE %:tenVatPham% ORDER BY ru.ngaySuDung DESC")
    List<RoomUsage> findByTenVatPhamContainingOrderByNgaySuDungDesc(@Param("tenVatPham") String tenVatPham);

    // Thống kê sử dụng theo số phòng
    @Query("SELECT ru.soPhong, ru.tenVatPham, SUM(ru.soLuongSuDung) as totalUsage " +
           "FROM RoomUsage ru WHERE ru.soPhong = :soPhong " +
           "GROUP BY ru.soPhong, ru.tenVatPham " +
           "ORDER BY totalUsage DESC")
    List<Object[]> getUsageStatisticsByRoom(@Param("soPhong") String soPhong);

    // Thống kê sử dụng theo loại vật phẩm
    @Query("SELECT ru.loaiSuDung, ru.tenVatPham, SUM(ru.soLuongSuDung) as totalUsage " +
           "FROM RoomUsage ru " +
           "GROUP BY ru.loaiSuDung, ru.tenVatPham " +
           "ORDER BY ru.loaiSuDung, totalUsage DESC")
    List<Object[]> getUsageStatisticsByType();

    // Tìm lịch sử sử dụng gần đây nhất
    @Query("SELECT ru FROM RoomUsage ru ORDER BY ru.ngaySuDung DESC")
    List<RoomUsage> findRecentUsage();
}
