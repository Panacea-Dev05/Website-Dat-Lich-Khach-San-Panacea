package panacea.website_dat_lich_khach_san.core.KhachHang;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/khachhang")
public class KhachHangController {
    @GetMapping("/dashboard")
    public String dashboard() {
        return "KhachHangDashboard";
    }

    // Trang chủ khách hàng
    @GetMapping("/")
    public String home() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/index";
    }

    // Trang về khách sạn
    @GetMapping("/about")
    public String about() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/about-hotel-resort";
    }

    // Trang phòng
    @GetMapping("/rooms")
    public String rooms() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/room";
    }

    // Trang chi tiết phòng
    @GetMapping("/room-detail")
    public String roomDetail() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/single-room";
    }

    // Trang resort
    @GetMapping("/resort")
    public String resort() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/resort";
    }

    // Trang nhà hàng
    @GetMapping("/restaurant")
    public String restaurant() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/restaurent";
    }

    // Trang spa & wellness
    @GetMapping("/spa-wellness")
    public String spaWellness() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/spa-wellness";
    }

    // Trang tiện ích khách sạn
    @GetMapping("/facilities")
    public String facilities() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/hotel-facilities";
    }

    // Trang sự kiện
    @GetMapping("/events")
    public String events() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/event-list";
    }

    // Trang chi tiết sự kiện
    @GetMapping("/event-detail")
    public String eventDetail() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/event-single-page";
    }

    // Trang blog
    @GetMapping("/blog")
    public String blog() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/blog";
    }

    // Trang chi tiết blog
    @GetMapping("/blog-detail")
    public String blogDetail() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/blog-details";
    }

    // Trang FAQ
    @GetMapping("/faq")
    public String faq() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/faq";
    }

    // Trang liên hệ
    @GetMapping("/contact")
    public String contact() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/contact";
    }

    // Trang đặt phòng
    @GetMapping("/booking")
    public String booking() {
        return "KhachHang/livepreview/elegencia-main/reservations";
    }

    // Trang 404
    @GetMapping("/404")
    public String notFound() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/404";
    }

    // Trang coming soon
    @GetMapping("/coming-soon")
    public String comingSoon() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/comming";
    }
}
