package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
import org.springframework.web.multipart.MultipartFile;

import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminRoomService;
import panacea.website_dat_lich_khach_san.entity.RoomImages;
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



    // API KIỂM TRA HẠNG PHÒNG: Kiểm tra xem hạng phòng có tồn tại không
    @GetMapping("/room-types/{id}/check")
    @ResponseBody
    public ResponseEntity<?> checkRoomType(@PathVariable Integer id) {
        try {
            RoomTypeDTO roomType = adminRoomService.getRoomTypeById(id);
            if (roomType == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Không tìm thấy hạng phòng với ID: " + id
                ));
            }
            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Tìm thấy hạng phòng: " + roomType.getTenLoaiPhong(),
                "roomType", roomType
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Lỗi kiểm tra hạng phòng: " + e.getMessage()
            ));
        }
    }

    // API LẤY ẢNH HẠNG PHÒNG: Lấy danh sách ảnh của hạng phòng
    @GetMapping("/room-types/{id}/images")
    @ResponseBody
    public ResponseEntity<?> getRoomTypeImages(@PathVariable Integer id) {
        try {
            System.out.println("=== GET ROOM TYPE IMAGES START ===");
            System.out.println("Room Type ID: " + id);
            
            List<RoomImages> images = adminRoomService.getRoomTypeImages(id);
            System.out.println("Found " + images.size() + " images");
            
            List<Map<String, Object>> imageList = new ArrayList<>();
            for (RoomImages image : images) {
                Map<String, Object> imageMap = new HashMap<>();
                imageMap.put("id", image.getId());
                imageMap.put("url", image.getUrlHinhAnh());
                imageMap.put("name", image.getTenHinhAnh());
                imageMap.put("description", image.getMoTa() != null ? image.getMoTa() : "");
                imageMap.put("isMain", image.getLaHinhChinh() != null ? image.getLaHinhChinh() : false);
                imageMap.put("order", image.getThuTuHienThi() != null ? image.getThuTuHienThi() : 1);
                imageList.add(imageMap);
            }
            
            System.out.println("=== GET ROOM TYPE IMAGES SUCCESS ===");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("images", imageList);
            return ResponseEntity.ok().body(response);
        } catch (Exception e) {
            System.err.println("=== GET ROOM TYPE IMAGES ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Lỗi lấy danh sách ảnh: " + e.getMessage()
            ));
        }
    }

    // API XÓA ẢNH HẠNG PHÒNG: Xóa ảnh của hạng phòng
    @DeleteMapping("/room-types/{roomTypeId}/images/{imageId}")
    @ResponseBody
    public ResponseEntity<?> deleteRoomTypeImage(
            @PathVariable Integer roomTypeId,
            @PathVariable Integer imageId) {
        
        try {
            System.out.println("=== DELETE ROOM TYPE IMAGE START ===");
            System.out.println("Room Type ID: " + roomTypeId);
            System.out.println("Image ID: " + imageId);
            
            boolean success = adminRoomService.deleteRoomTypeImage(imageId);
            
            if (success) {
                System.out.println("=== DELETE ROOM TYPE IMAGE SUCCESS ===");
                return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Xóa hình ảnh thành công"
                ));
            } else {
                System.out.println("=== DELETE ROOM TYPE IMAGE FAILED ===");
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Không thể xóa hình ảnh"
                ));
            }
        } catch (Exception e) {
            System.err.println("=== DELETE ROOM TYPE IMAGE ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Lỗi xóa hình ảnh: " + e.getMessage()
            ));
        }
    }

    // API LƯU ẢNH TỪ THƯ VIỆN: Lưu thông tin ảnh từ thư viện vào database
    @PostMapping("/room-types/{id}/images/save")
    @ResponseBody
    public ResponseEntity<?> saveRoomTypeImageFromLibrary(
            @PathVariable Integer id,
            @RequestBody Map<String, String> imageData) {
        
        try {
            System.out.println("=== SAVE IMAGE FROM LIBRARY START ===");
            System.out.println("Room Type ID: " + id);
            System.out.println("Image Data: " + imageData);
            
            String imageUrl = imageData.get("imageUrl");
            String imageName = imageData.get("imageName");
            String description = imageData.get("description");
            
            if (imageUrl == null || imageName == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Thiếu thông tin ảnh"
                ));
            }
            
            RoomImages image = adminRoomService.addRoomTypeImage(
                id,
                imageUrl,
                imageName,
                description != null ? description : "Hình ảnh hạng phòng từ thư viện"
            );
            
            System.out.println("=== SAVE IMAGE FROM LIBRARY SUCCESS ===");
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Lưu hình ảnh thành công");
            response.put("image", image);
            return ResponseEntity.ok().body(response);
            
        } catch (Exception e) {
            System.err.println("=== SAVE IMAGE FROM LIBRARY ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Lỗi lưu hình ảnh: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // API UPLOAD HÌNH ẢNH HẠNG PHÒNG: Upload hình ảnh cho hạng phòng (DEPRECATED - chỉ dùng cho upload file)
    @PostMapping("/room-types/{id}/images")
    @ResponseBody
    public ResponseEntity<?> uploadRoomTypeImages(
            @PathVariable Integer id,
            @RequestParam("files") MultipartFile[] files) {
        
        try {
            System.out.println("=== UPLOAD IMAGES START ===");
            System.out.println("Room Type ID: " + id);
            System.out.println("Number of files: " + files.length);
            
            // Kiểm tra hạng phòng có tồn tại không
            RoomTypeDTO roomType = adminRoomService.getRoomTypeById(id);
            if (roomType == null) {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Không tìm thấy hạng phòng với ID: " + id
                ));
            }
            
            List<RoomImages> uploadedImages = new ArrayList<>();
            
            for (int i = 0; i < files.length; i++) {
                MultipartFile file = files[i];
                if (file.isEmpty()) {
                    System.out.println("File " + i + " is empty, skipping...");
                    continue;
                }
                
                System.out.println("Processing file " + i + ": " + file.getOriginalFilename());
                
                // Tạo tên file unique
                String originalFilename = file.getOriginalFilename();
                if (originalFilename == null) continue;
                
                // Xử lý file extension an toàn
                String fileExtension = "";
                int lastDotIndex = originalFilename.lastIndexOf(".");
                if (lastDotIndex > 0 && lastDotIndex < originalFilename.length() - 1) {
                    fileExtension = originalFilename.substring(lastDotIndex);
                } else {
                    // Nếu không có extension hoặc extension không hợp lệ, dùng .jpg làm mặc định
                    fileExtension = ".jpg";
                }
                
                String uniqueFilename = "roomtype_" + id + "_" + System.currentTimeMillis() + "_" + i + fileExtension;
                
                // Tạo thư mục upload nếu chưa có
                // Sử dụng đường dẫn tuyệt đối từ project root
                String uploadDir = System.getProperty("user.dir") + "/src/main/resources/static/images/roomtypes/";
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) {
                    boolean created = uploadPath.mkdirs();
                    System.out.println("Created upload directory: " + created);
                    if (!created) {
                        System.err.println("Failed to create upload directory: " + uploadDir);
                        continue; // Bỏ qua file này nếu không tạo được thư mục
                    }
                }
                
                // Lưu file
                String filePath = uploadDir + uniqueFilename;
                File targetFile = new File(filePath);
                file.transferTo(targetFile);
                System.out.println("File saved to: " + filePath);
                
                // Tạo URL để truy cập hình ảnh
                String imageUrl = "/images/roomtypes/" + uniqueFilename;
                
                // Lưu thông tin hình ảnh vào database
                System.out.println("Saving image info to database...");
                RoomImages image = adminRoomService.addRoomTypeImage(
                    id, 
                    imageUrl, 
                    originalFilename, 
                    "Hình ảnh hạng phòng"
                );
                uploadedImages.add(image);
                System.out.println("Image saved with ID: " + image.getId());
            }
            
            System.out.println("=== UPLOAD IMAGES SUCCESS ===");
            System.out.println("Total uploaded: " + uploadedImages.size());
            
            return ResponseEntity.ok().body(Map.of(
                "success", true,
                "message", "Upload thành công " + uploadedImages.size() + " hình ảnh",
                "images", uploadedImages
            ));
            
        } catch (Exception e) {
            System.err.println("=== UPLOAD IMAGES ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Lỗi upload hình ảnh: " + e.getMessage()
            ));
        }
    }


}