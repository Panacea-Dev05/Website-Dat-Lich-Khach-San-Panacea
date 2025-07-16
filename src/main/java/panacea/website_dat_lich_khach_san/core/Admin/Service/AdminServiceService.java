package panacea.website_dat_lich_khach_san.core.Admin.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.ServiceDetail;
import panacea.website_dat_lich_khach_san.repository.ServiceDetailRepository;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.repository.ServiceRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminServiceService {
    @Autowired
    private ServiceRepository serviceRepository;
    @Autowired
    private ServiceDetailRepository serviceDetailRepository;
    @Autowired
    private BookingRepository bookingRepository;

    // 1. Tạo danh mục dịch vụ
    public panacea.website_dat_lich_khach_san.entity.Service createService(panacea.website_dat_lich_khach_san.entity.Service service) {
        service.setMaDichVu("SV" + UUID.randomUUID().toString().substring(0, 8));
        service.setTrangThai("Hoạt động");
        return serviceRepository.save(service);
    }
    // 2. Cập nhật dịch vụ
    public panacea.website_dat_lich_khach_san.entity.Service updateService(Integer id, panacea.website_dat_lich_khach_san.entity.Service updated) {
        Optional<panacea.website_dat_lich_khach_san.entity.Service> opt = serviceRepository.findById(id);
        if (opt.isEmpty()) return null;
        panacea.website_dat_lich_khach_san.entity.Service service = opt.get();
        service.setTenDichVu(updated.getTenDichVu());
        service.setLoaiDichVu(updated.getLoaiDichVu());
        service.setMoTa(updated.getMoTa());
        service.setDonGia(updated.getDonGia());
        service.setDonViTinh(updated.getDonViTinh());
        service.setTrangThai(updated.getTrangThai());
        return serviceRepository.save(service);
    }
    // 3. Xem danh sách dịch vụ theo khách sạn
    public List<panacea.website_dat_lich_khach_san.entity.Service> listServicesByHotel(Integer hotelId) {
        return serviceRepository.findAll().stream()
            .toList();
    }
    // 4. Đặt dịch vụ cho booking
    public ServiceDetail bookService(Integer bookingId, Integer serviceId, Short soLuong, BigDecimal donGia, LocalDateTime ngaySuDung, String ghiChu) {
        ServiceDetail detail = new ServiceDetail();
        detail.setDatPhongId(bookingId);
        detail.setDichVuId(serviceId);
        detail.setSoLuong(soLuong);
        detail.setDonGiaThucTe(donGia);
        detail.setNgaySuDung(ngaySuDung);
        detail.setGhiChu(ghiChu);
        return serviceDetailRepository.save(detail);
    }
    // 5. Cập nhật service booking
    public ServiceDetail updateServiceDetail(Integer id, Short soLuong, LocalDateTime ngaySuDung, String ghiChu) {
        Optional<ServiceDetail> opt = serviceDetailRepository.findById(id);
        if (opt.isEmpty()) return null;
        ServiceDetail detail = opt.get();
        detail.setSoLuong(soLuong);
        detail.setNgaySuDung(ngaySuDung);
        detail.setGhiChu(ghiChu);
        return serviceDetailRepository.save(detail);
    }
    // 6. Hủy dịch vụ đã đặt
    public boolean cancelServiceDetail(Integer id) {
        if (serviceDetailRepository.existsById(id)) {
            serviceDetailRepository.deleteById(id);
            return true;
        }
        return false;
    }
} 