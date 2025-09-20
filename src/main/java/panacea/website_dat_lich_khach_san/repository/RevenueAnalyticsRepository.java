package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.RevenueAnalytics;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RevenueAnalyticsRepository extends JpaRepository<RevenueAnalytics, Integer> {
    
    /**
     * Tìm báo cáo doanh thu theo ngày
     * @param ngay ngày cần tìm
     * @return Optional chứa RevenueAnalytics nếu tìm thấy
     */
    Optional<RevenueAnalytics> findByNgay(LocalDate ngay);

    /**
     * Tìm tất cả báo cáo doanh thu theo tháng và năm
     * @param month tháng (1-12)
     * @param year năm
     * @return danh sách RevenueAnalytics
     */
    @Query("SELECT ra FROM RevenueAnalytics ra WHERE MONTH(ra.ngay) = :month AND YEAR(ra.ngay) = :year")
    List<RevenueAnalytics> findByMonthAndYear(@Param("month") int month, @Param("year") int year);

    /**
     * Lấy tỷ lệ lấp đầy trung bình theo tháng trong năm
     * @param year năm
     * @return danh sách Object[] chứa [month, occupancyRate]
     */
    @Query("SELECT MONTH(ra.ngay) as month, AVG(ra.tyLeLayDay) as occupancyRate " +
           "FROM RevenueAnalytics ra WHERE YEAR(ra.ngay) = :year " +
           "GROUP BY MONTH(ra.ngay) ORDER BY MONTH(ra.ngay)")
    List<Object[]> getMonthlyOccupancyRateByYear(@Param("year") int year);
}