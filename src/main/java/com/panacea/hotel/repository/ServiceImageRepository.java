package com.panacea.hotel.repository;

import com.panacea.hotel.entity.ServiceImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceImageRepository extends JpaRepository<ServiceImage, Long> {
} 