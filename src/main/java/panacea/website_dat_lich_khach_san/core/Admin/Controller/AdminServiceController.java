package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.ServiceDTO;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminServiceService;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceController {
    @Autowired
    private AdminServiceService adminServiceService;

    // Danh sách dịch vụ (có phân trang)
    @GetMapping
    public String listServices(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "5") int size,
                               Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ServiceDTO> servicePage = adminServiceService.getAllServicesPaged(pageable);
        model.addAttribute("services", servicePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", Math.max(1, servicePage.getTotalPages()));
        model.addAttribute("totalItems", servicePage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("hasNext", servicePage.hasNext());
        model.addAttribute("hasPrevious", servicePage.hasPrevious());
        model.addAttribute("serviceForm", new ServiceDTO());
        return "Admin/view/QuanLyDichVu";
    }

    // Hiện form thêm dịch vụ
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("serviceForm", new ServiceDTO());
        return "Admin/view/FormDichVu";
    }

    // Xử lý thêm dịch vụ
    @PostMapping("/add")
    public String addService(@ModelAttribute("serviceForm") ServiceDTO dto) {
        adminServiceService.createService(dto);
        return "redirect:/admin/services";
    }

    // Hiện form sửa dịch vụ
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        ServiceDTO dto = adminServiceService.getServiceById(id);
        if (dto != null) {
            model.addAttribute("serviceForm", dto);
            model.addAttribute("editMode", true);
            // Truyền lại danh sách dịch vụ và các biến cần thiết cho view
            Pageable pageable = PageRequest.of(0, 5);
            Page<ServiceDTO> servicePage = adminServiceService.getAllServicesPaged(pageable);
            model.addAttribute("services", servicePage.getContent());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", Math.max(1, servicePage.getTotalPages()));
            model.addAttribute("totalItems", servicePage.getTotalElements());
            model.addAttribute("pageSize", 5);
            model.addAttribute("hasNext", servicePage.hasNext());
            model.addAttribute("hasPrevious", servicePage.hasPrevious());
            return "Admin/view/QuanLyDichVu";
        }
        return "redirect:/admin/services";
    }

    // Xử lý sửa dịch vụ
    @PostMapping("/edit/{id}")
    public String editService(@PathVariable Integer id, @ModelAttribute("serviceForm") ServiceDTO dto) {
        adminServiceService.updateService(id, dto);
        return "redirect:/admin/services";
    }

    // Xóa dịch vụ
    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable Integer id) {
        adminServiceService.deleteService(id);
        return "redirect:/admin/services";
    }

    // Xem chi tiết dịch vụ
    @GetMapping("/{id}")
    public String viewService(@PathVariable Integer id, Model model) {
        ServiceDTO dto = adminServiceService.getServiceById(id);
        if (dto != null) {
            model.addAttribute("service", dto);
            return "Admin/view/ChiTietDichVu";
        }
        return "redirect:/admin/services";
    }
} 