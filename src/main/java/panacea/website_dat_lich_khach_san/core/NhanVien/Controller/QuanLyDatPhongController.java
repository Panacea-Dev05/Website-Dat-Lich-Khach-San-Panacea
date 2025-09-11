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
import panacea.website_dat_lich_khach_san.repository.BookingHistoryRepository;
import panacea.website_dat_lich_khach_san.entity.BookingHistory;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/nhanvien/quanlydatphong")
public class QuanLyDatPhongController {
    private final QuanLyDatPhongService quanLyDatPhongService;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final ObjectMapper objectMapper;
    private final BookingHistoryRepository bookingHistoryRepository;
    
    public QuanLyDatPhongController(QuanLyDatPhongService quanLyDatPhongService, 
                                   HotelRepository hotelRepository, 
                                   RoomRepository roomRepository,
                                   ObjectMapper objectMapper,
                                   BookingHistoryRepository bookingHistoryRepository) {
        this.quanLyDatPhongService = quanLyDatPhongService;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.objectMapper = objectMapper;
        this.bookingHistoryRepository = bookingHistoryRepository;
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
        Integer bookingId = Integer.valueOf(payload.get("bookingId").toString());
        
        // Xử lý cả roomId đơn lẻ và roomIds array để tương thích ngược
        try {
            if (payload.containsKey("roomIds")) {
                @SuppressWarnings("unchecked")
                java.util.List<String> roomIdStrings = (java.util.List<String>) payload.get("roomIds");
                java.util.List<Long> roomIds = roomIdStrings.stream()
                    .map(Long::valueOf)
                    .collect(java.util.stream.Collectors.toList());
                boolean result = quanLyDatPhongService.confirmBookingAndAssignMultipleRooms(bookingId, roomIds);
                if (result) {
                    return Map.of("success", true, "message", "Đã xác nhận và gán " + roomIds.size() + " phòng thành công. Email đã được gửi cho khách hàng.");
                } else {
                    return Map.of("success", false, "message", "Có lỗi xảy ra khi xác nhận đặt phòng!");
                }
            } else {
                // Tương thích ngược với roomId đơn lẻ
                Long roomId = Long.valueOf(payload.get("roomId").toString());
                boolean result = quanLyDatPhongService.confirmBookingAndAssignRoom(bookingId, roomId);
                if (result) {
                    return Map.of("success", true, "message", "Đã xác nhận và gán phòng thành công. Email đã được gửi cho khách hàng.");
                } else {
                    return Map.of("success", false, "message", "Có lỗi xảy ra khi xác nhận đặt phòng!");
                }
            }
        } catch (RuntimeException e) {
            return Map.of("success", false, "message", e.getMessage());
        }
    }
    
    @PostMapping("/cancel/{bookingId}")
    @ResponseBody
    public String cancelBooking(@PathVariable Integer bookingId) {
        boolean success = quanLyDatPhongService.cancelBooking(bookingId);
        return success ? "success" : "error";
    }
    
    @GetMapping("/detail/{bookingId}")
    @ResponseBody
    public Object getBookingDetail(@PathVariable Integer bookingId) {
        BookingDetailViewDTO dto = quanLyDatPhongService.getBookingDetailViewDTOById(bookingId);
        if (dto == null) return new java.util.HashMap<>();
        return dto;
    }
    
    @GetMapping("/hotels")
    @ResponseBody
    public List<Hotel> getHotels() {
        return hotelRepository.findAll();
    }
    
    @GetMapping("/rooms/available")
    @ResponseBody
    public List<Map<String, Object>> getAvailableRooms(@RequestParam(value = "bookingId", required = false) Integer bookingId) {
        // Lấy booking để biết loại phòng khách đã chọn (nếu có)
        Integer roomTypeId = null;
        if (bookingId != null) {
            Booking booking = quanLyDatPhongService.getBookingById(bookingId).orElse(null);
            roomTypeId = (booking != null && booking.getRoomType() != null) ? booking.getRoomType().getId() : null;
        }
        List<Room> rooms = quanLyDatPhongService.getAvailableRooms(roomTypeId);
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
    public Map<String, Object> checkoutBooking(@PathVariable Integer bookingId) {
        boolean result = quanLyDatPhongService.checkoutBooking(bookingId);
        return java.util.Map.of("success", result);
    }

    @PostMapping("/checkin/{bookingId}")
    @ResponseBody
    public Map<String, Object> checkInBooking(@PathVariable Integer bookingId, @RequestBody Map<String, Object> payload) {
        String soCmndCccd = (String) payload.getOrDefault("soCmndCccd", null);
        String ngayCapCmndStr = (String) payload.getOrDefault("ngayCapCmnd", null);
        String noiCapCmnd = (String) payload.getOrDefault("noiCapCmnd", null);
        Byte soNguoiLonThucTe = payload.get("soNguoiLonThucTe") != null ? Byte.valueOf(payload.get("soNguoiLonThucTe").toString()) : null;
        Byte soTreEmThucTe = payload.get("soTreEmThucTe") != null ? Byte.valueOf(payload.get("soTreEmThucTe").toString()) : null;
        String ghiChuCheckIn = (String) payload.getOrDefault("ghiChuCheckIn", null);
        java.time.LocalDate ngayCapCmnd = null;
        if (ngayCapCmndStr != null && !ngayCapCmndStr.isEmpty()) {
            ngayCapCmnd = java.time.LocalDate.parse(ngayCapCmndStr);
        }
        boolean result = quanLyDatPhongService.checkInBooking(bookingId, soCmndCccd, ngayCapCmnd, noiCapCmnd, soNguoiLonThucTe, soTreEmThucTe, ghiChuCheckIn);
        return java.util.Map.of("success", result);
    }

    @PostMapping("/update-services/{bookingId}")
    @ResponseBody
    public ResponseEntity<?> updateBookingServices(@PathVariable Integer bookingId, @RequestBody java.util.List<java.util.Map<String, Object>> services) {
        boolean success = quanLyDatPhongService.updateBookingServices(bookingId, services);
        if (success) {
            return ResponseEntity.ok(java.util.Map.of("success", true));
        } else {
            return ResponseEntity.status(500).body(java.util.Map.of("success", false, "message", "Có lỗi khi cập nhật dịch vụ!"));
        }
    }

    @GetMapping("/history")
    public String viewHistory(Model model) {
        java.util.List<BookingHistory> historyList = bookingHistoryRepository.findAll();
        model.addAttribute("historyList", historyList);
        model.addAttribute("staffName", quanLyDatPhongService.getStaffName());
        return "NhanVien/BookingHistory";
    }
}