package panacea.website_dat_lich_khach_san.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import panacea.website_dat_lich_khach_san.entity.ServiceDetail;

@Repository
public interface ServiceDetailRepository extends JpaRepository<ServiceDetail, Integer> {
    // Lấy danh sách dịch vụ đã sử dụng theo bookingId (datPhongId)
    java.util.List<ServiceDetail> findByDatPhongId(Integer datPhongId);
}
