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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import panacea.website_dat_lich_khach_san.repository.StaffRepository;
import panacea.website_dat_lich_khach_san.entity.Staff;

@Controller
@RequestMapping("/admin/hotels")
public class AdminHotelController {
    
    @Autowired
    private HotelRepository hotelRepository;
    
    @Autowired
    private StaffRepository staffRepository;
    
    @GetMapping
    public String hotelManagement(Model model) {
        // Lấy email đăng nhập từ security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Staff admin = staffRepository.findByEmail(email).orElse(null);
        String adminName = admin != null ? admin.getHoTen() : email;
        model.addAttribute("adminName", adminName);
        List<HotelDTO> hotels = hotelRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        List<String> hotelStatuses = Arrays.asList("HOAT_DONG", "DONG_CUA", "BAO_TRI");
        model.addAttribute("hotels", hotels);
        model.addAttribute("hotelStatuses", hotelStatuses);
        return "Admin/view/QuanLyKhachSan";
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