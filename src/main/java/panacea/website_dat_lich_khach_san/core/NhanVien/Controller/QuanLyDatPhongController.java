package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.QuanLyDatPhongService;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminPromotionService;
import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.entity.Promotion;
import panacea.website_dat_lich_khach_san.repository.HotelRepository;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;
import panacea.website_dat_lich_khach_san.repository.PromotionRepository;
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
import org.springframework.beans.factory.annotation.Autowired;

@Controller
@RequestMapping("/nhanvien/quanlydatphong")
public class QuanLyDatPhongController {
    private final QuanLyDatPhongService quanLyDatPhongService;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final ObjectMapper objectMapper;
    private final BookingHistoryRepository bookingHistoryRepository;
    
    @Autowired
    private AdminPromotionService adminPromotionService;
    
    @Autowired
    private PromotionRepository promotionRepository;
    
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
    public List<Map<String, Object>> getAvailableRooms(@RequestParam(value = "bookingId") Long bookingId) {
        // Lấy booking để biết loại phòng khách đã chọn
        Booking booking = quanLyDatPhongService.getBookingById(bookingId).orElse(null);
        Integer roomTypeId = (booking != null && booking.getRoomType() != null) ? booking.getRoomType().getId() : null;
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
    public Map<String, Object> checkoutBooking(@PathVariable Long bookingId) {
        boolean result = quanLyDatPhongService.checkoutBooking(bookingId);
        return java.util.Map.of("success", result);
    }

    @PostMapping("/checkin/{bookingId}")
    @ResponseBody
    public Map<String, Object> checkInBooking(@PathVariable Long bookingId, @RequestBody Map<String, Object> payload) {
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
    public ResponseEntity<?> updateBookingServices(@PathVariable Long bookingId, @RequestBody java.util.List<java.util.Map<String, Object>> services) {
        boolean success = quanLyDatPhongService.updateBookingServices(bookingId, services);
        return ResponseEntity.ok(java.util.Map.of("success", success));
    }

    @GetMapping("/history")
    public String viewHistory(Model model) {
        java.util.List<BookingHistory> historyList = bookingHistoryRepository.findAll();
        model.addAttribute("historyList", historyList);
        model.addAttribute("staffName", quanLyDatPhongService.getStaffName());
        return "NhanVien/BookingHistory";
    }

    @PostMapping("/promotion/validate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> validatePromotion(@RequestBody Map<String, Object> request) {
        String promoCode = (String) request.get("promoCode");
        Long bookingId = Long.valueOf(request.get("bookingId").toString());
        java.math.BigDecimal bookingAmount;
        
        try {
            Object amountObj = request.get("amount");
            if (amountObj == null) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Thiếu thông tin số tiền đặt phòng!"
                ));
            }
            bookingAmount = new java.math.BigDecimal(amountObj.toString());
        } catch (NumberFormatException e) {
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Số tiền đặt phòng không hợp lệ!"
            ));
        }
        
        try {
            // Lấy thông tin booking để tính tiền phải trả
            java.util.Optional<Booking> bookingOpt = quanLyDatPhongService.getBookingById(bookingId);
            if (!bookingOpt.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Không tìm thấy thông tin đặt phòng!"
                ));
            }
            
            Booking booking = bookingOpt.get();
            java.math.BigDecimal tongTien = booking.getTongThanhToan();
            java.math.BigDecimal datCoc = booking.getTienDatCoc() != null ? booking.getTienDatCoc() : java.math.BigDecimal.ZERO;
            java.math.BigDecimal tienPhaiTra = tongTien.subtract(datCoc);
            
            // Kiểm tra nếu đã thanh toán hết
            if (tienPhaiTra.compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Đặt phòng này đã được thanh toán đầy đủ!"
                ));
            }
            
            // Tìm promotion theo mã
            java.util.List<Promotion> promotions = promotionRepository.findAll().stream()
                .filter(p -> p.getMaKhuyenMai().equalsIgnoreCase(promoCode))
                .collect(java.util.stream.Collectors.toList());
            
            if (promotions.isEmpty()) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Mã giảm giá không tồn tại!"
                ));
            }
            
            Promotion promotion = promotions.get(0);
            
            // Kiểm tra trạng thái
            if (promotion.getTrangThai() != Promotion.TrangThaiPromotion.HOAT_DONG) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Mã giảm giá không còn hoạt động!"
                ));
            }
            
            // Kiểm tra thời gian
            java.time.LocalDate now = java.time.LocalDate.now();
            if (promotion.getNgayBatDau().isAfter(now)) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Mã giảm giá chưa có hiệu lực!"
                ));
            }
            
            if (promotion.getNgayKetThuc().isBefore(now)) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Mã giảm giá đã hết hạn!"
                ));
            }
            
            // Kiểm tra số lượng sử dụng
            if (promotion.getSoLuongToiDa() != null && 
                promotion.getDaSuDung() != null && 
                promotion.getDaSuDung() >= promotion.getSoLuongToiDa()) {
                return ResponseEntity.ok(Map.of(
                    "success", false,
                    "message", "Mã giảm giá đã hết lượt sử dụng!"
                ));
            }
            
            // Kiểm tra điều kiện áp dụng dựa trên tiền phải trả
            if (promotion.getDieuKienApDung() != null && !promotion.getDieuKienApDung().trim().isEmpty()) {
                try {
                    java.math.BigDecimal dieuKienApDung = new java.math.BigDecimal(promotion.getDieuKienApDung());
                    if (tienPhaiTra.compareTo(dieuKienApDung) < 0) {
                        return ResponseEntity.ok(Map.of(
                            "success", false,
                            "message", "Số tiền phải trả chưa đủ điều kiện áp dụng mã giảm giá! (Tối thiểu: " + 
                                      new java.text.DecimalFormat("#,###").format(dieuKienApDung) + " VNĐ)"
                        ));
                    }
                } catch (NumberFormatException e) {
                    // Nếu điều kiện áp dụng không phải là số, bỏ qua kiểm tra này
                }
            }
            
            // Tính toán giảm giá dựa trên tiền phải trả
            java.math.BigDecimal discountAmount;
            if (promotion.getLoaiGiamGia() == Promotion.LoaiGiamGia.PHAN_TRAM) {
                discountAmount = tienPhaiTra.multiply(promotion.getGiaTriGiam()).divide(java.math.BigDecimal.valueOf(100));
                if (promotion.getGiamToiDa() != null && discountAmount.compareTo(promotion.getGiamToiDa()) > 0) {
                    discountAmount = promotion.getGiamToiDa();
                }
            } else {
                discountAmount = promotion.getGiaTriGiam();
                if (discountAmount.compareTo(tienPhaiTra) > 0) {
                    discountAmount = tienPhaiTra;
                }
            }
            
            // Tính tổng tiền sau giảm giá
            java.math.BigDecimal finalAmount = tongTien.subtract(discountAmount);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Áp dụng mã giảm giá thành công!",
                "promotion", Map.of(
                    "id", promotion.getId(),
                    "maKhuyenMai", promotion.getMaKhuyenMai(),
                    "tenKhuyenMai", promotion.getTenKhuyenMai(),
                    "loaiGiamGia", promotion.getLoaiGiamGia().name(),
                    "giaTriGiam", promotion.getGiaTriGiam(),
                    "giamToiDa", promotion.getGiamToiDa()
                ),
                "discountAmount", discountAmount,
                "finalAmount", finalAmount,
                "tienPhaiTra", tienPhaiTra,
                "datCoc", datCoc
            ));
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of(
                "success", false,
                "message", "Có lỗi xảy ra khi xác thực mã giảm giá!"
            ));
        }
    }
}