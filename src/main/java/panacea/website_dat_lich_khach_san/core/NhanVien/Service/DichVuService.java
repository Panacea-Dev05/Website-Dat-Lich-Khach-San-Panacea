package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import org.springframework.beans.factory.annotation.Autowired;

import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.entity.Service;
import panacea.website_dat_lich_khach_san.entity.ServiceDetail;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.repository.ServiceDetailRepository;
import panacea.website_dat_lich_khach_san.repository.ServiceRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@org.springframework.stereotype.Service
public class DichVuService {
    @Autowired
    private ServiceDetailRepository serviceDetailRepository;
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private BookingRepository bookingRepository;

    public String getStaffName() {
        return "Nguyễn Văn A";
    }

    public List<ServiceDetail> getAllServiceDetails() {
        return serviceDetailRepository.findAll();
    }

    // CRUD cho Service
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }
    public Service getServiceById(Integer id) {
        return serviceRepository.findById(id).orElse(null);
    }
    public Service createService(Service service) {
        return serviceRepository.save(service);
    }
    public Service updateService(Integer id, Service updated) {
        return serviceRepository.findById(id).map(s -> {
            s.setMaDichVu(updated.getMaDichVu());
            s.setTenDichVu(updated.getTenDichVu());
            s.setLoaiDichVu(updated.getLoaiDichVu());
            s.setMoTa(updated.getMoTa());
            s.setDonGia(updated.getDonGia());
            s.setDonViTinh(updated.getDonViTinh());
            s.setTrangThai(updated.getTrangThai());
            return serviceRepository.save(s);
        }).orElse(null);
    }
    public boolean deleteService(Integer id) {
        if (serviceRepository.existsById(id)) {
            serviceRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // CRUD cho ServiceDetail (Đặt dịch vụ trong booking)
    public ServiceDetail createServiceDetail(ServiceDetail serviceDetail) {
        // Lấy đơn giá từ Service entity để tính donGiaThucTe
        Service service = serviceRepository.findById(serviceDetail.getDichVuId()).orElse(null);
        if (service != null) {
            BigDecimal donGia = service.getDonGia();
            if (donGia != null && serviceDetail.getSoLuong() != null) {
                serviceDetail.setDonGiaThucTe(donGia);
                // Total is calculated on the frontend or when retrieving, for now just save donGiaThucTe
            }
        }
        return serviceDetailRepository.save(serviceDetail);
    }

    // Get all bookings
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
} 