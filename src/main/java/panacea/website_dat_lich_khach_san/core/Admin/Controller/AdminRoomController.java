package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import java.util.Arrays;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminRoomService;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomTypeDTO;

@Controller
@RequestMapping("/admin/rooms")
public class AdminRoomController {
    
    @Autowired
    private AdminRoomService adminRoomService;
    
    @GetMapping
    public String roomManagement(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Integer roomTypeId,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String area,
        @RequestParam(required = false) String branch,
        Model model
    ) {
        List<RoomDTO> rooms = adminRoomService.filterRooms(keyword, roomTypeId, status, area, branch);
        List<RoomTypeDTO> roomTypes = adminRoomService.getAllRoomTypes();
        List<String> roomViews = Arrays.asList("City", "Pool", "Sea", "Garden");
        List<String> roomStatuses = Arrays.asList("SAN_SANG", "BAO_TRI", "DON_DEP");
        model.addAttribute("rooms", rooms);
        model.addAttribute("roomTypes", roomTypes);
        model.addAttribute("roomViews", roomViews);
        model.addAttribute("roomStatuses", roomStatuses);
        model.addAttribute("keyword", keyword);
        model.addAttribute("roomTypeId", roomTypeId);
        model.addAttribute("status", status);
        model.addAttribute("area", area);
        model.addAttribute("branch", branch);
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
    
    @GetMapping("/room-types")
    public String roomTypeManagement(
        @RequestParam(required = false) String keyword,
        Model model
    ) {
        List<RoomTypeDTO> roomTypes = adminRoomService.filterRoomTypes(keyword, null, null);
        model.addAttribute("roomTypes", roomTypes);
        model.addAttribute("keyword", keyword);
        return "Admin/view/QuanLyHangPhong";
    }

    @PostMapping("/room-types")
    @ResponseBody
    public RoomTypeDTO createRoomType(@RequestBody RoomTypeDTO roomTypeDTO) {
        return adminRoomService.createRoomType(roomTypeDTO);
    }
} 