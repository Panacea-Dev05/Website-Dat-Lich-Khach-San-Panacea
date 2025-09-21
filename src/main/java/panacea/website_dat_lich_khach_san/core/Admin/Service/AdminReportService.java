package panacea.website_dat_lich_khach_san.core.Admin.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.repository.CustomerRepository;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;
import panacea.website_dat_lich_khach_san.repository.RevenueAnalyticsRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

@Service
public class AdminReportService {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    
    @Autowired
    private RevenueAnalyticsRepository revenueAnalyticsRepository;

    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Tổng số booking đã xác nhận
            long totalBookings = bookingRepository.countByTrangThaiDatPhong(
                panacea.website_dat_lich_khach_san.entity.Booking.TrangThaiDatPhong.DA_XAC_NHAN);
            stats.put("totalBookings", totalBookings);
            
            // Tổng số khách hàng
            long totalCustomers = customerRepository.count();
            stats.put("totalCustomers", totalCustomers);
            
            // Tổng số phòng
            long totalRooms = roomRepository.count();
            stats.put("totalRooms", totalRooms);
            
            // Tính tỷ lệ lấp đầy thực tế
            double occupancyRate = calculateOccupancyRate();
            stats.put("occupancyRate", occupancyRate);
            
            // Tính doanh thu tháng hiện tại
            BigDecimal monthlyRevenue = calculateCurrentMonthRevenue();
            stats.put("monthlyRevenue", monthlyRevenue);
            
        } catch (Exception e) {
            // Fallback values nếu có lỗi
            stats.put("totalBookings", 0L);
            stats.put("totalCustomers", 0L);
            stats.put("totalRooms", 0L);
            stats.put("occupancyRate", 0.0);
            stats.put("monthlyRevenue", BigDecimal.ZERO);
        }
        
        return stats;
    }

    public Map<String, Object> getMonthlyRevenue() {
        Map<String, Object> revenue = new HashMap<>();
        
        try {
            int currentYear = LocalDate.now().getYear();
            List<Object[]> monthlyData = bookingRepository.getMonthlyRevenueByYear(currentYear);
            
            // Khởi tạo mảng 12 tháng với giá trị 0
            BigDecimal[] monthlyRevenues = new BigDecimal[12];
            Arrays.fill(monthlyRevenues, BigDecimal.ZERO);
            
            // Điền dữ liệu thực tế
            for (Object[] data : monthlyData) {
                int month = (Integer) data[0];
                BigDecimal monthRevenue = (BigDecimal) data[1];
                if (month >= 1 && month <= 12) {
                    monthlyRevenues[month - 1] = monthRevenue != null ? monthRevenue : BigDecimal.ZERO;
                }
            }
            
            // Tạo map với tên tháng
            String[] monthNames = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", 
                                  "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
            
            for (int i = 0; i < 12; i++) {
                revenue.put(monthNames[i], monthlyRevenues[i]);
            }
            
        } catch (Exception e) {
            // Fallback với dữ liệu mẫu nếu có lỗi
            String[] months = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", 
                              "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
            
            for (String month : months) {
                revenue.put(month, BigDecimal.ZERO);
            }
        }
        
        return revenue;
    }

    public Map<String, Object> getRoomTypeBookings() {
        Map<String, Object> bookings = new HashMap<>();
        
        try {
            List<Object[]> roomTypeData = bookingRepository.countBookingsByRoomType();
            
            for (Object[] data : roomTypeData) {
                String roomTypeName = (String) data[0];
                Long count = (Long) data[1];
                bookings.put(roomTypeName, count != null ? count : 0L);
            }
            
        } catch (Exception e) {
            // Fallback với dữ liệu mẫu nếu có lỗi
            bookings.put("Phòng Deluxe", 0L);
            bookings.put("Phòng Suite", 0L);
            bookings.put("Phòng Standard", 0L);
            bookings.put("Phòng VIP", 0L);
            bookings.put("Phòng Family", 0L);
        }
        
        return bookings;
    }

    public Map<String, Object> getOccupancyData() {
        Map<String, Object> occupancy = new HashMap<>();
        
        try {
            int currentYear = LocalDate.now().getYear();
            List<Object[]> monthlyData = revenueAnalyticsRepository.getMonthlyOccupancyRateByYear(currentYear);
            
            // Khởi tạo mảng 12 tháng với giá trị 0
            double[] monthlyOccupancy = new double[12];
            Arrays.fill(monthlyOccupancy, 0.0);
            
            // Điền dữ liệu thực tế
            for (Object[] data : monthlyData) {
                int month = (Integer) data[0];
                BigDecimal occupancyRate = (BigDecimal) data[1];
                if (month >= 1 && month <= 12) {
                    monthlyOccupancy[month - 1] = occupancyRate != null ? 
                        occupancyRate.doubleValue() : 0.0;
                }
            }
            
            // Tạo map với tên tháng
            String[] monthNames = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", 
                                  "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
            
            for (int i = 0; i < 12; i++) {
                occupancy.put(monthNames[i], Math.max(0, Math.min(100, monthlyOccupancy[i])));
            }
            
        } catch (Exception e) {
            // Fallback với dữ liệu mẫu nếu có lỗi
            String[] months = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", 
                              "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
            
            for (String month : months) {
                occupancy.put(month, 0.0);
            }
        }
        
        return occupancy;
    }
    
    // Helper methods
    private double calculateOccupancyRate() {
        try {
            long totalRooms = roomRepository.count();
            if (totalRooms == 0) return 0.0;
            
            // Tính số phòng đã đặt trong tháng hiện tại
            LocalDate now = LocalDate.now();
            LocalDate startOfMonth = now.withDayOfMonth(1);
            LocalDate endOfMonth = now.withDayOfMonth(now.lengthOfMonth());
            
            long occupiedRooms = bookingRepository.countBookingsByCheckInDateRange(startOfMonth, endOfMonth);
            
            return (double) occupiedRooms / totalRooms * 100;
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private BigDecimal calculateCurrentMonthRevenue() {
        try {
            LocalDate now = LocalDate.now();
            int currentMonth = now.getMonthValue();
            int currentYear = now.getYear();
            
            BigDecimal revenue = bookingRepository.sumRevenueByMonth(currentMonth, currentYear);
            return revenue != null ? revenue : BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}

