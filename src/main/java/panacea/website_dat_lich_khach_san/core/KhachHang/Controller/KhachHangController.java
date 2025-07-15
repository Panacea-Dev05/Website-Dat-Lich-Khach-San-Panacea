package panacea.website_dat_lich_khach_san.core.KhachHang.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.BookingRequestDTO;
import panacea.website_dat_lich_khach_san.core.KhachHang.Service.KhachHangService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import panacea.website_dat_lich_khach_san.entity.Review;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;

@Controller
@RequestMapping("/khachhang")

public class KhachHangController {
    @Autowired
    private KhachHangService khachHangService;

    @Autowired
    private RoomRepository roomRepository;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "KhachHangDashboard";
    }

    // Trang chủ khách hàng
    @GetMapping("")
    public String home() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/index";
    }

    // Trang về khách sạn
    @GetMapping("/about")
    public String about() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/about-hotel-resort";
    }

    // Trang phòng
    @GetMapping("/room")
    public String rooms() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/room";
    }

    // Trang phòng (alias)
    @GetMapping("/rooms")
    public String roomsAlias() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/room";
    }

    // Trang chi tiết phòng (có review)
    @GetMapping("/single-room")
    public String roomDetail(@RequestParam(value = "roomId", required = false) Integer roomId, Model model) {
        if (roomId == null) {
            // Lấy phòng đầu tiên trong DB
            var firstRoom = roomRepository.findAll().stream().findFirst().orElse(null);
            if (firstRoom == null) {
                model.addAttribute("reviews", java.util.Collections.emptyList());
                model.addAttribute("roomId", null);
                return "KhachHang/livepreview/elegencia-main/hotel-resort/single-room";
            }
            roomId = firstRoom.getId();
        }
        List<Review> reviews = khachHangService.getApprovedReviewsByRoomId(roomId);
        model.addAttribute("reviews", reviews);
        model.addAttribute("roomId", roomId);
        return "KhachHang/livepreview/elegencia-main/hotel-resort/single-room";
    }

    // Trang chi tiết phòng (alias)
    @GetMapping("/room-detail")
    public String roomDetailAlias() {
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
        return "KhachHang/livepreview/elegencia-main/hotel-resort/restaurant";
    }

    // Trang spa & wellness
    @GetMapping("/spa-wellness")
    public String spaWellness() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/spa-wellness";
    }

    // Trang tiện ích khách sạn
    @GetMapping("/hotel-facilities")
    public String facilities() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/hotel-facilities";
    }

    // Trang tiện ích khách sạn (alias)
    @GetMapping("/facilities")
    public String facilitiesAlias() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/hotel-facilities";
    }

    // Trang sự kiện
    @GetMapping("/event-list")
    public String events() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/event-list";
    }

    // Trang sự kiện (alias)
    @GetMapping("/events")
    public String eventsAlias() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/event-list";
    }

    // Trang chi tiết sự kiện
    @GetMapping("/event-single-page")
    public String eventDetail() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/event-single-page";
    }

    // Trang chi tiết sự kiện (alias)
    @GetMapping("/event-detail")
    public String eventDetailAlias() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/event-single-page";
    }

    // Trang blog
    @GetMapping("/blog")
    public String blog() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/blog";
    }

    // Trang chi tiết blog
    @GetMapping("/blog-details")
    public String blogDetail() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/blog-details";
    }

    // Trang chi tiết blog (alias)
    @GetMapping("/blog-detail")
    public String blogDetailAlias() {
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

    // Trang đặt phòng (alias)
    @GetMapping("/reservations")
    public String reservations() {
        return "KhachHang/livepreview/elegencia-main/reservations";
    }

    // Trang 404
    @GetMapping("/404")
    public String notFound() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/404";
    }

    // Trang coming soon
    @GetMapping("/coming")
    public String comingSoon() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/comming";
    }

    // Trang coming soon (alias)
    @GetMapping("/coming-soon")
    public String comingSoonAlias() {
        return "KhachHang/livepreview/elegencia-main/hotel-resort/comming";
    }

    // Đặt phòng qua AJAX (JSON)
    @PostMapping(value = "/single-room/booking", consumes = "application/json", produces = "application/json")
    @ResponseBody
    public Object datPhongJson(@RequestBody BookingRequestDTO bookingRequestDTO) {
        boolean result = khachHangService.datPhongChoKhachHang(bookingRequestDTO);
        if (result) {
            return java.util.Map.of(
                "success", true,
                "message", "Đặt phòng thành công! Vui lòng kiểm tra email để xác nhận."
            );
        } else {
            return java.util.Map.of(
                "success", false,
                "message", "Đặt phòng thất bại. Vui lòng thử lại sau."
            );
        }
    }

    // Xử lý đặt phòng từ form single-room
//    @PostMapping("/single-room/booking")
//    public String datPhong(@ModelAttribute BookingRequestDTO bookingRequestDTO, Model model) {
//        boolean result = khachHangService.datPhongChoKhachHang(bookingRequestDTO);
//        model.addAttribute("result", result);
//        model.addAttribute("email", bookingRequestDTO.getEmailKhach());
//        // Trả về template thông báo chờ xác nhận
//        return result ? "KhachHang/livepreview/elegencia-main/hotel-resort/single-room-success" : "KhachHang/livepreview/elegencia-main/hotel-resort/single-room-fail";
//    }

    // Xử lý gửi review mới cho phòng
    @PostMapping("/single-room/review")
    public String postReview(@RequestParam("roomId") Integer roomId,
                             @RequestParam("bookingId") Integer bookingId,
                             @RequestParam("diemTongQuan") Byte diemTongQuan,
                             @RequestParam("binhLuan") String binhLuan,
                             @AuthenticationPrincipal UserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        // Lấy customerId từ userDetails hoặc session (giả sử username là email)
        Integer customerId = null;
        // TODO: Lấy customerId từ userDetails hoặc session
        // customerId = ...
        boolean result = khachHangService.addReviewForRoom(roomId, customerId, bookingId, diemTongQuan, binhLuan);
        redirectAttributes.addAttribute("roomId", roomId);
        return "redirect:/khachhang/single-room";
    }
}
