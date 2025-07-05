package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import panacea.website_dat_lich_khach_san.core.NhanVien.Service.ThongTinKhachHangService;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.CustomerDTO;

@Controller
@RequestMapping("/nhanvien/thongtinkhachhang")
public class ThongTinKhachHangController {
    private final ThongTinKhachHangService thongTinKhachHangService;
    public ThongTinKhachHangController(ThongTinKhachHangService thongTinKhachHangService) {
        this.thongTinKhachHangService = thongTinKhachHangService;
    }
    @GetMapping("")
    public String view(Model model) {
        java.util.List<panacea.website_dat_lich_khach_san.entity.Customer> dsKhachHang;
        try {
            dsKhachHang = thongTinKhachHangService.getAllCustomers();
            if (dsKhachHang == null) dsKhachHang = new java.util.ArrayList<>();
        } catch (Exception e) {
            dsKhachHang = new java.util.ArrayList<>();
        }
        model.addAttribute("dsKhachHang", dsKhachHang);
        model.addAttribute("staffName", thongTinKhachHangService.getStaffName());
        model.addAttribute("preferences", thongTinKhachHangService.getAllPreferences());
        model.addAttribute("gioiTinhs", java.util.List.of("Nam", "Nữ", "Khác"));
        model.addAttribute("loaiPhongList", java.util.List.of("Standard", "Deluxe", "Suite"));
        model.addAttribute("tangList", java.util.List.of("Thấp", "Trung bình", "Cao"));
        model.addAttribute("hutThuocList", java.util.List.of("Không hút thuốc", "Hút thuốc"));
        return "NhanVien/ThongTinKhachHang";
    }

    @PostMapping("/add")
    public String addCustomer(@ModelAttribute CustomerDTO customerDTO, Model model, RedirectAttributes redirectAttributes) {
        try {
            thongTinKhachHangService.addCustomer(customerDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm khách hàng thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi thêm khách hàng: " + e.getMessage());
        }
        return "redirect:/nhanvien/thongtinkhachhang";
    }

    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable Integer id) {
        thongTinKhachHangService.deleteCustomer(id);
        return "redirect:/nhanvien/thongtinkhachhang";
    }

    @GetMapping("/edit/{id}")
    public String editCustomerForm(@PathVariable Integer id, Model model) {
        var customer = thongTinKhachHangService.getCustomerById(id);
        model.addAttribute("editCustomer", customer);
        // Truyền lại các biến cần thiết cho view
        model.addAttribute("dsKhachHang", thongTinKhachHangService.getAllCustomers());
        model.addAttribute("staffName", thongTinKhachHangService.getStaffName());
        model.addAttribute("preferences", thongTinKhachHangService.getAllPreferences());
        model.addAttribute("gioiTinhs", java.util.List.of("Nam", "Nữ", "Khác"));
        model.addAttribute("loaiPhongList", java.util.List.of("Standard", "Deluxe", "Suite"));
        model.addAttribute("tangList", java.util.List.of("Thấp", "Trung bình", "Cao"));
        model.addAttribute("hutThuocList", java.util.List.of("Không hút thuốc", "Hút thuốc"));
        return "NhanVien/ThongTinKhachHang";
    }

    @PostMapping("/edit")
    public String editCustomer(@ModelAttribute("editCustomer") CustomerDTO customerDTO, RedirectAttributes redirectAttributes) {
        try {
            if (customerDTO.getId() != null) {
                // Cập nhật khách hàng hiện có
                thongTinKhachHangService.updateCustomer(customerDTO);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật khách hàng thành công!");
            } else {
                // Thêm khách hàng mới
                thongTinKhachHangService.addCustomer(customerDTO);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm khách hàng thành công!");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/nhanvien/thongtinkhachhang";
    }
} 