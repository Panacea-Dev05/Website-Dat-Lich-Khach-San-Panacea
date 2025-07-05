package panacea.website_dat_lich_khach_san.core.NhanVien.Controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.core.NhanVien.Service.QuanLyPhongService;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomCreateDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomTypeCreateDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomTypeDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomUpdateDTO;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/nhanvien/quanlyphong")
public class QuanLyPhongController {
    private final QuanLyPhongService quanLyPhongService;
    
    public QuanLyPhongController(QuanLyPhongService quanLyPhongService) {
        this.quanLyPhongService = quanLyPhongService;
    }
    
    // View mapping
    @GetMapping("")
    public String view(Model model) {
        model.addAttribute("staffName", quanLyPhongService.getStaffName());
        model.addAttribute("rooms", quanLyPhongService.getAllRoomsDTO());
        model.addAttribute("roomTypes", quanLyPhongService.getAllRoomTypes());
        
        // Add trang thai list for dropdown
        List<String> trangThaiList = List.of(
            "SAN_SANG", "DANG_SU_DUNG", "BAO_TRI", "DON_DEP"
        );
        model.addAttribute("trangThaiList", trangThaiList);
        
        return "NhanVien/QuanLyPhong";
    }
    
    // REST API endpoints
    
    // Get all rooms
    @GetMapping("/api/rooms")
    @ResponseBody
    public ResponseEntity<List<RoomDTO>> getAllRooms() {
        List<RoomDTO> rooms = quanLyPhongService.getAllRoomsDTO();
        return ResponseEntity.ok(rooms);
    }
    
    // Get rooms with pagination
    @GetMapping("/api/rooms/paged")
    @ResponseBody
    public ResponseEntity<List<RoomDTO>> getRoomsPaged(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<RoomDTO> rooms = quanLyPhongService.getRoomsByHotelPaged(null, page, size);
        return ResponseEntity.ok(rooms);
    }
    
    // Get room by ID
    @GetMapping("/api/rooms/{id}")
    @ResponseBody
    public ResponseEntity<RoomDTO> getRoomById(@PathVariable Integer id) {
        RoomDTO room = quanLyPhongService.getRoomById(id);
        return ResponseEntity.ok(room);
    }
    
