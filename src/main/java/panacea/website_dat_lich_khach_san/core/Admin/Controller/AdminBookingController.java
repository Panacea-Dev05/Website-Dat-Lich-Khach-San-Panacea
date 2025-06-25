package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminBookingService;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.BookingDTO;

import java.util.List;

@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {
    
    @Autowired
    private AdminBookingService adminBookingService;
    
    @GetMapping
    public String bookingManagement(Model model) {
        List<BookingDTO> bookings = adminBookingService.getAllBookings();
        model.addAttribute("bookings", bookings);
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