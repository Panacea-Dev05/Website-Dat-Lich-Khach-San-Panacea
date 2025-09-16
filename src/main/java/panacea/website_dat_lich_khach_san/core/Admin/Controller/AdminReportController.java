package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import panacea.website_dat_lich_khach_san.core.Admin.Service.AdminReportService;

import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminReportController {

    @Autowired
    private AdminReportService adminReportService;

    @GetMapping("/baocao-thongke")
    public String baoCaoThongKe(Model model) {
        // Lấy thống kê tổng quan
        Map<String, Object> statistics = adminReportService.getStatistics();
        model.addAttribute("statistics", statistics);
        
        // Lấy dữ liệu doanh thu theo tháng
        Map<String, Object> monthlyRevenue = adminReportService.getMonthlyRevenue();
        model.addAttribute("monthlyRevenue", monthlyRevenue);
        
        // Lấy dữ liệu booking theo loại phòng
        Map<String, Object> roomTypeBookings = adminReportService.getRoomTypeBookings();
        model.addAttribute("roomTypeBookings", roomTypeBookings);
        
        return "Admin/view/BaoCaovaThongKe";
    }

    // Các method để cung cấp dữ liệu cho JavaScript
    public Map<String, Object> getRevenueData() {
        return adminReportService.getMonthlyRevenue();
    }

    public Map<String, Object> getOccupancyData() {
        return adminReportService.getOccupancyData();
    }
}