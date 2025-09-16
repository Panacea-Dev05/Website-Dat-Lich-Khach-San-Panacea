package panacea.website_dat_lich_khach_san.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import panacea.website_dat_lich_khach_san.dto.FloorDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomDTO;
import panacea.website_dat_lich_khach_san.service.AdminFloorService;

// Controller quản lý tầng cho Admin
@Controller
@RequestMapping("/admin/floors")
public class AdminFloorController {

    @Autowired
    private AdminFloorService adminFloorService;

    /**
     * Hiển thị trang quản lý tầng
     */
    @GetMapping
    public String showFloorManagement(Model model) {
        List<FloorDTO> floors = adminFloorService.getAllFloors();
        model.addAttribute("floors", floors);
        return "admin/QuanLyTang";
    }

    /**
     * API lấy danh sách tất cả tầng với thống kê
     */
    @GetMapping("/api/all")
    @ResponseBody
    public ResponseEntity<List<FloorDTO>> getAllFloors() {
        List<FloorDTO> floors = adminFloorService.getAllFloors();
        return ResponseEntity.ok(floors);
    }

    /**
     * API lấy danh sách phòng theo tầng
     */
    @GetMapping("/api/{floorNumber}/rooms")
    @ResponseBody
    public ResponseEntity<List<RoomDTO>> getRoomsByFloor(@PathVariable Byte floorNumber) {
        List<RoomDTO> rooms = adminFloorService.getRoomsByFloor(floorNumber);
        return ResponseEntity.ok(rooms);
    }

    /**
     * API cập nhật tầng cho phòng
     */
    @PostMapping("/api/update-room-floor")
    @ResponseBody
    public ResponseEntity<String> updateRoomFloor(
            @RequestParam Integer roomId,
            @RequestParam Byte newFloor) {
        try {
            adminFloorService.updateRoomFloor(roomId, newFloor);
            return ResponseEntity.ok("Cập nhật tầng thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * API di chuyển tất cả phòng từ tầng này sang tầng khác
     */
    @PostMapping("/api/move-all-rooms")
    @ResponseBody
    public ResponseEntity<String> moveAllRoomsToFloor(
            @RequestParam Byte fromFloor,
            @RequestParam Byte toFloor) {
        try {
            adminFloorService.moveAllRoomsToFloor(fromFloor, toFloor);
            return ResponseEntity.ok("Di chuyển tất cả phòng thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    /**
     * API lấy thống kê tầng
     */
    @GetMapping("/api/{floorNumber}/statistics")
    @ResponseBody
    public ResponseEntity<FloorDTO> getFloorStatistics(@PathVariable Byte floorNumber) {
        FloorDTO floorStats = adminFloorService.getFloorStatistics(floorNumber);
        if (floorStats != null) {
            return ResponseEntity.ok(floorStats);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}