package panacea.website_dat_lich_khach_san.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import panacea.website_dat_lich_khach_san.entity.Room;

@Repository

public interface RoomRepository extends JpaRepository<Room, Integer> {

//    List<Room> findByHotelIdAndTrangThai(Integer hotelId, Room.TrangThaiPhong trangThai);
    
    @Query("SELECT r FROM Room r WHERE r.roomType.id = :roomTypeId")
    List<Room> findByRoomTypeId(@Param("roomTypeId") Integer roomTypeId);
    
    @Query("SELECT r FROM Room r WHERE r.trangThai = :trangThai")
    List<Room> findByTrangThai(@Param("trangThai") Room.TrangThaiPhong trangThai);
    
    @Query("SELECT r FROM Room r WHERE r.soPhong LIKE %:soPhong%")
    List<Room> findBySoPhongContaining(@Param("soPhong") String soPhong);
    
    @Query("SELECT r FROM Room r WHERE r.tang = :tang")
    List<Room> findByTang(@Param("tang") Byte tang);
    
    @Query("SELECT r FROM Room r WHERE r.giaCoBan BETWEEN :minPrice AND :maxPrice")
    List<Room> findByGiaCoBanBetween(@Param("minPrice") java.math.BigDecimal minPrice, 
                                    @Param("maxPrice") java.math.BigDecimal maxPrice);
    
    @Query("SELECT COUNT(r) FROM Room r WHERE r.roomType.id = :roomTypeId")
    long countByRoomTypeId(@Param("roomTypeId") Integer roomTypeId);

} 