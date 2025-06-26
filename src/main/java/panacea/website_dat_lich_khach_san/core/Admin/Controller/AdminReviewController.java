package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminReviewService;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.ReviewDTO;

import java.util.List;

@Controller
@RequestMapping("/admin/reviews")
public class AdminReviewController {
    
    @Autowired
    private AdminReviewService adminReviewService;
    
    @GetMapping
    public String reviewManagement(Model model) {
        List<ReviewDTO> reviews = adminReviewService.getAllReviews();
        model.addAttribute("reviews", reviews);
        return "Admin/view/QuanLyDanhGia";
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public ReviewDTO getReview(@PathVariable Long id) {
        return adminReviewService.getReviewById(id);
    }
    
    @PostMapping
    @ResponseBody
    public ReviewDTO createReview(@RequestBody ReviewDTO reviewDTO) {
        return adminReviewService.createReview(reviewDTO);
    }
    
    @PutMapping("/{id}")
    @ResponseBody
    public ReviewDTO updateReview(@PathVariable Long id, @RequestBody ReviewDTO reviewDTO) {
        return adminReviewService.updateReview(id, reviewDTO);
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public boolean deleteReview(@PathVariable Long id) {
        return adminReviewService.deleteReview(id);
    }
} 