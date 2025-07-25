package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.ServiceDetail;
import panacea.website_dat_lich_khach_san.repository.ServiceDetailRepository;
import panacea.website_dat_lich_khach_san.repository.ServiceRepository;
import panacea.website_dat_lich_khach_san.entity.ServiceEntity;

import java.util.List;

@Service
public class DichVuService {
    @Autowired
    private ServiceDetailRepository serviceDetailRepository;
    @Autowired
    private ServiceRepository serviceRepository;

    public String getStaffName() {
        return "Nguyễn Văn A";
    }

    public List<ServiceDetail> getAllServiceDetails() {
        return serviceDetailRepository.findAll();
    }
    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }
} 