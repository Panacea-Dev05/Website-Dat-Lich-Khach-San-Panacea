package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import panacea.website_dat_lich_khach_san.core.NhanVien.Service.QuanLyDatPhongService;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.entity.BookingHistory;
import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.entity.RoomType;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.BookingDetailViewDTO;
import panacea.website_dat_lich_khach_san.infrastructure.Exception.BadRequestException;
import panacea.website_dat_lich_khach_san.infrastructure.Exception.ResourceNotFoundException;
import panacea.website_dat_lich_khach_san.infrastructure.Exception.ValidationException;
import panacea.website_dat_lich_khach_san.repository.BookingHistoryRepository;
import panacea.website_dat_lich_khach_san.repository.HotelRepository;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;
import panacea.website_dat_lich_khach_san.repository.RoomTypeRepository;

// Controller quản lý đặt phòng cho nhân viên
@Controller
@RequestMapping("/nhanvien/quanlydatphong")
public class QuanLyDatPhongController {
    private static final Logger logger = LoggerFactory.getLogger(QuanLyDatPhongController.class);
    
    private final QuanLyDatPhongService quanLyDatPhongService;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final ObjectMapper objectMapper;
    private final BookingHistoryRepository bookingHistoryRepository;
    
    public QuanLyDatPhongController(QuanLyDatPhongService quanLyDatPhongService, 
                                   HotelRepository hotelRepository, 
                                   RoomRepository roomRepository,
                                   RoomTypeRepository roomTypeRepository,
                                   ObjectMapper objectMapper,
                                   BookingHistoryRepository bookingHistoryRepository) {
        this.quanLyDatPhongService = quanLyDatPhongService;
        this.hotelRepository = hotelRepository;
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.objectMapper = objectMapper;
        this.bookingHistoryRepository = bookingHistoryRepository;
    }
    
    // Hiển thị trang quản lý đặt phòng
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
    
    // Xác nhận đặt phòng và gán phòng
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
    
    // Hủy đặt phòng
    @PostMapping("/cancel/{bookingId}")
    @ResponseBody
    public String cancelBooking(@PathVariable Integer bookingId) {
        boolean success = quanLyDatPhongService.cancelBooking(bookingId);
        return success ? "success" : "error";
    }
    
    // Lấy chi tiết đặt phòng
    @GetMapping("/detail/{bookingId}")
    @ResponseBody
    public Object getBookingDetail(@PathVariable Integer bookingId) {
        BookingDetailViewDTO dto = quanLyDatPhongService.getBookingDetailViewDTOById(bookingId);
        if (dto == null) return new java.util.HashMap<>();
        return dto;
    }
    
    // Lấy danh sách khách sạn
    @GetMapping("/hotels")
    @ResponseBody
    public List<Hotel> getHotels() {
        return hotelRepository.findAll();
    }
    
    @GetMapping("/room-types/available")
    @ResponseBody
    public List<Map<String, Object>> getAvailableRoomTypes() {
        List<RoomType> roomTypes = roomTypeRepository.findAll();
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        
        for (RoomType roomType : roomTypes) {
            // Đếm số phòng trống theo loại
            long availableCount = roomRepository.countByRoomTypeIdAndTrangThai(roomType.getId(), panacea.website_dat_lich_khach_san.entity.Room.TrangThaiPhong.SAN_SANG);
            
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", roomType.getId());
            map.put("tenLoaiPhong", roomType.getTenLoaiPhong());
            map.put("availableRooms", availableCount);
            
            // Lấy thông tin giá từ RoomPricing
            java.math.BigDecimal giaGio = java.math.BigDecimal.ZERO;
            java.math.BigDecimal giaNgay = java.math.BigDecimal.ZERO;
            java.math.BigDecimal giaQuaDem = java.math.BigDecimal.ZERO;
            
            // Tìm pricing cho room type này
            java.util.Optional<panacea.website_dat_lich_khach_san.entity.RoomPricing> pricing = 
                quanLyDatPhongService.getRoomPricingByRoomTypeId(roomType.getId());
            
            if (pricing.isPresent()) {
                giaGio = pricing.get().getGiaGio() != null ? pricing.get().getGiaGio() : java.math.BigDecimal.ZERO;
                giaNgay = pricing.get().getGiaNgay() != null ? pricing.get().getGiaNgay() : java.math.BigDecimal.ZERO;
                giaQuaDem = pricing.get().getGiaQuaDem() != null ? pricing.get().getGiaQuaDem() : java.math.BigDecimal.ZERO;
            }
            
            map.put("giaTheoGio", giaGio);
            map.put("giaTheoNgay", giaNgay);
            map.put("giaQuaDem", giaQuaDem);
            result.add(map);
        }
        return result;
    }
    
