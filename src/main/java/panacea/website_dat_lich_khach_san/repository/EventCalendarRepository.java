package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.EventCalendar;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventCalendarRepository extends JpaRepository<EventCalendar, Integer> {
    
    // Tìm sự kiện theo khách sạn
    List<EventCalendar> findByKhachSanId(Integer khachSanId);
    
    // Tìm sự kiện đang hoạt động theo khách sạn
    @Query("SELECT e FROM EventCalendar e WHERE e.khachSanId = :khachSanId AND e.trangThai = 'Hoạt động'")
    List<EventCalendar> findActiveEventsByHotelId(@Param("khachSanId") Integer khachSanId);
    
    // Tìm sự kiện hiện tại (đang diễn ra)
    @Query("SELECT e FROM EventCalendar e WHERE e.khachSanId = :khachSanId " +
           "AND e.ngayBatDau <= :now AND e.ngayKetThuc >= :now " +
           "AND e.trangThai = 'Hoạt động'")
    List<EventCalendar> findCurrentEvents(@Param("khachSanId") Integer khachSanId, 
                                         @Param("now") LocalDateTime now);
    
    // Tìm sự kiện sắp tới
    @Query("SELECT e FROM EventCalendar e WHERE e.khachSanId = :khachSanId " +
           "AND e.ngayBatDau > :now AND e.trangThai = 'Hoạt động' " +
           "ORDER BY e.ngayBatDau ASC")
    List<EventCalendar> findUpcomingEvents(@Param("khachSanId") Integer khachSanId, 
                                          @Param("now") LocalDateTime now);
    
    // Tìm sự kiện trong khoảng thời gian
    @Query("SELECT e FROM EventCalendar e WHERE e.khachSanId = :khachSanId " +
           "AND ((e.ngayBatDau BETWEEN :startDate AND :endDate) " +
           "OR (e.ngayKetThuc BETWEEN :startDate AND :endDate) " +
           "OR (e.ngayBatDau <= :startDate AND e.ngayKetThuc >= :endDate))")
    List<EventCalendar> findEventsInDateRange(@Param("khachSanId") Integer khachSanId,
                                             @Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);
    
    // Tìm sự kiện có tác động đến giá
    @Query("SELECT e FROM EventCalendar e WHERE e.khachSanId = :khachSanId " +
           "AND e.tacDongGia = true AND e.trangThai = 'Hoạt động' " +
           "AND ((e.ngayBatDau BETWEEN :startDate AND :endDate) " +
           "OR (e.ngayKetThuc BETWEEN :startDate AND :endDate) " +
           "OR (e.ngayBatDau <= :startDate AND e.ngayKetThuc >= :endDate))")
    List<EventCalendar> findPriceImpactingEvents(@Param("khachSanId") Integer khachSanId,
                                                @Param("startDate") LocalDateTime startDate,
                                                @Param("endDate") LocalDateTime endDate);
    
    // Tìm sự kiện theo loại
    List<EventCalendar> findByKhachSanIdAndLoaiSuKien(Integer khachSanId, String loaiSuKien);
    
    // Tìm sự kiện theo tên (tìm kiếm gần đúng)
    @Query("SELECT e FROM EventCalendar e WHERE e.khachSanId = :khachSanId " +
           "AND LOWER(e.tenSuKien) LIKE LOWER(CONCAT('%', :tenSuKien, '%'))")
    List<EventCalendar> findByTenSuKienContaining(@Param("khachSanId") Integer khachSanId,
                                                 @Param("tenSuKien") String tenSuKien);
    
    // Đếm số sự kiện đang hoạt động
    @Query("SELECT COUNT(e) FROM EventCalendar e WHERE e.khachSanId = :khachSanId " +
           "AND e.trangThai = 'Hoạt động'")
    Long countActiveEvents(@Param("khachSanId") Integer khachSanId);
    
    // Tìm sự kiện theo UUID
    Optional<EventCalendar> findByUuidId(java.util.UUID uuidId);
}