package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import panacea.website_dat_lich_khach_san.entity.ServiceEntity;
 
@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Integer> {
    /**
     * Kiểm tra xem mã dịch vụ đã tồn tại chưa
     * @param maDichVu - Mã dịch vụ cần kiểm tra
     * @return boolean - true nếu đã tồn tại, false nếu chưa
     */
    boolean existsByMaDichVu(String maDichVu);
} 