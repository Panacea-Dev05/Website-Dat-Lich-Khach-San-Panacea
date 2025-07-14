package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.QuanLyDatPhongService;
import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.repository.HotelRepository;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.BookingDetailViewDTO;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/nhanvien/quanlydatphong")
public class QuanLyDatPhongController {
    private final QuanLyDatPhongService quanLyDatPhongService;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final ObjectMapper objectMapper;
    
    public QuanLyDatPhongController(QuanLyDatPhongService quanLyDatPhongService, 
                                   HotelRepository hotelRepository, 
                                   RoomRepository roomRepository,
                                   ObjectMapper objectMapper) {
        this.quanLyDatPhongService = quanLyDatPhongService;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.objectMapper = objectMapper;
    }
    
    @GetMapping("")
    public String view(@RequestParam(value = "page", defaultValue = "0") int page,
                      @RequestParam(value = "size", defaultValue = "10") int size,
                      @RequestParam(value = "keyword", required = false) String keyword,
                      @RequestParam(value = "ngayNhan", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate ngayNhan,
                      Model model) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<BookingDetailViewDTO> bookingPage = quanLyDatPhongService.getBookingDetailViewDTOs(keyword, ngayNhan, pageable);
        model.addAttribute("staffName", quanLyDatPhongService.getStaffName());
        model.addAttribute("bookings", bookingPage.getContent());
        model.addAttribute("currentPage", bookingPage.getNumber());
        model.addAttribute("totalPages", bookingPage.getTotalPages());
        model.addAttribute("pageSize", bookingPage.getSize());
        model.addAttribute("keyword", keyword);
        model.addAttribute("ngayNhan", ngayNhan);
        return "NhanVien/QuanLyDatPhong";
    }
    
    @PostMapping("/confirm")
    @ResponseBody
    public Map<String, Object> confirmBooking(@RequestBody Map<String, Object> payload) {
        Long bookingId = Long.valueOf(payload.get("bookingId").toString());
        Long roomId = Long.valueOf(payload.get("roomId").toString());
        boolean result = quanLyDatPhongService.confirmBookingAndAssignRoom(bookingId, roomId);
        if (result) {
            return Map.of("success", true, "message", "Đã xác nhận và gán phòng thành công. Email đã được gửi cho khách hàng.");
        } else {
            return Map.of("success", false, "message", "Có lỗi xảy ra khi xác nhận đặt phòng!");
        }
    }
    
    @PostMapping("/cancel/{bookingId}")
    @ResponseBody
    public String cancelBooking(@PathVariable Long bookingId) {
        boolean success = quanLyDatPhongService.cancelBooking(bookingId);
        return success ? "success" : "error";
    }
    
    @GetMapping("/detail/{bookingId}")
    @ResponseBody
    public Object getBookingDetail(@PathVariable Long bookingId) {
        return quanLyDatPhongService.getBookingById(bookingId)
            .map(booking -> {
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", booking.getId());
                map.put("maDatPhong", booking.getMaDatPhong());
                map.put("khachHang", booking.getKhachHang().getHo() + " " + booking.getKhachHang().getTen());
                map.put("hotel", booking.getHotel().getTenKhachSan());
                map.put("hotelId", booking.getHotel().getId());
                map.put("ngayNhan", booking.getNgayNhanPhong());
                map.put("ngayTra", booking.getNgayTraPhong());
                map.put("soNguoiLon", booking.getSoNguoiLon());
                map.put("soTreEm", booking.getSoTreEm());
                map.put("tongThanhToan", booking.getTongThanhToan());
                map.put("trangThai", booking.getTrangThaiDatPhong().getLabel());
                return map;
            })
            .orElse(new java.util.HashMap<>());
    }
    
    @GetMapping("/hotels")
    @ResponseBody
    public List<Hotel> getHotels() {
        return hotelRepository.findAll();
    }
    
    @GetMapping("/rooms/{hotelId}")
    @ResponseBody
    public List<Map<String, Object>> getAvailableRooms(@PathVariable Long hotelId) {
        List<Room> rooms = quanLyDatPhongService.getAvailableRoomsByHotel(hotelId);
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Room room : rooms) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", room.getId());
            map.put("soPhong", room.getSoPhong());
            if (room.getRoomType() != null) {
                map.put("roomType", room.getRoomType().getTenLoaiPhong());
            }
            result.add(map);
        }
        return result;
    }
    
    @PostMapping("/add-customer")
    @ResponseBody
    public String addCustomerBooking(@RequestBody Map<String, Object> requestData) {
        try {
            boolean success = quanLyDatPhongService.addCustomerBooking(requestData);
            return success ? "success" : "error";
        } catch (Exception e) {
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }

    @PostMapping("/checkout/{bookingId}")
    @ResponseBody
    public Map<String, Object> checkoutBooking(@PathVariable Long bookingId) {
        boolean result = quanLyDatPhongService.checkoutBooking(bookingId);
        return java.util.Map.of("success", result);
    }
} 