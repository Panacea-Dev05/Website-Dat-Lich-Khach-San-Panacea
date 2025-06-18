package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class testcontrolelr {

    @GetMapping("/tesst")
    public String testcontrolelr() {
        return "/NhanVien/viewtest";
    }
}
