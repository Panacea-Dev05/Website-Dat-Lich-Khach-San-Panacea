package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminCustomerService;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminReviewService;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminRoomService;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.ReviewDTO;

import java.util.List;

// Controller quản lý đánh giá cho Admin
@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {
    
    @Autowired
    private AdminReviewService adminReviewService;
    
    @Autowired
    private AdminCustomerService adminCustomerService;
    
    @Autowired
    private AdminRoomService adminRoomService;
    
    // HIỂN THỊ TRANG QUẢN LÝ ĐÁNH GIÁ: Hiển thị danh sách đánh giá, khách hàng và phòng
    @GetMapping
    public String reviewManagement(Model model) {
        List<ReviewDTO> reviews = adminReviewService.getAllReviews();
        model.addAttribute("reviews", reviews);
        model.addAttribute("customers", adminCustomerService.getAllCustomers());
        model.addAttribute("rooms", adminRoomService.getAllRooms());
        return "Admin/view/QuanLyDanhGia";
    }
    
    // API LẤY THÔNG TIN ĐÁNH GIÁ: Lấy chi tiết thông tin đánh giá theo ID
    @GetMapping("/{id}")
    @ResponseBody
    public ReviewDTO getReview(@PathVariable Long id) {
        return adminReviewService.getReviewById(id);
    }
    
    // API DUYỆT ĐÁNH GIÁ: Duyệt đánh giá để hiển thị công khai (admin chỉ có thể duyệt/từ chối, không tạo/sửa)
    @PutMapping("/{id}/approve")
    @ResponseBody
    public Object approveReview(@PathVariable Long id) {
        try {
            return adminReviewService.approveReview(id);
        } catch (Exception e) {
            return java.util.Map.of("error", true, "message", e.getMessage());
        }
    }
    
    // API TỪ CHỐI ĐÁNH GIÁ: Từ chối đánh giá không phù hợp
    @PutMapping("/{id}/reject")
    @ResponseBody
    public Object rejectReview(@PathVariable Long id) {
        try {
            return adminReviewService.rejectReview(id);
        } catch (Exception e) {
            return java.util.Map.of("error", true, "message", e.getMessage());
        }
    }
    
    // API XÓA ĐÁNH GIÁ: Xóa đánh giá vi phạm hoặc không phù hợp
    @DeleteMapping("/{id}")
    @ResponseBody
    public Object deleteReview(@PathVariable Long id) {
        try {
            boolean deleted = adminReviewService.deleteReview(id);
            if (deleted) {
                return java.util.Map.of("success", true, "message", "Đã xóa đánh giá thành công");
            } else {
                return java.util.Map.of("error", true, "message", "Không tìm thấy đánh giá để xóa");
            }
        } catch (Exception e) {
            return java.util.Map.of("error", true, "message", e.getMessage());
        }
    }
}