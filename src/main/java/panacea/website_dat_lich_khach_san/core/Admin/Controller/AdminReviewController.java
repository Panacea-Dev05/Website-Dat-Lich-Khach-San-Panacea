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

@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {
    
    @Autowired
    private AdminReviewService adminReviewService;
    
    @Autowired
    private AdminCustomerService adminCustomerService;
    
    @Autowired
    private AdminRoomService adminRoomService;
    
    @GetMapping
    public String reviewManagement(Model model) {
        List<ReviewDTO> reviews = adminReviewService.getAllReviews();
        model.addAttribute("reviews", reviews);
        model.addAttribute("customers", adminCustomerService.getAllCustomers());
        model.addAttribute("rooms", adminRoomService.getAllRooms());
        return "Admin/view/QuanLyDanhGia";
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public ReviewDTO getReview(@PathVariable Long id) {
        return adminReviewService.getReviewById(id);
    }
    
    // Admin chỉ có thể duyệt/từ chối đánh giá, không thể tạo/sửa
    @PutMapping("/{id}/approve")
    @ResponseBody
    public Object approveReview(@PathVariable Long id) {
        try {
            return adminReviewService.approveReview(id);
        } catch (Exception e) {
            return java.util.Map.of("error", true, "message", e.getMessage());
        }
    }
    
    @PutMapping("/{id}/reject")
    @ResponseBody
    public Object rejectReview(@PathVariable Long id) {
        try {
            return adminReviewService.rejectReview(id);
        } catch (Exception e) {
            return java.util.Map.of("error", true, "message", e.getMessage());
        }
    }
    
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