package panacea.website_dat_lich_khach_san.core.Admin.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.repository.CustomerRepository;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;
import panacea.website_dat_lich_khach_san.repository.RoomTypeRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminReportService {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Tổng số booking
        long totalBookings = bookingRepository.count();
        stats.put("totalBookings", totalBookings);
        
        // Tổng số khách hàng
        long totalCustomers = customerRepository.count();
        stats.put("totalCustomers", totalCustomers);
        
        // Tổng số phòng
        long totalRooms = roomRepository.count();
        stats.put("totalRooms", totalRooms);
        
        // Tỷ lệ lấp đầy (giả sử 75.5%)
        double occupancyRate = 75.5;
        stats.put("occupancyRate", occupancyRate);
        
        // Doanh thu tháng (dữ liệu mẫu)
        BigDecimal monthlyRevenue = new BigDecimal("15000000");
        stats.put("monthlyRevenue", monthlyRevenue);
        
        return stats;
    }

    public Map<String, Object> getMonthlyRevenue() {
        Map<String, Object> revenue = new HashMap<>();
        
        // Dữ liệu doanh thu theo tháng (dữ liệu mẫu)
        String[] months = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", 
                          "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        
        for (int i = 0; i < months.length; i++) {
            // Tạo dữ liệu ngẫu nhiên cho mỗi tháng
            int baseRevenue = 10000000 + (i * 500000);
            int randomFactor = (int) (Math.random() * 2000000) - 1000000; // ±1M
            revenue.put(months[i], baseRevenue + randomFactor);
        }
        
        return revenue;
    }

    public Map<String, Object> getRoomTypeBookings() {
        Map<String, Object> bookings = new HashMap<>();
        
        // Dữ liệu booking theo loại phòng (dữ liệu mẫu)
        bookings.put("Phòng Deluxe", 45);
        bookings.put("Phòng Suite", 32);
        bookings.put("Phòng Standard", 28);
        bookings.put("Phòng VIP", 18);
        bookings.put("Phòng Family", 12);
        
        return bookings;
    }

    public Map<String, Object> getOccupancyData() {
        Map<String, Object> occupancy = new HashMap<>();
        
        // Dữ liệu tỷ lệ lấp đầy theo tháng
        String[] months = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", 
                          "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        
        for (int i = 0; i < months.length; i++) {
            // Tạo dữ liệu tỷ lệ lấp đầy ngẫu nhiên
            double baseRate = 70.0 + (i * 1.5);
            double randomFactor = (Math.random() * 10) - 5; // ±5%
            occupancy.put(months[i], Math.max(0, Math.min(100, baseRate + randomFactor)));
        }
        
        return occupancy;
    }
}