    @GetMapping("/rooms/available")
    @ResponseBody
    public List<Map<String, Object>> getAvailableRooms(@RequestParam(value = "bookingId", required = false) Integer bookingId,
                                                        @RequestParam(value = "roomTypeId", required = false) Integer roomTypeId) {
        // Lấy booking để biết loại phòng khách đã chọn (nếu có)
        if (bookingId != null && roomTypeId == null) {
            Booking booking = quanLyDatPhongService.getBookingById(bookingId).orElse(null);
            roomTypeId = (booking != null && booking.getRoomType() != null) ? booking.getRoomType().getId() : null;
        }
        
        List<Room> rooms;
        if (roomTypeId != null) {
            // Lấy phòng theo loại phòng cụ thể
            rooms = roomRepository.findByRoomTypeIdAndTrangThai(roomTypeId, panacea.website_dat_lich_khach_san.entity.Room.TrangThaiPhong.SAN_SANG);
        } else {
            // Lấy tất cả phòng trống
            rooms = quanLyDatPhongService.getAvailableRooms(null);
        }
        
        List<Map<String, Object>> result = new java.util.ArrayList<>();
        for (Room room : rooms) {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", room.getId());
            map.put("soPhong", room.getSoPhong());
            map.put("giaCoBan", room.getGiaCoBan() != null ? room.getGiaCoBan() : java.math.BigDecimal.ZERO);
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
        } catch (ValidationException | BadRequestException e) {
            logger.warn("Validation error in addCustomerBooking: {}", e.getMessage());
            return "error: " + e.getMessage();
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found in addCustomerBooking: {}", e.getMessage());
            return "error: " + e.getMessage();
        } catch (Exception e) {
            logger.error("Unexpected error in addCustomerBooking", e);
            return "error: Có lỗi hệ thống xảy ra";
        }
    }

