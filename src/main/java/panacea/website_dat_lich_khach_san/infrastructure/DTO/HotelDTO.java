package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class HotelDTO {
    private Integer id;
    private String maKhachSan;
    private String tenKhachSan;
    private String diaChi;
    private String thanhPho;
    private String quocGia;
    private String soDienThoai;
    private String email;
    private String website;
    private Byte soSao;
    private String moTa;
    private String chinhSachHuy;
    private LocalTime thoiGianNhanPhong;
    private LocalTime thoiGianTraPhong;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
} 