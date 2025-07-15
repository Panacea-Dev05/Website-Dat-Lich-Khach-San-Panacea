package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import panacea.website_dat_lich_khach_san.entity.RoomImages;

@Repository

public interface RoomImagesRepositoty extends JpaRepository<RoomImages, Integer> {
    // Lấy ảnh theo loại phòng
    java.util.List<RoomImages> findByLoaiPhong_Id(Integer loaiPhongId);
    // Lấy ảnh theo phòng
    java.util.List<RoomImages> findByPhong_Id(Integer phongId);
}
