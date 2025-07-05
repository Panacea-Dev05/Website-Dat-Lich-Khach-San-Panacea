package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import panacea.website_dat_lich_khach_san.entity.Service;

public interface ServiceRepository extends JpaRepository<Service, Integer> {
    // Có thể bổ sung các query method custom ở đây nếu cần
} 