    // Create new room
    @PostMapping("/api/rooms")
    @ResponseBody
    public ResponseEntity<RoomDTO> createRoom(@RequestBody RoomCreateDTO roomCreateDTO) {
        RoomDTO createdRoom = quanLyPhongService.createRoom(roomCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }
    
    // Update room
    @PutMapping("/api/rooms/{id}")
    @ResponseBody
    public ResponseEntity<RoomDTO> updateRoom(@PathVariable Integer id, @RequestBody RoomUpdateDTO roomUpdateDTO) {
        RoomDTO updatedRoom = quanLyPhongService.updateRoom(id, roomUpdateDTO);
        return ResponseEntity.ok(updatedRoom);
    }
    
    // Delete room
    @DeleteMapping("/api/rooms/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, String>> deleteRoom(@PathVariable Integer id) {
        quanLyPhongService.deleteRoom(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Phòng đã được xóa thành công");
        return ResponseEntity.ok(response);
    }
    
    // Search rooms by room number
    @GetMapping("/api/rooms/search")
    @ResponseBody
    public ResponseEntity<List<RoomDTO>> searchRoomsBySoPhong(@RequestParam String soPhong) {
        List<RoomDTO> rooms = quanLyPhongService.searchRoomsBySoPhong(soPhong);
        return ResponseEntity.ok(rooms);
    }
    
    // Get rooms by hotel
    @GetMapping("/api/rooms/hotel/{hotelId}")
    @ResponseBody
    public ResponseEntity<List<RoomDTO>> getRoomsByHotel(@PathVariable Integer hotelId) {
        List<RoomDTO> rooms = quanLyPhongService.getRoomsByHotel(hotelId);
        return ResponseEntity.ok(rooms);
    }
    
    // Get rooms by hotel with pagination
    @GetMapping("/api/rooms/hotel/{hotelId}/paged")
    @ResponseBody
    public ResponseEntity<List<RoomDTO>> getRoomsByHotelPaged(
            @PathVariable Integer hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<RoomDTO> rooms = quanLyPhongService.getRoomsByHotelPaged(hotelId, page, size);
        return ResponseEntity.ok(rooms);
    }
    
    // Get rooms by room type
    @GetMapping("/api/rooms/roomtype/{roomTypeId}")
    @ResponseBody
    public ResponseEntity<List<RoomDTO>> getRoomsByRoomType(@PathVariable Integer roomTypeId) {
        List<RoomDTO> rooms = quanLyPhongService.getRoomsByRoomType(roomTypeId);
        return ResponseEntity.ok(rooms);
    }
    
    // Get rooms by status
    @GetMapping("/api/rooms/status/{trangThai}")
    @ResponseBody
    public ResponseEntity<List<RoomDTO>> getRoomsByTrangThai(@PathVariable String trangThai) {
        List<RoomDTO> rooms = quanLyPhongService.getRoomsByTrangThai(trangThai);
        return ResponseEntity.ok(rooms);
    }
    
    // Get rooms by hotel and status
    @GetMapping("/api/rooms/hotel/{hotelId}/status/{trangThai}")
    @ResponseBody
    public ResponseEntity<List<RoomDTO>> getRoomsByHotelAndTrangThai(
            @PathVariable Integer hotelId, 
            @PathVariable String trangThai) {
        List<RoomDTO> rooms = quanLyPhongService.getRoomsByHotelAndTrangThai(hotelId, trangThai);
        return ResponseEntity.ok(rooms);
    }
    
    // Get rooms by hotel and status with pagination
    @GetMapping("/api/rooms/hotel/{hotelId}/status/{trangThai}/paged")
    @ResponseBody
    public ResponseEntity<List<RoomDTO>> getRoomsByHotelAndTrangThaiPaged(
            @PathVariable Integer hotelId,
            @PathVariable String trangThai,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<RoomDTO> rooms = quanLyPhongService.getRoomsByHotelAndTrangThaiPaged(hotelId, trangThai, page, size);
        return ResponseEntity.ok(rooms);
    }
    
    // Get rooms by floor
    @GetMapping("/api/rooms/floor/{tang}")
    @ResponseBody
    public ResponseEntity<List<RoomDTO>> getRoomsByTang(@PathVariable Byte tang) {
        List<RoomDTO> rooms = quanLyPhongService.getRoomsByTang(tang);
        return ResponseEntity.ok(rooms);
    }
    
    // Get rooms by hotel and floor
    @GetMapping("/api/rooms/hotel/{hotelId}/floor/{tang}")
    @ResponseBody
    public ResponseEntity<List<RoomDTO>> getRoomsByHotelAndTang(
            @PathVariable Integer hotelId, 
            @PathVariable Byte tang) {
        List<RoomDTO> rooms = quanLyPhongService.getRoomsByHotelAndTang(hotelId, tang);
        return ResponseEntity.ok(rooms);
    }
    
    // Get rooms by price range
    @GetMapping("/api/rooms/price")
    @ResponseBody
    public ResponseEntity<List<RoomDTO>> getRoomsByPriceRange(
            @RequestParam BigDecimal minPrice, 
            @RequestParam BigDecimal maxPrice) {
        List<RoomDTO> rooms = quanLyPhongService.getRoomsByPriceRange(minPrice, maxPrice);
        return ResponseEntity.ok(rooms);
    }
    
    // Get rooms by hotel and price range
    @GetMapping("/api/rooms/hotel/{hotelId}/price")
    @ResponseBody
    public ResponseEntity<List<RoomDTO>> getRoomsByHotelAndPriceRange(
            @PathVariable Integer hotelId,
            @RequestParam BigDecimal minPrice, 
            @RequestParam BigDecimal maxPrice) {
        List<RoomDTO> rooms = quanLyPhongService.getRoomsByHotelAndPriceRange(hotelId, minPrice, maxPrice);
        return ResponseEntity.ok(rooms);
    }
    
    // Room Type endpoints
    
    // Get all room types
    @GetMapping("/api/roomtypes")
    @ResponseBody
    public ResponseEntity<List<RoomTypeDTO>> getAllRoomTypes() {
        List<RoomTypeDTO> roomTypes = quanLyPhongService.getAllRoomTypes();
        return ResponseEntity.ok(roomTypes);
    }
    
    // Get room type by ID
    @GetMapping("/api/roomtypes/{id}")
    @ResponseBody
    public ResponseEntity<RoomTypeDTO> getRoomTypeById(@PathVariable Integer id) {
        RoomTypeDTO roomType = quanLyPhongService.getRoomTypeById(id);
        return ResponseEntity.ok(roomType);
    }
    
    // Search room types by name
    @GetMapping("/api/roomtypes/search")
    @ResponseBody
    public ResponseEntity<List<RoomTypeDTO>> searchRoomTypesByTenLoaiPhong(@RequestParam String tenLoaiPhong) {
        List<RoomTypeDTO> roomTypes = quanLyPhongService.searchRoomTypesByTenLoaiPhong(tenLoaiPhong);
        return ResponseEntity.ok(roomTypes);
    }
    
    // Get room types by number of beds
    @GetMapping("/api/roomtypes/beds/{soGiuong}")
    @ResponseBody
    public ResponseEntity<List<RoomTypeDTO>> getRoomTypesBySoGiuong(@PathVariable Byte soGiuong) {
        List<RoomTypeDTO> roomTypes = quanLyPhongService.getRoomTypesBySoGiuong(soGiuong);
        return ResponseEntity.ok(roomTypes);
    }
    
    // Get room types by capacity
    @GetMapping("/api/roomtypes/capacity/{sucChua}")
    @ResponseBody
    public ResponseEntity<List<RoomTypeDTO>> getRoomTypesBySucChua(@PathVariable Byte sucChua) {
        List<RoomTypeDTO> roomTypes = quanLyPhongService.getRoomTypesBySucChua(sucChua);
        return ResponseEntity.ok(roomTypes);
    }
    
    // Get room types by area range
    @GetMapping("/api/roomtypes/area")
    @ResponseBody
    public ResponseEntity<List<RoomTypeDTO>> getRoomTypesByDienTich(
            @RequestParam BigDecimal minDienTich, 
            @RequestParam BigDecimal maxDienTich) {
        List<RoomTypeDTO> roomTypes = quanLyPhongService.getRoomTypesByDienTich(minDienTich, maxDienTich);
        return ResponseEntity.ok(roomTypes);
    }
    
    // Statistics endpoints
    
    // Get total rooms
    @GetMapping("/api/statistics/total-rooms")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getTotalRooms() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalRooms", quanLyPhongService.getTotalRooms());
        return ResponseEntity.ok(stats);
    }
    
    // Get total rooms by hotel
    @GetMapping("/api/statistics/hotel/{hotelId}/total-rooms")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getTotalRoomsByHotel(@PathVariable Integer hotelId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalRooms", quanLyPhongService.getTotalRoomsByHotel(hotelId));
        return ResponseEntity.ok(stats);
    }
    
    // Get total rooms by status
    @GetMapping("/api/statistics/status/{trangThai}/total-rooms")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getTotalRoomsByTrangThai(@PathVariable String trangThai) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalRooms", quanLyPhongService.getTotalRoomsByTrangThai(trangThai));
        return ResponseEntity.ok(stats);
    }
    
