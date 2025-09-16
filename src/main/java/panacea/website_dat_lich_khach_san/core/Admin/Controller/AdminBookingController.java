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

/**
 * CHỨC NĂNG: Controller quản lý đặt phòng cho Admin
 * 
 * Mục đích:
 * - Quản lý toàn bộ booking trong hệ thống
 * - Cung cấp CRUD operations cho booking
 * - Hiển thị giao diện quản lý đặt phòng cho admin
 * 
 * Các chức năng chính:
 * 1. Xem danh sách tất cả booking
 * 2. Tạo booking mới
 * 3. Cập nhật thông tin booking
 * 4. Xóa booking
 * 5. Xem chi tiết booking
 */
@Controller
@RequestMapping("/admin/bookings")
public class AdminBookingController {
    
    @Autowired
    private AdminBookingService adminBookingService;
    
    @Autowired
    private AdminCustomerService adminCustomerService;
    
    @Autowired
    private AdminRoomService adminRoomService;
    
    /**
     * CHỨC NĂNG: Hiển thị trang quản lý đặt phòng
     * 
     * Trả về:
     * - Danh sách tất cả booking
     * - Danh sách khách hàng (để tạo booking mới)
     * - Danh sách phòng (để tạo booking mới)
     */
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
    
    /**
     * CHỨC NĂNG: Lấy thông tin chi tiết một booking (API)
     * 
     * @param id ID của booking cần lấy
     * @return BookingDTO chứa thông tin booking
     */
    @GetMapping("/{id}")
    @ResponseBody
    public BookingDTO getBooking(@PathVariable Integer id) {
        return adminBookingService.getBookingById(id);
    }
    
    /**
     * CHỨC NĂNG: Tạo booking mới (API)
     * 
     * @param bookingDTO Thông tin booking cần tạo
     * @return BookingDTO của booking vừa tạo
     */
    @PostMapping
    @ResponseBody
    public BookingDTO createBooking(@RequestBody BookingDTO bookingDTO) {
        return adminBookingService.createBooking(bookingDTO);
    }
    
    /**
     * CHỨC NĂNG: Cập nhật thông tin booking (API)
     * 
     * @param id ID của booking cần cập nhật
     * @param bookingDTO Thông tin booking mới
     * @return BookingDTO của booking sau khi cập nhật
     */
    @PutMapping("/{id}")
    @ResponseBody
    public BookingDTO updateBooking(@PathVariable Integer id, @RequestBody BookingDTO bookingDTO) {
        return adminBookingService.updateBooking(id, bookingDTO);
    }
    
    /**
     * CHỨC NĂNG: Xóa booking (API)
     * 
     * @param id ID của booking cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    @DeleteMapping("/{id}")
    @ResponseBody
    public boolean deleteBooking(@PathVariable Integer id) {
        return adminBookingService.deleteBooking(id);
    }
}