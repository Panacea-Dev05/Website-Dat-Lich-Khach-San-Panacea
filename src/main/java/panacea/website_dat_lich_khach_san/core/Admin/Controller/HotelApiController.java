package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.entity.HotelAmenities;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.HotelDTO;
import panacea.website_dat_lich_khach_san.repository.HotelAmenitiesRepository;
import panacea.website_dat_lich_khach_san.repository.HotelRepository;

@RestController
@RequestMapping("/api/hotels")
public class HotelApiController {
    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private HotelAmenitiesRepository hotelAmenitiesRepository;

    @GetMapping("/{id}")
    public ResponseEntity<HotelDTO> getHotelById(@PathVariable Integer id) {
        Hotel hotel = hotelRepository.findById(id).orElse(null);
        if (hotel == null) {
            return ResponseEntity.notFound().build();
        }
        HotelDTO dto = convertToDTO(hotel);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelDTO> updateHotel(@PathVariable Integer id, @RequestBody HotelDTO hotelDTO) {
        Hotel hotel = hotelRepository.findById(id).orElse(null);
        if (hotel == null) {
            return ResponseEntity.notFound().build();
        }
        hotel.setTenKhachSan(hotelDTO.getTenKhachSan());
        hotel.setDiaChi(hotelDTO.getDiaChi());
        hotel.setSoDienThoai(hotelDTO.getSoDienThoai());
        hotel.setEmail(hotelDTO.getEmail());
        if (hotelDTO.getTrangThai() != null) {
            hotel.setTrangThai(panacea.website_dat_lich_khach_san.entity.Hotel.TrangThaiHotel.valueOf(hotelDTO.getTrangThai()));
        }
        hotelRepository.save(hotel);
        return ResponseEntity.ok(convertToDTO(hotel));
    }

    @GetMapping("/{hotelId}/amenities")
    public ResponseEntity<?> listAmenities(@PathVariable Integer hotelId) {
        try {
            // Kiểm tra khách sạn có tồn tại không
            if (!hotelRepository.existsById(hotelId)) {
                return ResponseEntity.badRequest().body("Không tìm thấy khách sạn với ID: " + hotelId);
            }
            
            List<HotelAmenities> amenities = hotelAmenitiesRepository.findAll().stream()

                .toList();
            return ResponseEntity.ok(amenities);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lấy danh sách tiện ích: " + e.getMessage());
        }
    }

    @GetMapping("/code/{maKhachSan}/amenities")
    public ResponseEntity<?> listAmenitiesByCode(@PathVariable String maKhachSan) {
        try {
            // Kiểm tra khách sạn có tồn tại không
            Hotel hotel = hotelRepository.findByMaKhachSan(maKhachSan);
            if (hotel == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy khách sạn với mã: " + maKhachSan);
            }
            
            List<HotelAmenities> amenities = hotelAmenitiesRepository.findAll().stream()

                .toList();
            return ResponseEntity.ok(amenities);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi lấy danh sách tiện ích: " + e.getMessage());
        }
    }

    @PostMapping("/{hotelId}/amenities")
    public ResponseEntity<?> addAmenity(@PathVariable Integer hotelId, @RequestBody HotelAmenities amenity) {
        try {
            // Kiểm tra khách sạn có tồn tại không
            if (!hotelRepository.existsById(hotelId)) {
                return ResponseEntity.badRequest().body("Không tìm thấy khách sạn với ID: " + hotelId);
            }
            
            // Validate dữ liệu
            if (amenity.getTenTienIch() == null || amenity.getTenTienIch().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên tiện ích không được để trống");
            }
            
            // Set hotelId và lưu

            HotelAmenities savedAmenity = hotelAmenitiesRepository.save(amenity);
            return ResponseEntity.ok(savedAmenity);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi thêm tiện ích: " + e.getMessage());
        }
    }

    @PostMapping("/code/{maKhachSan}/amenities")
    public ResponseEntity<?> addAmenityByCode(@PathVariable String maKhachSan, @RequestBody HotelAmenities amenity) {
        try {
            // Kiểm tra khách sạn có tồn tại không
            Hotel hotel = hotelRepository.findByMaKhachSan(maKhachSan);
            if (hotel == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy khách sạn với mã: " + maKhachSan);
            }
            
            // Validate dữ liệu
            if (amenity.getTenTienIch() == null || amenity.getTenTienIch().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên tiện ích không được để trống");
            }
            
            // Set hotelId và lưu

            HotelAmenities savedAmenity = hotelAmenitiesRepository.save(amenity);
            return ResponseEntity.ok(savedAmenity);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi thêm tiện ích: " + e.getMessage());
        }
    }

    @PutMapping("/amenities/{amenityId}")
    public ResponseEntity<?> updateAmenity(@PathVariable Integer amenityId, @RequestBody HotelAmenities updatedAmenity) {
        try {
            HotelAmenities amenity = hotelAmenitiesRepository.findById(amenityId)
                .orElse(null);
            if (amenity == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Validate dữ liệu
            if (updatedAmenity.getTenTienIch() == null || updatedAmenity.getTenTienIch().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Tên tiện ích không được để trống");
            }
            
            amenity.setTenTienIch(updatedAmenity.getTenTienIch());
            amenity.setLoaiTienIch(updatedAmenity.getLoaiTienIch());
            amenity.setMoTa(updatedAmenity.getMoTa());
            amenity.setGioMo(updatedAmenity.getGioMo());
            amenity.setGioDong(updatedAmenity.getGioDong());
            amenity.setPhiSuDung(updatedAmenity.getPhiSuDung());
            amenity.setYeuCauDatTruoc(updatedAmenity.getYeuCauDatTruoc());
            amenity.setTrangThai(updatedAmenity.getTrangThai());
            amenity.setIcon(updatedAmenity.getIcon());
            
            HotelAmenities savedAmenity = hotelAmenitiesRepository.save(amenity);
            return ResponseEntity.ok(savedAmenity);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi cập nhật tiện ích: " + e.getMessage());
        }
    }

    @DeleteMapping("/amenities/{amenityId}")
    public ResponseEntity<?> deleteAmenity(@PathVariable Integer amenityId) {
        try {
            if (!hotelAmenitiesRepository.existsById(amenityId)) {
                return ResponseEntity.notFound().build();
            }
            hotelAmenitiesRepository.deleteById(amenityId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi khi xóa tiện ích: " + e.getMessage());
        }
    }

    private HotelDTO convertToDTO(Hotel hotel) {
        HotelDTO dto = new HotelDTO();
        dto.setId(hotel.getId());
        dto.setMaKhachSan(hotel.getMaKhachSan());
        dto.setTenKhachSan(hotel.getTenKhachSan());
        dto.setDiaChi(hotel.getDiaChi());
        dto.setThanhPho(hotel.getThanhPho());
        dto.setQuocGia(hotel.getQuocGia());
        dto.setSoDienThoai(hotel.getSoDienThoai());
        dto.setEmail(hotel.getEmail());
        dto.setWebsite(hotel.getWebsite());
        dto.setSoSao(hotel.getSoSao());
        dto.setMoTa(hotel.getMoTa());
        dto.setChinhSachHuy(hotel.getChinhSachHuy());
        dto.setThoiGianNhanPhong(hotel.getThoiGianNhanPhong());
        dto.setThoiGianTraPhong(hotel.getThoiGianTraPhong());
        dto.setTrangThai(hotel.getTrangThai() != null ? hotel.getTrangThai().name() : null);
        dto.setUuidId(hotel.getUuidId());
        dto.setCreatedDate(hotel.getCreatedDate());
        dto.setLastModifiedDate(hotel.getLastModifiedDate());
        return dto;
    }
} 