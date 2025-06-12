package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import panacea.website_dat_lich_khach_san.entity.CustomerPreferences;

public interface ServiceRepository extends JpaRepository<CustomerPreferences, Integer> {
}
