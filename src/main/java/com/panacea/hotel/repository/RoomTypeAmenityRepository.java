package com.panacea.hotel.repository;

import com.panacea.hotel.entity.RoomTypeAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomTypeAmenityRepository extends JpaRepository<RoomTypeAmenity, Long> {
} 