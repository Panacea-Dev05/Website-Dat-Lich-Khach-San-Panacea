package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.repository.CustomerRepository;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/reports")
public class AdminReportController {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @GetMapping
    public String reportsAndStatistics(Model model) {
        // Thêm dữ liệu thống kê vào model
        Map<String, Object> statistics = new HashMap<>();
        
        // Số lượng booking
        statistics.put("totalBookings", bookingRepository.count());
        
        // Số lượng khách hàng
        statistics.put("totalCustomers", customerRepository.count());
        
        // Số lượng phòng
        statistics.put("totalRooms", roomRepository.count());
        
        // Tỷ lệ lấp đầy phòng (có thể tính toán từ booking)
        statistics.put("occupancyRate", 75.5); // Giá trị mẫu
        
        // Doanh thu tháng hiện tại
        statistics.put("monthlyRevenue", 15000000); // Giá trị mẫu
        
        model.addAttribute("statistics", statistics);
        return "Admin/view/BaoCaovaThongKe";
    }
    
    @GetMapping("/revenue")
    @ResponseBody
    public Map<String, Object> getRevenueData() {
        // Logic để lấy dữ liệu doanh thu
        Map<String, Object> revenueData = new HashMap<>();
        revenueData.put("labels", new String[]{"T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12"});
        revenueData.put("data", new double[]{12000000, 13500000, 14200000, 15800000, 16500000, 17200000, 18000000, 17500000, 16800000, 15500000, 14800000, 16000000});
        return revenueData;
    }
    
    @GetMapping("/occupancy")
    @ResponseBody
    public Map<String, Object> getOccupancyData() {
        // Logic để lấy dữ liệu tỷ lệ lấp đầy
        Map<String, Object> occupancyData = new HashMap<>();
        occupancyData.put("labels", new String[]{"T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12"});
        occupancyData.put("data", new double[]{65, 70, 75, 80, 85, 90, 95, 92, 88, 82, 78, 85});
        return occupancyData;
    }
} 