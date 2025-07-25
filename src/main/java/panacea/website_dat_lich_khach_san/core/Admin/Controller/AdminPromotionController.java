package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminPromotionService;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.PromotionDTO;

import java.util.Arrays;
import java.util.List;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;

@Controller
@RequestMapping("/admin/promotions")
public class AdminPromotionController {
    
    @Autowired
    private AdminPromotionService adminPromotionService;
    
    @GetMapping
    public String promotionManagement(Model model) {
        List<PromotionDTO> promotions = adminPromotionService.getAllPromotions();
        if (promotions == null) promotions = new java.util.ArrayList<>();
        List<String> promotionTypes = Arrays.asList("PHAN_TRAM", "SO_TIEN");
        List<String> promotionStatuses = Arrays.asList("HOAT_DONG", "TAM_NGUNG", "HET_HAN");
        model.addAttribute("promotions", promotions);
        model.addAttribute("promotionTypes", promotionTypes);
        model.addAttribute("promotionStatuses", promotionStatuses);
        model.addAttribute("promotionForm", new PromotionDTO());
        return "Admin/view/QuanLyKhuyenMai";
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public PromotionDTO getPromotion(@PathVariable Integer id) {
        return adminPromotionService.getPromotionById(id);
    }
    
    @GetMapping("/edit/{id}")
    public String editPromotion(@PathVariable Integer id, Model model) {
        List<PromotionDTO> promotions = adminPromotionService.getAllPromotions();
        List<String> promotionTypes = Arrays.asList("PHAN_TRAM", "SO_TIEN");
        List<String> promotionStatuses = Arrays.asList("HOAT_DONG", "TAM_NGUNG", "HET_HAN");
        PromotionDTO promotion = adminPromotionService.getPromotionById(id);
        model.addAttribute("promotions", promotions);
        model.addAttribute("promotionTypes", promotionTypes);
        model.addAttribute("promotionStatuses", promotionStatuses);
        model.addAttribute("promotionForm", promotion);
        model.addAttribute("editMode", true);
        return "Admin/view/QuanLyKhuyenMai";
    }
    
    @PostMapping
    public String createPromotion(@ModelAttribute("promotionForm") @Validated PromotionDTO promotionDTO,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        // Kiểm tra lỗi validate cơ bản
        if (bindingResult.hasErrors()) {
            model.addAttribute("promotionForm", promotionDTO);
            model.addAttribute("promotionTypes", Arrays.asList("PHAN_TRAM", "SO_TIEN"));
            model.addAttribute("promotionStatuses", Arrays.asList("HOAT_DONG", "TAM_NGUNG", "HET_HAN"));
            model.addAttribute("promotions", adminPromotionService.getAllPromotions());
            model.addAttribute("error", "Vui lòng nhập đầy đủ và đúng thông tin!");
            return "Admin/view/QuanLyKhuyenMai";
        }
        // Kiểm tra mã khuyến mãi trùng
        List<PromotionDTO> allPromos = adminPromotionService.getAllPromotions();
        boolean isDuplicate = allPromos.stream().anyMatch(p -> p.getMaKhuyenMai().equalsIgnoreCase(promotionDTO.getMaKhuyenMai()));
        if (isDuplicate) {
            model.addAttribute("promotionForm", promotionDTO);
            model.addAttribute("promotionTypes", Arrays.asList("PHAN_TRAM", "SO_TIEN"));
            model.addAttribute("promotionStatuses", Arrays.asList("HOAT_DONG", "TAM_NGUNG", "HET_HAN"));
            model.addAttribute("promotions", allPromos);
            model.addAttribute("error", "Mã khuyến mãi đã tồn tại!");
            return "Admin/view/QuanLyKhuyenMai";
        }
        adminPromotionService.createPromotion(promotionDTO);
        redirectAttributes.addFlashAttribute("success", "Thêm khuyến mãi thành công!");
        return "redirect:/admin/promotions";
    }
    
    @PostMapping("/edit/{id}")
    public String updatePromotionForm(@PathVariable Integer id,
                                      @ModelAttribute("promotionForm") @Validated PromotionDTO promotionDTO,
                                      BindingResult bindingResult,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("promotionForm", promotionDTO);
            model.addAttribute("promotionTypes", Arrays.asList("PHAN_TRAM", "SO_TIEN"));
            model.addAttribute("promotionStatuses", Arrays.asList("HOAT_DONG", "TAM_NGUNG", "HET_HAN"));
            model.addAttribute("promotions", adminPromotionService.getAllPromotions());
            model.addAttribute("editMode", true);
            model.addAttribute("error", "Vui lòng nhập đầy đủ và đúng thông tin!");
            return "Admin/view/QuanLyKhuyenMai";
        }
        adminPromotionService.updatePromotion(id, promotionDTO);
        redirectAttributes.addFlashAttribute("success", "Cập nhật khuyến mãi thành công!");
        return "redirect:/admin/promotions";
    }
    
    @PutMapping("/{id}")
    @ResponseBody
    public PromotionDTO updatePromotion(@PathVariable Integer id, @RequestBody PromotionDTO promotionDTO) {
        return adminPromotionService.updatePromotion(id, promotionDTO);
    }
    
    @DeleteMapping("/{id}")
    @ResponseBody
    public boolean deletePromotion(@PathVariable Integer id) {
        return adminPromotionService.deletePromotion(id);
    }
} 