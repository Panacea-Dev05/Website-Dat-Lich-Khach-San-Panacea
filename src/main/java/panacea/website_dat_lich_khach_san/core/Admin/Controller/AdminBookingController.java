package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminBookingService;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminCustomerService;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminRoomService;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.BookingDTO;

@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {
    
    @Autowired
    private AdminBookingService adminBookingService;
    
    @Autowired
    private AdminCustomerService adminCustomerService;
    
    @Autowired
    private AdminRoomService adminRoomService;
    
    @GetMapping
    public String bookingManagement(Model model) {
        List<BookingDTO> bookings = adminBookingService.getAllBookings();
        model.addAttribute("bookings", bookings);
        // Truyền thêm cột tên nhân viên tạo booking ra view
        // (View sẽ lấy từ bookings.tenNhanVienTao)
        model.addAttribute("customers", adminCustomerService.getAllCustomers());
        model.addAttribute("rooms", adminRoomService.getAllRooms());
        return "Admin/view/QuanLyDatPhong";
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public BookingDTO getBooking(@PathVariable Long id) {
        return adminBookingService.getBookingById(id);
    }
    
    @PostMapping
    @ResponseBody
    public BookingDTO createBooking(@RequestBody BookingDTO bookingDTO) {
        return adminBookingService.createBooking(bookingDTO);
    }
    
    @PutMapping("/{id}")
    @ResponseBody
    public BookingDTO updateBooking(@PathVariable Long id, @RequestBody BookingDTO bookingDTO) {
        return adminBookingService.updateBooking(id, bookingDTO);
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public boolean deleteBooking(@PathVariable Long id) {
        return adminBookingService.deleteBooking(id);
    }
} 