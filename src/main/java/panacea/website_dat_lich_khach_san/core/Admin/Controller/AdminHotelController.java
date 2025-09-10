package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.repository.HotelRepository;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.HotelDTO;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/hotels")
public class AdminHotelController {
    
    @Autowired
    private HotelRepository hotelRepository;
    
    @GetMapping
    public String hotelManagement(Model model) {
        // Lấy khách sạn đầu tiên (hoặc duy nhất) trong hệ thống
        Hotel hotel = hotelRepository.findAll().stream().findFirst().orElse(null);
        List<String> hotelStatuses = Arrays.asList("HOAT_DONG", "DONG_CUA", "BAO_TRI");
        
        if (hotel != null) {
            model.addAttribute("hotel", convertToDTO(hotel));
        }
        model.addAttribute("hotelStatuses", hotelStatuses);
        return "Admin/view/QuanLyKhachSan";
    }
    
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<HotelDTO> updateHotel(@RequestBody HotelDTO hotelDTO) {
        try {
            Hotel hotel = hotelRepository.findById(hotelDTO.getId()).orElse(null);
            if (hotel == null) {
                return ResponseEntity.notFound().build();
            }
            
            // Cập nhật thông tin khách sạn
            hotel.setTenKhachSan(hotelDTO.getTenKhachSan());
            hotel.setDiaChi(hotelDTO.getDiaChi());
            hotel.setThanhPho(hotelDTO.getThanhPho());
            // Đảm bảo quocGia không null
            hotel.setQuocGia(hotelDTO.getQuocGia() != null ? hotelDTO.getQuocGia() : "Việt Nam");
            hotel.setSoDienThoai(hotelDTO.getSoDienThoai());
            hotel.setEmail(hotelDTO.getEmail());
            hotel.setWebsite(hotelDTO.getWebsite());
            hotel.setSoSao(hotelDTO.getSoSao());
            hotel.setMoTa(hotelDTO.getMoTa());
            hotel.setChinhSachHuy(hotelDTO.getChinhSachHuy());
            hotel.setThoiGianNhanPhong(hotelDTO.getThoiGianNhanPhong());
            hotel.setThoiGianTraPhong(hotelDTO.getThoiGianTraPhong());
            
            if (hotelDTO.getTrangThai() != null) {
                hotel.setTrangThai(Hotel.TrangThaiHotel.valueOf(hotelDTO.getTrangThai()));
            }
            
            Hotel savedHotel = hotelRepository.save(hotel);
            return ResponseEntity.ok(convertToDTO(savedHotel));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/{id}")
    @ResponseBody
    public HotelDTO getHotel(@PathVariable Integer id) {
        return hotelRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }
    
    @GetMapping("/api/hotels/{id}")
    @ResponseBody
    public ResponseEntity<HotelDTO> getHotelById(@PathVariable Integer id) {
        Hotel hotel = hotelRepository.findById(id).orElse(null);
        if (hotel == null) {
            return ResponseEntity.notFound().build();
        }
        HotelDTO dto = convertToDTO(hotel);
        return ResponseEntity.ok(dto);
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