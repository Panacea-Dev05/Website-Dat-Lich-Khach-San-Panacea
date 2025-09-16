package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminCustomerService;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.CustomerDTO;

import java.util.Arrays;
import java.util.List;

// Controller quản lý khách hàng cho Admin
@Controller
@RequestMapping("/admin/customers")
public class AdminCustomerController {
    
    @Autowired
    private AdminCustomerService adminCustomerService;
    
    // Hiển thị trang quản lý khách hàng
    @GetMapping
    public String customerManagement(Model model) {
        List<CustomerDTO> customers = adminCustomerService.getAllCustomers();
        model.addAttribute("customers", customers);
        model.addAttribute("customerStatuses", Arrays.asList("HOAT_DONG", "LOCKED"));
        return "Admin/view/QuanLyKhachHang";
    }
    
    // Lấy thông tin khách hàng theo ID
    @GetMapping("/{id}")
    @ResponseBody
    public CustomerDTO getCustomer(@PathVariable Integer id) {
        return adminCustomerService.getCustomerById(id);
    }
    
    // Tạo khách hàng mới
    @PostMapping
    @ResponseBody
    public CustomerDTO createCustomer(@RequestBody CustomerDTO customerDTO) {
        return adminCustomerService.createCustomer(customerDTO);
    }
    
    // Cập nhật thông tin khách hàng
    @PutMapping("/{id}")
    @ResponseBody
    public CustomerDTO updateCustomer(@PathVariable Integer id, @RequestBody CustomerDTO customerDTO) {
        return adminCustomerService.updateCustomer(id, customerDTO);
    }
    
    // Xóa khách hàng
    @DeleteMapping("/{id}")
    @ResponseBody
    public boolean deleteCustomer(@PathVariable Integer id) {
        return adminCustomerService.deleteCustomer(id);
    }
} 