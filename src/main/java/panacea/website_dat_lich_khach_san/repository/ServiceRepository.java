package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import panacea.website_dat_lich_khach_san.entity.Service;
 
@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer> {
} 