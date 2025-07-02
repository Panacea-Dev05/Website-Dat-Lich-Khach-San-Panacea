package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;

import java.util.List;

@Service
public class QuanLyDatPhongService {
    @Autowired
    private BookingRepository bookingRepository;

    public String getStaffName() {
        return "Nguyễn Văn A";
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
} 