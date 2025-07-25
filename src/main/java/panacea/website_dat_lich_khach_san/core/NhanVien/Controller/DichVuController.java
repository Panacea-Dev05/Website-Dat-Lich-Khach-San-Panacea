package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.DichVuService;
import panacea.website_dat_lich_khach_san.repository.ServiceRepository;
import panacea.website_dat_lich_khach_san.entity.Service;
import java.math.BigDecimal;
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

    // API tạo dữ liệu mẫu dịch vụ
    @PostMapping("/init-sample-data")
    @ResponseBody
    public String initSampleData() {
        try {
            // Kiểm tra xem đã có dữ liệu chưa
            if (serviceRepository.count() > 0) {
                return "Dữ liệu dịch vụ đã tồn tại!";
            }

            // Tạo các dịch vụ mẫu
            Service[] sampleServices = {
                createService("SV001", "Dọn phòng", "Phòng", "Dịch vụ dọn phòng hàng ngày", new BigDecimal("50000"), "Lần"),
                createService("SV002", "Giặt ủi", "Giặt ủi", "Dịch vụ giặt ủi quần áo", new BigDecimal("30000"), "Kg"),
                createService("SV003", "Massage", "Spa", "Dịch vụ massage thư giãn", new BigDecimal("200000"), "Giờ"),
                createService("SV004", "Ăn sáng", "Ăn uống", "Buffet ăn sáng", new BigDecimal("150000"), "Suất"),
                createService("SV005", "Đưa đón sân bay", "Vận chuyển", "Dịch vụ đưa đón sân bay", new BigDecimal("300000"), "Chuyến"),
                createService("SV006", "Thuê xe", "Vận chuyển", "Thuê xe du lịch", new BigDecimal("500000"), "Ngày"),
                createService("SV007", "Karaoke", "Giải trí", "Phòng karaoke", new BigDecimal("100000"), "Giờ"),
                createService("SV008", "Hồ bơi", "Thể thao", "Sử dụng hồ bơi", new BigDecimal("80000"), "Ngày"),
                createService("SV009", "Phòng gym", "Thể thao", "Sử dụng phòng tập gym", new BigDecimal("60000"), "Ngày"),
                createService("SV010", "Minibar", "Ăn uống", "Đồ uống trong minibar", new BigDecimal("25000"), "Chai")
            };

            for (Service service : sampleServices) {
                serviceRepository.save(service);
            }

            return "Đã tạo " + sampleServices.length + " dịch vụ mẫu thành công!";
        } catch (Exception e) {
            return "Lỗi khi tạo dữ liệu mẫu: " + e.getMessage();
        }
    }

    private Service createService(String maDichVu, String tenDichVu, String loaiDichVu, String moTa, BigDecimal donGia, String donViTinh) {
        Service service = new Service();
        service.setMaDichVu(maDichVu);
        service.setTenDichVu(tenDichVu);
        service.setLoaiDichVu(loaiDichVu);
        service.setMoTa(moTa);
        service.setDonGia(donGia);
        service.setDonViTinh(donViTinh);
        service.setTrangThai("Hoạt động");
        return service;
    }
}