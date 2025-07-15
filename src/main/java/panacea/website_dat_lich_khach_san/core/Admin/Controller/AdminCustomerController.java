package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
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
        model.addAttribute("customerStatuses", Arrays.asList("HOAT_DONG", "TAM_KHOA"));
        return "Admin/view/QuanLyKhachHang";
    }
    
    // Hiển thị form thêm khách hàng ở trang riêng
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("customerStatuses", Arrays.asList("HOAT_DONG", "TAM_KHOA"));
        model.addAttribute("gioiTinhs", Arrays.asList("Nam", "Nữ", "Khác"));
        return "Admin/QuanLyKhachSan/ThemKhachHang";
    }

    // Xử lý thêm khách hàng (giữ nguyên logic cũ)
    @PostMapping("/add")
    public String addCustomer(@ModelAttribute panacea.website_dat_lich_khach_san.infrastructure.DTO.CustomerDTO customerDTO, 
                            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {
        try {
            if (customerDTO.getHo() == null || customerDTO.getHo().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Họ không được để trống!");
                return "redirect:/admin/customers/add";
            }
            if (customerDTO.getTen() == null || customerDTO.getTen().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Tên không được để trống!");
                return "redirect:/admin/customers/add";
            }
            if (customerDTO.getEmail() == null || customerDTO.getEmail().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Email không được để trống!");
                return "redirect:/admin/customers/add";
            }
            if (customerDTO.getSoDienThoai() == null || customerDTO.getSoDienThoai().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Số điện thoại không được để trống!");
                return "redirect:/admin/customers/add";
            }
            adminCustomerService.createCustomer(customerDTO);
            redirectAttributes.addFlashAttribute("success", "Thêm khách hàng thành công!");
            return "redirect:/admin/customers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi thêm khách hàng: " + e.getMessage());
            return "redirect:/admin/customers/add";
        }
    }
    
    // Hiển thị form sửa khách hàng
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        CustomerDTO customer = adminCustomerService.getCustomerById(id);
        if (customer == null) {
            return "redirect:/admin/customers";
        }
        model.addAttribute("customer", customer);
        model.addAttribute("customerStatuses", Arrays.asList("HOAT_DONG", "TAM_KHOA"));
        model.addAttribute("gioiTinhs", Arrays.asList("Nam", "Nữ", "Khác"));
        model.addAttribute("isEdit", true);
        return "Admin/view/QuanLyKhachHang";
    }
    
    // Xử lý sửa khách hàng
    @PostMapping("/edit/{id}")
    public String updateCustomer(@PathVariable Integer id, 
                               @ModelAttribute CustomerDTO customerDTO,
                               RedirectAttributes redirectAttributes) {
        try {
            // Validation
            if (customerDTO.getHo() == null || customerDTO.getHo().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Họ không được để trống!");
                return "redirect:/admin/customers/edit/" + id;
            }
            if (customerDTO.getTen() == null || customerDTO.getTen().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Tên không được để trống!");
                return "redirect:/admin/customers/edit/" + id;
            }
            if (customerDTO.getEmail() == null || customerDTO.getEmail().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Email không được để trống!");
                return "redirect:/admin/customers/edit/" + id;
            }
            if (customerDTO.getSoDienThoai() == null || customerDTO.getSoDienThoai().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Số điện thoại không được để trống!");
                return "redirect:/admin/customers/edit/" + id;
            }
            
            CustomerDTO updatedCustomer = adminCustomerService.updateCustomer(id, customerDTO);
            if (updatedCustomer != null) {
                redirectAttributes.addFlashAttribute("success", "Cập nhật khách hàng thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng!");
            }
            return "redirect:/admin/customers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi cập nhật khách hàng: " + e.getMessage());
            return "redirect:/admin/customers";
        }
    }
    
    // Xử lý xóa khách hàng
    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            boolean deleted = adminCustomerService.deleteCustomer(id);
            if (deleted) {
                redirectAttributes.addFlashAttribute("success", "Xóa khách hàng thành công!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy khách hàng để xóa!");
            }
            return "redirect:/admin/customers";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi khi xóa khách hàng: " + e.getMessage());
            return "redirect:/admin/customers";
        }
    }
    
    // API endpoints cho AJAX (nếu cần trong tương lai)
    @GetMapping("/{id}")
    @ResponseBody
    public CustomerDTO getCustomer(@PathVariable Integer id) {
        return adminCustomerService.getCustomerById(id);
    }
    
    @PostMapping
    @ResponseBody
    public CustomerDTO createCustomerApi(@RequestBody CustomerDTO customerDTO) {
        return adminCustomerService.createCustomer(customerDTO);
    }
    
    @PutMapping("/{id}")
    @ResponseBody
    public CustomerDTO updateCustomerApi(@PathVariable Integer id, @RequestBody CustomerDTO customerDTO) {
        return adminCustomerService.updateCustomer(id, customerDTO);
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public boolean deleteCustomerApi(@PathVariable Integer id) {
        return adminCustomerService.deleteCustomer(id);
    }
} 