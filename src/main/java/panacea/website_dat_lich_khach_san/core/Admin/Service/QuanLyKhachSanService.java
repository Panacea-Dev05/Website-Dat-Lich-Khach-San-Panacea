package panacea.website_dat_lich_khach_san.core.Admin.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.entity.HotelAmenities;
import panacea.website_dat_lich_khach_san.repository.HotelAmenitiesRepository;
import panacea.website_dat_lich_khach_san.repository.HotelRepository;

@Service
public class QuanLyKhachSanService {
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private HotelAmenitiesRepository hotelAmenitiesRepository;

    // 1. Cập nhật thông tin khách sạn
    @Transactional
    public Hotel updateHotel(Integer id, Hotel updatedHotel) {
        Optional<Hotel> hotelOpt = hotelRepository.findById(id);
        if (hotelOpt.isEmpty()) throw new RuntimeException("Hotel not found");
        Hotel hotel = hotelOpt.get();
        // Cập nhật các trường cơ bản
        hotel.setTenKhachSan(updatedHotel.getTenKhachSan());
        hotel.setDiaChi(updatedHotel.getDiaChi());
        hotel.setThanhPho(updatedHotel.getThanhPho());
        hotel.setQuocGia(updatedHotel.getQuocGia());
        hotel.setSoDienThoai(updatedHotel.getSoDienThoai());
        hotel.setEmail(updatedHotel.getEmail());
        hotel.setWebsite(updatedHotel.getWebsite());
        hotel.setSoSao(updatedHotel.getSoSao());
        hotel.setMoTa(updatedHotel.getMoTa());
        hotel.setChinhSachHuy(updatedHotel.getChinhSachHuy());
        hotel.setThoiGianNhanPhong(updatedHotel.getThoiGianNhanPhong());
        hotel.setThoiGianTraPhong(updatedHotel.getThoiGianTraPhong());
        // ... có thể bổ sung validate quyền admin, validate dữ liệu ở đây
        return hotelRepository.save(hotel);
    }

    // 2. Xem chi tiết khách sạn (bao gồm tiện ích)
    public Hotel getHotelDetail(Integer id) {
        Optional<Hotel> hotelOpt = hotelRepository.findById(id);
        if (hotelOpt.isEmpty()) throw new RuntimeException("Hotel not found");
        Hotel hotel = hotelOpt.get();
        // Fetch amenities nếu cần (Lazy)
        hotel.getAmenities().size();
        hotel.getRooms().size();
        return hotel;
    }

    // 3. Thay đổi trạng thái khách sạn
    @Transactional
    public Hotel changeHotelStatus(Integer id, Hotel.TrangThaiHotel newStatus) {
        Optional<Hotel> hotelOpt = hotelRepository.findById(id);
        if (hotelOpt.isEmpty()) throw new RuntimeException("Hotel not found");
        Hotel hotel = hotelOpt.get();
        // Validate trạng thái chuyển đổi nếu cần
        hotel.setTrangThai(newStatus);
        return hotelRepository.save(hotel);
    }

    // 4. CRUD tiện ích khách sạn
    // Thêm tiện ích
    @Transactional
    public HotelAmenities addAmenity(Integer hotelId, HotelAmenities amenity) {
        amenity.setKhachSanId(hotelId);
        return hotelAmenitiesRepository.save(amenity);
    }
    // Sửa tiện ích
    @Transactional
    public HotelAmenities updateAmenity(Integer amenityId, HotelAmenities updatedAmenity) {
        Optional<HotelAmenities> amenityOpt = hotelAmenitiesRepository.findById(amenityId);
        if (amenityOpt.isEmpty()) throw new RuntimeException("Amenity not found");
        HotelAmenities amenity = amenityOpt.get();
        amenity.setTenTienIch(updatedAmenity.getTenTienIch());
        amenity.setLoaiTienIch(updatedAmenity.getLoaiTienIch());
        amenity.setMoTa(updatedAmenity.getMoTa());
        amenity.setGioMo(updatedAmenity.getGioMo());
        amenity.setGioDong(updatedAmenity.getGioDong());
        amenity.setPhiSuDung(updatedAmenity.getPhiSuDung());
        amenity.setYeuCauDatTruoc(updatedAmenity.getYeuCauDatTruoc());
        amenity.setTrangThai(updatedAmenity.getTrangThai());
        amenity.setIcon(updatedAmenity.getIcon());
        return hotelAmenitiesRepository.save(amenity);
    }
    // Xóa tiện ích
    @Transactional
    public void deleteAmenity(Integer amenityId) {
        hotelAmenitiesRepository.deleteById(amenityId);
    }
    // Danh sách tiện ích theo khách sạn
    public List<HotelAmenities> listAmenities(Integer hotelId) {
        return hotelAmenitiesRepository.findAll().stream()
                .filter(a -> a.getKhachSanId().equals(hotelId))
                .toList();
    }
} 