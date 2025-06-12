package com.panacea.hotel.repository;

import com.panacea.hotel.entity.RoomTypeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeImageRepository extends JpaRepository<RoomTypeImage, Long> {
} 