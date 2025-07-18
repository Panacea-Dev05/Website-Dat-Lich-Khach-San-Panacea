package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminPromotionService;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.PromotionDTO;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/promotions")
public class AdminPromotionController {
    
    @Autowired
    private AdminPromotionService adminPromotionService;
    
    @GetMapping
    public String promotionManagement(Model model) {
        List<PromotionDTO> promotions = adminPromotionService.getAllPromotions();
        List<String> promotionTypes = Arrays.asList("PHAN_TRAM", "SO_TIEN");
        List<String> promotionStatuses = Arrays.asList("HOAT_DONG", "TAM_NGUNG", "HET_HAN");
        model.addAttribute("promotions", promotions);
        model.addAttribute("promotionTypes", promotionTypes);
        model.addAttribute("promotionStatuses", promotionStatuses);
        return "Admin/view/QuanLyKhuyenMai";
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public PromotionDTO getPromotion(@PathVariable Integer id) {
        return adminPromotionService.getPromotionById(id);
    }
    
    @PostMapping
    @ResponseBody
    public PromotionDTO createPromotion(@RequestBody PromotionDTO promotionDTO) {
        return adminPromotionService.createPromotion(promotionDTO);
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