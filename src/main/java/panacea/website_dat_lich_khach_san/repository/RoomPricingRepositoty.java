package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.RoomPricing;
import java.util.Optional;
import java.util.List;
import panacea.website_dat_lich_khach_san.entity.RoomType;

@Repository

public interface RoomPricingRepositoty extends JpaRepository<RoomPricing, Integer> {
<<<<<<< HEAD
    // Lấy giá BASE theo loại phòng
    RoomPricing findFirstByRoomType_IdAndLoaiGia(Integer roomTypeId, panacea.website_dat_lich_khach_san.entity.RoomPricing.LoaiGia loaiGia);
=======
    Optional<RoomPricing> findFirstByRoomTypeIdAndLoaiGiaOrderByNgayBatDauDesc(Integer roomTypeId, RoomPricing.LoaiGia loaiGia);
    List<RoomPricing> findByRoomTypeId(Integer roomTypeId);
>>>>>>> c94b5f18d09cd35734cbddc478ddd32df49c736e
}
