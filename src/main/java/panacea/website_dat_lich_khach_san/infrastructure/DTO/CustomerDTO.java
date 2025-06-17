package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CustomerDTO {
    private Integer id;
    private String maKhachHang;
    private String ho;
    private String ten;
    private String email;
    private String soDienThoai;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String quocTich;
    private String soCmndCccd;
    private String soHoChieu;
    private String diaChi;
    private String loaiKhachHang;
    private Integer diemTichLuy;
    private String matKhauHash;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
} 