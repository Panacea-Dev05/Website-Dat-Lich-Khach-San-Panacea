package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.repository.PaymentRepository;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;

@Service
public class HomeService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    public String getStaffName() {
        return "Nguyễn Văn A";
    }

    public List<Map<String, Object>> getOverviewList() {
        List<Map<String, Object>> overviewList = new ArrayList<>();

        // Đặt phòng hôm nay
        Map<String, Object> item1 = new HashMap<>();
        item1.put("category", "Đặt phòng hôm nay");
        item1.put("count", bookingRepository.count()); // Thay bằng countBookingToday nếu có
        item1.put("detail", "Đã xác nhận: " + bookingRepository.count()); // Thay bằng countConfirmedToday nếu có
        overviewList.add(item1);

        // Phòng cần dọn
        Map<String, Object> item2 = new HashMap<>();
        item2.put("category", "Phòng cần dọn");
        item2.put("count", roomRepository.count()); // Thay bằng countRoomsNeedCleaning nếu có
        item2.put("detail", "Checkout trong ngày");
        overviewList.add(item2);

        // Phòng đang bảo trì
        Map<String, Object> item3 = new HashMap<>();
        item3.put("category", "Phòng đang bảo trì");
        item3.put("count", 2); // Thay bằng số thực tế nếu có
        item3.put("detail", "Kết thúc vào mai");
        overviewList.add(item3);

        // Giao dịch hôm nay
        Map<String, Object> item4 = new HashMap<>();
        item4.put("category", "Giao dịch hôm nay");
        item4.put("count", paymentRepository.count()); // Thay bằng countPaymentsToday nếu có
        item4.put("detail", "Thành công: 8");
        overviewList.add(item4);

        // Yêu cầu mới
        Map<String, Object> item5 = new HashMap<>();
        item5.put("category", "Yêu cầu mới");
        item5.put("count", 3); // Thay bằng số thực tế nếu có
        item5.put("detail", "Chưa xử lý");
        overviewList.add(item5);

        return overviewList;
    }
} 