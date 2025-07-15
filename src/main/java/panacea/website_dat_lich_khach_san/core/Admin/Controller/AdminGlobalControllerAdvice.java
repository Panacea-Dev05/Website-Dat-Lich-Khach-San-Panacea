package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;
import panacea.website_dat_lich_khach_san.repository.StaffRepository;
import panacea.website_dat_lich_khach_san.entity.Staff;

@ControllerAdvice("panacea.website_dat_lich_khach_san.core.Admin.Controller")
public class AdminGlobalControllerAdvice {

    @Autowired
    private StaffRepository staffRepository;

    @ModelAttribute
    public void addAdminName(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getName())) {
            String email = authentication.getName();
            Staff admin = staffRepository.findByEmail(email).orElse(null);
            String adminName = admin != null ? admin.getHoTen() : email;
            model.addAttribute("adminName", adminName);
        } else {
            model.addAttribute("adminName", "Admin");
        }
    }
} 