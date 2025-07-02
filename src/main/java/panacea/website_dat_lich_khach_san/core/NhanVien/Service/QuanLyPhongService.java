package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;

import java.util.List;

@Service
public class QuanLyPhongService {
    @Autowired
    private RoomRepository roomRepository;

    public String getStaffName() {
        return "Nguyễn Văn A";
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
} 