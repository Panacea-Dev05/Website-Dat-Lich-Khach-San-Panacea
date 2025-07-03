package panacea.website_dat_lich_khach_san.core.Admin.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.repository.HotelRepository;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.HotelDTO;

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