    @PostMapping("/checkout/{bookingId}")
    @ResponseBody
    public Map<String, Object> checkoutBooking(@PathVariable Integer bookingId) {
        try {
            boolean result = quanLyDatPhongService.checkoutBooking(bookingId);
            return java.util.Map.of("success", result);
        } catch (IllegalStateException e) {
            return java.util.Map.of("success", false, "error", true, "message", e.getMessage());
        } catch (ValidationException | BadRequestException e) {
            logger.warn("Validation error in checkoutBooking: {}", e.getMessage());
            return java.util.Map.of("success", false, "error", true, "message", e.getMessage());
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found in checkoutBooking: {}", e.getMessage());
            return java.util.Map.of("success", false, "error", true, "message", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error in checkoutBooking", e);
            return java.util.Map.of("success", false, "error", true, "message", "Có lỗi hệ thống xảy ra khi checkout");
        }
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

    // API đổi phòng cho khách hàng
    @PostMapping("/api/change-room/{bookingId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> changeRoom(@PathVariable Integer bookingId, 
                                                         @RequestBody Map<String, Object> request) {
        try {
            Integer oldRoomId = (Integer) request.get("oldRoomId");
            Integer newRoomId = (Integer) request.get("newRoomId");
            String lyDo = (String) request.get("lyDo");
            
            if (oldRoomId == null || newRoomId == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Thiếu thông tin phòng cũ hoặc phòng mới");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            if (oldRoomId.equals(newRoomId)) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Phòng mới phải khác phòng cũ");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Gọi service để đổi phòng
            boolean success = quanLyDatPhongService.changeRoomForBooking(bookingId, oldRoomId, newRoomId, lyDo);
            
            Map<String, Object> response = new HashMap<>();
            if (success) {
                response.put("success", true);
                response.put("message", "Đổi phòng thành công");
            } else {
                response.put("success", false);
                response.put("message", "Không thể đổi phòng. Vui lòng kiểm tra lại thông tin");
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
    
    // API lấy danh sách phòng trống để đổi
    @GetMapping("/api/available-rooms-for-change/{currentRoomId}")
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getAvailableRoomsForChange(@PathVariable Integer currentRoomId) {
        try {
            List<Room> availableRooms = quanLyDatPhongService.getAvailableRoomsForChange(currentRoomId);
            
            List<Map<String, Object>> roomList = availableRooms.stream()
                .map(room -> {
                    Map<String, Object> roomInfo = new HashMap<>();
                    roomInfo.put("id", room.getId());
                    roomInfo.put("soPhong", room.getSoPhong());
                    roomInfo.put("tang", room.getTang());
                    roomInfo.put("viewPhong", room.getViewPhong());
                    roomInfo.put("roomType", room.getRoomType() != null ? room.getRoomType().getTenLoaiPhong() : "N/A");
                    return roomInfo;
                })
                .collect(java.util.stream.Collectors.toList());
            
            return ResponseEntity.ok(roomList);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ArrayList<>());
        }
    }


    @PostMapping("/update-services/{bookingId}")
    @ResponseBody
    public ResponseEntity<?> updateBookingServices(@PathVariable Integer bookingId, @RequestBody java.util.List<java.util.Map<String, Object>> services) {
        try {
            boolean success = quanLyDatPhongService.updateBookingServices(bookingId, services);
            if (success) {
                return ResponseEntity.ok(java.util.Map.of("success", true));
            } else {
                return ResponseEntity.status(400).body(java.util.Map.of("success", false, "message", "Không thể cập nhật dịch vụ. Booking có thể đã thanh toán đủ hoặc bị hủy."));
            }
        } catch (Exception e) {
            logger.error("Error updating booking services for booking {}: {}", bookingId, e.getMessage());
            return ResponseEntity.status(500).body(java.util.Map.of("success", false, "message", "Có lỗi hệ thống khi cập nhật dịch vụ!"));
        }
    }

    @GetMapping("/history")
    public String viewHistory(Model model) {
        java.util.List<BookingHistory> historyList = bookingHistoryRepository.findAll();
        model.addAttribute("historyList", historyList);
        model.addAttribute("staffName", quanLyDatPhongService.getStaffName());
        return "NhanVien/BookingHistory";
    }

    @GetMapping("/inventory/available")
    @ResponseBody
    public ResponseEntity<?> getAvailableInventoryItems() {
        try {
            java.util.List<java.util.Map<String, Object>> inventoryItems = quanLyDatPhongService.getAvailableInventoryItems();
            return ResponseEntity.ok(inventoryItems);
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found in getAvailableInventoryItems: {}", e.getMessage());
            return ResponseEntity.status(404).body(java.util.Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error in getAvailableInventoryItems", e);
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Có lỗi hệ thống khi lấy danh sách vật phẩm tồn kho"));
        }
    }

    @PostMapping("/update-services-and-inventory/{bookingId}")
    @ResponseBody
    public ResponseEntity<?> updateBookingServicesAndInventory(@PathVariable Integer bookingId, @RequestBody java.util.Map<String, Object> requestData) {
        try {
            @SuppressWarnings("unchecked")
            java.util.List<java.util.Map<String, Object>> services = (java.util.List<java.util.Map<String, Object>>) requestData.get("services");
            @SuppressWarnings("unchecked")
            java.util.List<java.util.Map<String, Object>> inventoryItems = (java.util.List<java.util.Map<String, Object>>) requestData.get("inventoryItems");
            
            boolean success = quanLyDatPhongService.updateBookingServicesAndInventory(bookingId, services, inventoryItems);
            if (success) {
                return ResponseEntity.ok(java.util.Map.of("success", true));
            } else {
                return ResponseEntity.status(400).body(java.util.Map.of("success", false, "message", "Không thể cập nhật dịch vụ và vật phẩm. Booking có thể đã thanh toán đủ hoặc bị hủy."));
            }
        } catch (ValidationException | BadRequestException e) {
            logger.warn("Validation error in updateBookingServicesAndInventory: {}", e.getMessage());
            return ResponseEntity.status(400).body(java.util.Map.of("success", false, "message", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found in updateBookingServicesAndInventory: {}", e.getMessage());
            return ResponseEntity.status(404).body(java.util.Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error in updateBookingServicesAndInventory", e);
            return ResponseEntity.status(500).body(java.util.Map.of("success", false, "message", "Có lỗi hệ thống khi cập nhật dịch vụ và vật phẩm"));
        }
    }

    @PostMapping("/add-inventory/{bookingId}")
    @ResponseBody
    public ResponseEntity<?> addInventoryToBooking(@PathVariable Integer bookingId, @RequestBody java.util.List<java.util.Map<String, Object>> inventoryItems) {
        try {
            boolean success = quanLyDatPhongService.addInventoryToBooking(bookingId, inventoryItems);
            if (success) {
                return ResponseEntity.ok(java.util.Map.of("success", true));
            } else {
                return ResponseEntity.status(400).body(java.util.Map.of("success", false, "message", "Không thể thêm vật phẩm. Booking có thể đã thanh toán đủ hoặc bị hủy."));
            }
        } catch (ValidationException | BadRequestException e) {
            logger.warn("Validation error in addInventoryToBooking: {}", e.getMessage());
            return ResponseEntity.status(400).body(java.util.Map.of("success", false, "message", e.getMessage()));
        } catch (ResourceNotFoundException e) {
            logger.warn("Resource not found in addInventoryToBooking: {}", e.getMessage());
            return ResponseEntity.status(404).body(java.util.Map.of("success", false, "message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error in addInventoryToBooking", e);
            return ResponseEntity.status(500).body(java.util.Map.of("success", false, "message", "Có lỗi hệ thống khi thêm vật phẩm"));
        }
    }
}