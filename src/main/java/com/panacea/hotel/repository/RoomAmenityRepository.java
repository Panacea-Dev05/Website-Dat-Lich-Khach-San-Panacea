package com.panacea.hotel.repository;

import com.panacea.hotel.entity.RoomAmenity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomAmenityRepository extends JpaRepository<RoomAmenity, Long> {
} 