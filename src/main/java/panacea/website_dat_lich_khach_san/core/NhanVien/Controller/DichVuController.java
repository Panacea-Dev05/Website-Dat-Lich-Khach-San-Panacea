package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import jakarta.mail.Service;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;

import panacea.website_dat_lich_khach_san.core.NhanVien.Service.DichVuService;
import panacea.website_dat_lich_khach_san.repository.ServiceRepository;
import panacea.website_dat_lich_khach_san.repository.ServiceDetailRepository;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.repository.InventoryManagementRepository;


import panacea.website_dat_lich_khach_san.entity.ServiceEntity;
import panacea.website_dat_lich_khach_san.entity.ServiceDetail;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.entity.InventoryManagement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/nhanvien/dichvu")
public class DichVuController {

    private final DichVuService dichVuService;
    private final ServiceDetailRepository serviceDetailRepository;
    private final ServiceRepository serviceRepository;
    private final BookingRepository bookingRepository;
    private final InventoryManagementRepository inventoryManagementRepository;

    public DichVuController(
            DichVuService dichVuService,
            ServiceDetailRepository serviceDetailRepository,
            ServiceRepository serviceRepository,
            BookingRepository bookingRepository,
            InventoryManagementRepository inventoryManagementRepository
    ) {
        this.dichVuService = dichVuService;
        this.serviceDetailRepository = serviceDetailRepository;
        this.serviceRepository = serviceRepository;
        this.bookingRepository = bookingRepository;
        this.inventoryManagementRepository = inventoryManagementRepository;
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

    // API trả về tất cả dịch vụ cho JS
    @GetMapping("/all")
    @ResponseBody
    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }

    // API trả về tất cả inventory items cho JS
    @GetMapping("/inventory/all")
    @ResponseBody
    public List<InventoryManagement> getAllInventoryItems() {
        return inventoryManagementRepository.findAll();
    }


}
