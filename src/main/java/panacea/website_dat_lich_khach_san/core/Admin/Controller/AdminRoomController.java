package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

// Controller quản lý phòng và loại phòng cho Admin
@Controller
@RequestMapping("/admin/rooms")
public class AdminRoomController {
    
    @Autowired
    private AdminRoomService adminRoomService;
    
    // HIỂN THỊ TRANG QUẢN LÝ PHÒNG: Hiển thị danh sách phòng với tính năng tìm kiếm, lọc và phân trang
    @GetMapping
    public String roomManagement(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Integer roomTypeId,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String area,
        @RequestParam(required = false) String branch,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "1000") int size, // Tăng size để lấy tất cả dữ liệu
        Model model
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<RoomDTO> roomPage = adminRoomService.filterRoomsPaged(keyword, roomTypeId, status, area, branch, pageable);
        List<RoomTypeDTO> roomTypes = adminRoomService.getAllRoomTypes();
        List<String> roomViews = Arrays.asList("City", "Pool", "Sea", "Garden");
        List<String> roomStatuses = Arrays.asList("SAN_SANG", "BAO_TRI", "DON_DEP");
        
        model.addAttribute("rooms", roomPage.getContent());
        model.addAttribute("roomTypes", roomTypes);
        model.addAttribute("roomViews", roomViews);
        model.addAttribute("roomStatuses", roomStatuses);
        model.addAttribute("keyword", keyword);
        model.addAttribute("roomTypeId", roomTypeId);
        model.addAttribute("status", status);
        model.addAttribute("area", area);
        model.addAttribute("branch", branch);
        
        // Phân trang
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", Math.max(1, roomPage.getTotalPages())); // Đảm bảo ít nhất 1 trang
        model.addAttribute("totalItems", roomPage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("hasNext", roomPage.hasNext());
        model.addAttribute("hasPrevious", roomPage.hasPrevious());
        
        return "Admin/view/QuanLyPhong";
    }
    
    // API LẤY THÔNG TIN PHÒNG: Lấy chi tiết thông tin phòng theo ID
    @GetMapping("/{id}")
    @ResponseBody
    public RoomDTO getRoom(@PathVariable Integer id) {
        return adminRoomService.getRoomById(id);
    }
    
    // API TẠO PHÒNG MỚI: Tạo phòng mới với thông tin từ form
    @PostMapping
    @ResponseBody
    public RoomDTO createRoom(@RequestBody RoomDTO roomDTO) {
        return adminRoomService.createRoom(roomDTO);
    }
    
    // API CẬP NHẬT PHÒNG: Cập nhật thông tin phòng theo ID
    @PutMapping("/{id}")
    @ResponseBody
    public RoomDTO updateRoom(@PathVariable Integer id, @RequestBody RoomDTO roomDTO) {
        return adminRoomService.updateRoom(id, roomDTO);
    }
    
    // API XÓA PHÒNG: Xóa phòng theo ID
    @DeleteMapping("/{id}")
    @ResponseBody
    public boolean deleteRoom(@PathVariable Integer id) {
        return adminRoomService.deleteRoom(id);
    }
    
    // HIỂN THỊ TRANG QUẢN LÝ LOẠI PHÒNG: Hiển thị danh sách loại phòng với tính năng tìm kiếm
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

    // API LẤY THÔNG TIN LOẠI PHÒNG: Lấy chi tiết thông tin loại phòng theo ID
    @GetMapping("/room-types/{id}")
    @ResponseBody
    public RoomTypeDTO getRoomType(@PathVariable Integer id) {
        return adminRoomService.getRoomTypeById(id);
    }
    
    // API TẠO LOẠI PHÒNG MỚI: Tạo loại phòng mới với thông tin từ form
    @PostMapping("/room-types")
    @ResponseBody
    public RoomTypeDTO createRoomType(@RequestBody RoomTypeDTO roomTypeDTO) {
        return adminRoomService.createRoomType(roomTypeDTO);
    }
    
    // API CẬP NHẬT LOẠI PHÒNG: Cập nhật thông tin loại phòng theo ID
    @PutMapping("/room-types/{id}")
    @ResponseBody
    public RoomTypeDTO updateRoomType(@PathVariable Integer id, @RequestBody RoomTypeDTO roomTypeDTO) {
        return adminRoomService.updateRoomType(id, roomTypeDTO);
    }
    
    // API XÓA LOẠI PHÒNG: Xóa loại phòng theo ID
    @DeleteMapping("/room-types/{id}")
    @ResponseBody
    public boolean deleteRoomType(@PathVariable Integer id) {
        return adminRoomService.deleteRoomType(id);
    }

    // API LẤY DANH SÁCH LOẠI PHÒNG JSON: Trả về tất cả loại phòng dạng JSON cho dropdown/select
    @GetMapping("/room-types/json")
    @ResponseBody
    public List<RoomTypeDTO> getRoomTypesJson() {
        return adminRoomService.getAllRoomTypes();
    }
}