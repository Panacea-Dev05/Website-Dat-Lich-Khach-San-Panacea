package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.Room;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotelIdAndTrangThai(Integer hotelId, Room.TrangThaiPhong trangThai);
} 