package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminCustomerService;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.CustomerDTO;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/customers")
public class AdminCustomerController {
    
    @Autowired
    private AdminCustomerService adminCustomerService;
    
    @GetMapping
    public String customerManagement(Model model) {
        List<CustomerDTO> customers = adminCustomerService.getAllCustomers();
        model.addAttribute("customers", customers);
        model.addAttribute("customerStatuses", Arrays.asList("HOAT_DONG", "LOCKED"));
        return "Admin/view/QuanLyKhachHang";
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public CustomerDTO getCustomer(@PathVariable Integer id) {
        return adminCustomerService.getCustomerById(id);
    }
    
    @PostMapping
    @ResponseBody
    public CustomerDTO createCustomer(@RequestBody CustomerDTO customerDTO) {
        return adminCustomerService.createCustomer(customerDTO);
    }
    
    @PutMapping("/{id}")
    @ResponseBody
    public CustomerDTO updateCustomer(@PathVariable Integer id, @RequestBody CustomerDTO customerDTO) {
        return adminCustomerService.updateCustomer(id, customerDTO);
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public boolean deleteCustomer(@PathVariable Integer id) {
        return adminCustomerService.deleteCustomer(id);
    }
} 