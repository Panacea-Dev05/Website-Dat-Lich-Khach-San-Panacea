package panacea.website_dat_lich_khach_san.core.Admin.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.RevenueAnalytics;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;
import panacea.website_dat_lich_khach_san.repository.RevenueAnalyticsRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class RevenueAnalyticsService {

    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RevenueAnalyticsRepository revenueAnalyticsRepository;

    /**
     * Tạo báo cáo doanh thu cho ngày hiện tại
     * Chạy tự động mỗi ngày lúc 23:59
     */
    @Scheduled(cron = "0 59 23 * * ?")
    public void generateDailyRevenueReport() {
        try {
            LocalDate today = LocalDate.now();
            
            // Kiểm tra xem đã có báo cáo cho ngày hôm nay chưa
            if (revenueAnalyticsRepository.findByNgay(today).isPresent()) {
                return; // Đã có báo cáo, không tạo lại
            }
            
            RevenueAnalytics report = calculateDailyRevenue(today);
            revenueAnalyticsRepository.save(report);
            
        } catch (Exception e) {
            System.err.println("Lỗi khi tạo báo cáo doanh thu hàng ngày: " + e.getMessage());
        }
    }

    /**
     * Tính toán báo cáo doanh thu cho một ngày cụ thể
     * @param date ngày cần tính toán
     * @return RevenueAnalytics chứa dữ liệu báo cáo
     */
    public RevenueAnalytics calculateDailyRevenue(LocalDate date) {
        RevenueAnalytics report = new RevenueAnalytics();
        report.setNgay(date);
        report.setUuidId(UUID.randomUUID());
        report.setCreatedDate(System.currentTimeMillis());
        
        try {
            // Tính tổng doanh thu từ booking đã xác nhận trong ngày
            BigDecimal totalRevenue = bookingRepository.sumRevenueByDateRange(date, date);
            report.setTongDoanhThu(totalRevenue != null ? totalRevenue : BigDecimal.ZERO);
            
            // Tạm thời coi tất cả doanh thu là từ phòng (có thể mở rộng sau)
            report.setDoanhThuPhong(report.getTongDoanhThu());
            report.setDoanhThuDichVu(BigDecimal.ZERO);
            
            // Tính số phòng bán và trống
            long totalRooms = roomRepository.count();
            long occupiedRooms = bookingRepository.countBookingsByCheckInDate(date);
            
            report.setSoPhongBan((short) occupiedRooms);
            report.setSoPhongTrong((short) (totalRooms - occupiedRooms));
            
            // Tính tỷ lệ lấp đầy
            if (totalRooms > 0) {
                BigDecimal occupancyRate = BigDecimal.valueOf(occupiedRooms)
                    .divide(BigDecimal.valueOf(totalRooms), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
                report.setTyLeLayDay(occupancyRate);
            } else {
                report.setTyLeLayDay(BigDecimal.ZERO);
            }
            
            // Tính giá phòng trung bình (ADR)
            if (occupiedRooms > 0) {
                BigDecimal adr = report.getTongDoanhThu()
                    .divide(BigDecimal.valueOf(occupiedRooms), 2, RoundingMode.HALF_UP);
                report.setGiaPhongTrungBinh(adr);
            } else {
                report.setGiaPhongTrungBinh(BigDecimal.ZERO);
            }
            
            // Tính doanh thu mỗi phòng (RevPAR)
            if (totalRooms > 0) {
                BigDecimal revpar = report.getTongDoanhThu()
                    .divide(BigDecimal.valueOf(totalRooms), 2, RoundingMode.HALF_UP);
                report.setDoanhThuMoiPhong(revpar);
            } else {
                report.setDoanhThuMoiPhong(BigDecimal.ZERO);
            }
            
            // Tạm thời set số khách mới và quay lại = 0 (có thể mở rộng sau)
            report.setSoKhachMoi((short) 0);
            report.setSoKhachQuayLai((short) 0);
            
        } catch (Exception e) {
            // Nếu có lỗi, set tất cả về 0
            report.setTongDoanhThu(BigDecimal.ZERO);
            report.setDoanhThuPhong(BigDecimal.ZERO);
            report.setDoanhThuDichVu(BigDecimal.ZERO);
            report.setSoPhongBan((short) 0);
            report.setSoPhongTrong((short) 0);
            report.setTyLeLayDay(BigDecimal.ZERO);
            report.setGiaPhongTrungBinh(BigDecimal.ZERO);
            report.setDoanhThuMoiPhong(BigDecimal.ZERO);
            report.setSoKhachMoi((short) 0);
            report.setSoKhachQuayLai((short) 0);
        }
        
        return report;
    }

    /**
     * Tạo báo cáo cho một khoảng thời gian
     * @param startDate ngày bắt đầu
     * @param endDate ngày kết thúc
     */
    public void generateRevenueReportsForPeriod(LocalDate startDate, LocalDate endDate) {
        LocalDate currentDate = startDate;
        
        while (!currentDate.isAfter(endDate)) {
            try {
                // Kiểm tra xem đã có báo cáo cho ngày này chưa
                if (!revenueAnalyticsRepository.findByNgay(currentDate).isPresent()) {
                    RevenueAnalytics report = calculateDailyRevenue(currentDate);
                    revenueAnalyticsRepository.save(report);
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi tạo báo cáo cho ngày " + currentDate + ": " + e.getMessage());
            }
            
            currentDate = currentDate.plusDays(1);
        }
    }

    /**
     * Lấy báo cáo tổng hợp cho tháng hiện tại
     * @param year năm
     * @param month tháng
     * @return RevenueAnalytics tổng hợp cho tháng
     */
    public RevenueAnalytics getMonthlySummary(int year, int month) {
        try {
            List<RevenueAnalytics> monthlyReports = revenueAnalyticsRepository.findByMonthAndYear(month, year);
            
            if (monthlyReports.isEmpty()) {
                return null;
            }
            
            // Tính tổng hợp
            RevenueAnalytics summary = new RevenueAnalytics();
            summary.setNgay(LocalDate.of(year, month, 1));
            summary.setTongDoanhThu(BigDecimal.ZERO);
            summary.setDoanhThuPhong(BigDecimal.ZERO);
            summary.setDoanhThuDichVu(BigDecimal.ZERO);
            summary.setSoPhongBan((short) 0);
            summary.setSoPhongTrong((short) 0);
            summary.setTyLeLayDay(BigDecimal.ZERO);
            summary.setGiaPhongTrungBinh(BigDecimal.ZERO);
            summary.setDoanhThuMoiPhong(BigDecimal.ZERO);
            summary.setSoKhachMoi((short) 0);
            summary.setSoKhachQuayLai((short) 0);
            
            for (RevenueAnalytics report : monthlyReports) {
                summary.setTongDoanhThu(summary.getTongDoanhThu().add(report.getTongDoanhThu()));
                summary.setDoanhThuPhong(summary.getDoanhThuPhong().add(report.getDoanhThuPhong()));
                summary.setDoanhThuDichVu(summary.getDoanhThuDichVu().add(report.getDoanhThuDichVu()));
                summary.setSoPhongBan((short) (summary.getSoPhongBan() + report.getSoPhongBan()));
                summary.setSoPhongTrong((short) (summary.getSoPhongTrong() + report.getSoPhongTrong()));
                summary.setSoKhachMoi((short) (summary.getSoKhachMoi() + report.getSoKhachMoi()));
                summary.setSoKhachQuayLai((short) (summary.getSoKhachQuayLai() + report.getSoKhachQuayLai()));
            }
            
            // Tính trung bình
            int daysInMonth = monthlyReports.size();
            if (daysInMonth > 0) {
                summary.setTyLeLayDay(summary.getTyLeLayDay().divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP));
                summary.setGiaPhongTrungBinh(summary.getGiaPhongTrungBinh().divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP));
                summary.setDoanhThuMoiPhong(summary.getDoanhThuMoiPhong().divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP));
            }
            
            return summary;
            
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy báo cáo tổng hợp tháng: " + e.getMessage());
            return null;
        }
    }
}
