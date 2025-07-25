package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.RoomPricing;

@Repository

public interface RoomPricingRepositoty extends JpaRepository<RoomPricing, Integer> {
    // Lấy giá BASE theo loại phòng
    RoomPricing findFirstByRoomType_IdAndLoaiGia(Integer roomTypeId, panacea.website_dat_lich_khach_san.entity.RoomPricing.LoaiGia loaiGia);
}
