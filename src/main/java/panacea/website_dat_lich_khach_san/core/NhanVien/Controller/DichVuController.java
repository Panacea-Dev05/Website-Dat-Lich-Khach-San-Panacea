package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.DichVuService;
import panacea.website_dat_lich_khach_san.repository.ServiceRepository;
import panacea.website_dat_lich_khach_san.entity.Service;
import java.util.List;

@Controller
@RequestMapping("/nhanvien/dichvu")
public class DichVuController {
    private final DichVuService dichVuService;
    private final ServiceRepository serviceRepository;
    public DichVuController(DichVuService dichVuService, ServiceRepository serviceRepository) {
        this.dichVuService = dichVuService;
        this.serviceRepository = serviceRepository;
    }
    @GetMapping("")
    public String view(Model model) {
        model.addAttribute("staffName", dichVuService.getStaffName());
        model.addAttribute("serviceDetails", dichVuService.getAllServiceDetails());
        return "NhanVien/DichVu";
    }

    // API trả về tất cả dịch vụ cho JS
    @GetMapping("/all")
    @ResponseBody
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }
} 