    // Get total rooms by hotel and status
    @GetMapping("/api/statistics/hotel/{hotelId}/status/{trangThai}/total-rooms")
    @ResponseBody
    public ResponseEntity<Map<String, Long>> getTotalRoomsByHotelAndTrangThai(
            @PathVariable Integer hotelId, 
            @PathVariable String trangThai) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("totalRooms", quanLyPhongService.getTotalRoomsByHotelAndTrangThai(hotelId, trangThai));
        return ResponseEntity.ok(stats);
    }
    
    // Get comprehensive statistics
    @GetMapping("/api/statistics/comprehensive")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getComprehensiveStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRooms", quanLyPhongService.getTotalRooms());
        stats.put("sanSang", quanLyPhongService.getTotalRoomsByTrangThai("SAN_SANG"));
        stats.put("dangSuDung", quanLyPhongService.getTotalRoomsByTrangThai("DANG_SU_DUNG"));
        stats.put("baoTri", quanLyPhongService.getTotalRoomsByTrangThai("BAO_TRI"));
        stats.put("donDep", quanLyPhongService.getTotalRoomsByTrangThai("DON_DEP"));
        return ResponseEntity.ok(stats);
    }
    
    // Form handling endpoints for HTML template
    
    // Create room from form
    @PostMapping("/create-room")
    public String createRoomFromForm(@ModelAttribute RoomCreateDTO roomCreateDTO, Model model) {
        try {
            quanLyPhongService.createRoom(roomCreateDTO);
            return "redirect:/nhanvien/quanlyphong?success=created";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return view(model);
        }
    }
    
    // Update room from form
    @PostMapping("/update-room/{id}")
    public String updateRoomFromForm(@PathVariable Integer id, @ModelAttribute RoomUpdateDTO roomUpdateDTO, Model model) {
        try {
            quanLyPhongService.updateRoom(id, roomUpdateDTO);
            return "redirect:/nhanvien/quanlyphong?success=updated";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return view(model);
        }
    }
    
    // Delete room from form
    @PostMapping("/delete-room/{id}")
    public String deleteRoomFromForm(@PathVariable Integer id) {
        try {
            quanLyPhongService.deleteRoom(id);
            return "redirect:/nhanvien/quanlyphong?success=deleted";
        } catch (Exception e) {
            return "redirect:/nhanvien/quanlyphong?error=" + e.getMessage();
        }
    }
    
    // Create room type from form
    @PostMapping("/create-roomtype")
    public String createRoomTypeFromForm(@ModelAttribute RoomTypeCreateDTO roomTypeCreateDTO, Model model) {
        try {
            quanLyPhongService.createRoomType(roomTypeCreateDTO);
            return "redirect:/nhanvien/quanlyphong?success=roomtype_created";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return view(model);
        }
    }
    
    // Search rooms from form
    @GetMapping("/search")
    public String searchRooms(@RequestParam(required = false) String soPhong,
                             @RequestParam(required = false) String trangThai,
                             @RequestParam(required = false) Integer hotelId,
                             Model model) {
        List<RoomDTO> roomDTOs;
        if (soPhong != null && !soPhong.trim().isEmpty()) {
            roomDTOs = quanLyPhongService.searchRoomsBySoPhong(soPhong);
        } else if (trangThai != null && !trangThai.trim().isEmpty()) {
            roomDTOs = quanLyPhongService.getRoomsByTrangThai(trangThai);
        } else {
            roomDTOs = quanLyPhongService.getAllRoomsDTO();
        }
        model.addAttribute("rooms", roomDTOs);
        model.addAttribute("roomTypes", quanLyPhongService.getAllRoomTypes());
        List<String> trangThaiList = List.of(
            "SAN_SANG", "DANG_SU_DUNG", "BAO_TRI", "DON_DEP"
        );
        model.addAttribute("trangThaiList", trangThaiList);
        return "NhanVien/QuanLyPhong";
    }
} 