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

    // Hiển thị form sửa phòng
    @GetMapping("/edit/{id}")
    public String editRoomForm(@PathVariable Integer id, Model model) {
        RoomDTO room = adminRoomService.getRoomById(id);
        if (room == null) {
            return "redirect:/admin/rooms?error=Phòng không tồn tại";
        }
        List<RoomTypeDTO> roomTypes = adminRoomService.getAllRoomTypes();
        List<String> roomViews = Arrays.asList("City", "Pool", "Sea", "Garden");
        List<String> roomStatuses = Arrays.asList("SAN_SANG", "BAO_TRI", "DON_DEP");
        model.addAttribute("room", room);
        model.addAttribute("roomTypes", roomTypes);
        model.addAttribute("roomViews", roomViews);
        model.addAttribute("roomStatuses", roomStatuses);
        return "Admin/QuanLyPhong/EditRoom";
    }

    // Xử lý cập nhật phòng từ form
    @PostMapping("/edit/{id}")
    public String updateRoomFromForm(@PathVariable Integer id, @ModelAttribute RoomDTO roomDTO, Model model) {
        RoomDTO updatedRoom = adminRoomService.updateRoom(id, roomDTO);
        if (updatedRoom == null) {
            model.addAttribute("error", "Không tìm thấy phòng để cập nhật");
            return editRoomForm(id, model);
        }
        return "redirect:/admin/rooms?success=updated";
    }

    // Xử lý xóa phòng (GET, xác nhận xong sẽ xóa và redirect)
    @GetMapping("/delete/{id}")
    public String deleteRoom(@PathVariable Integer id) {
        boolean deleted = adminRoomService.deleteRoom(id);
        if (deleted) {
            return "redirect:/admin/rooms?success=deleted";
        } else {
            return "redirect:/admin/rooms?error=Không tìm thấy phòng để xóa";
        }
    }
} 