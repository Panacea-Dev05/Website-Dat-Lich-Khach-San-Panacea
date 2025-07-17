package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.DichVuService;
import panacea.website_dat_lich_khach_san.repository.ServiceDetailRepository;
import panacea.website_dat_lich_khach_san.repository.ServiceRepository;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.entity.ServiceEntity;
import panacea.website_dat_lich_khach_san.entity.ServiceDetail;
import panacea.website_dat_lich_khach_san.entity.Booking;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/nhanvien/dichvu")
public class DichVuController {
    private final DichVuService dichVuService;
    private final ServiceDetailRepository serviceDetailRepository;
    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;
    public DichVuController(DichVuService dichVuService, ServiceDetailRepository serviceDetailRepository, ServiceRepository serviceRepository, BookingRepository bookingRepository) {
        this.dichVuService = dichVuService;
        this.serviceDetailRepository = serviceDetailRepository;
        this.serviceRepository = serviceRepository;
        this.bookingRepository = bookingRepository;
    }
    @GetMapping("")
    public String view(Model model) {
        model.addAttribute("staffName", dichVuService.getStaffName());
        model.addAttribute("services", dichVuService.getAllServices());
        model.addAttribute("bookings", bookingRepository.findAll());
        model.addAttribute("dichVus", serviceRepository.findAll());
        model.addAttribute("bookedServices", serviceDetailRepository.findAll());
        return "NhanVien/DichVu";
    }

    @PostMapping("/add-service-to-booking")
    public String addServiceToBooking(
        @RequestParam Integer bookingId,
        @RequestParam Integer dichVuId,
        @RequestParam Integer soLuong,
        @RequestParam String thoiGianSuDung
    ) {
        ServiceDetail detail = new ServiceDetail();
        detail.setDatPhongId(bookingId);
        detail.setDichVuId(dichVuId);
        detail.setSoLuong(soLuong.shortValue());
        detail.setNgaySuDung(LocalDateTime.parse(thoiGianSuDung));
        ServiceEntity service = serviceRepository.findById(dichVuId).orElse(null);
        if (service != null) detail.setDonGiaThucTe(service.getDonGia());
        serviceDetailRepository.save(detail);
        return "redirect:/nhanvien/dichvu";
    }
} 