package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminRoomService;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomTypeDTO;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/rooms")
public class AdminRoomController {
    
    @Autowired
    private AdminRoomService adminRoomService;
    
    @GetMapping
    public String roomManagement(Model model) {
        List<RoomDTO> rooms = adminRoomService.getAllRooms();
        List<RoomTypeDTO> roomTypes = adminRoomService.getAllRoomTypes();
        List<String> roomViews = Arrays.asList("City", "Pool", "Sea", "Garden");
        List<String> roomStatuses = Arrays.asList("SAN_SANG", "BAO_TRI", "DON_DEP");
        model.addAttribute("rooms", rooms);
        model.addAttribute("roomTypes", roomTypes);
        model.addAttribute("roomViews", roomViews);
        model.addAttribute("roomStatuses", roomStatuses);
        return "Admin/view/QuanLyPhong";
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public RoomDTO getRoom(@PathVariable Integer id) {
        return adminRoomService.getRoomById(id);
    }
    
    @PostMapping
    @ResponseBody
    public RoomDTO createRoom(@RequestBody RoomDTO roomDTO) {
        return adminRoomService.createRoom(roomDTO);
    }
    
    @PutMapping("/{id}")
    @ResponseBody
    public RoomDTO updateRoom(@PathVariable Integer id, @RequestBody RoomDTO roomDTO) {
        return adminRoomService.updateRoom(id, roomDTO);
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public boolean deleteRoom(@PathVariable Integer id) {
        return adminRoomService.deleteRoom(id);
    }
} 