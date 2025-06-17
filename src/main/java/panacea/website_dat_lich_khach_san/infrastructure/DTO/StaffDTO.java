package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class StaffDTO {
    private Integer id;
    private String maNhanVien;
    private String ho;
    private String ten;
    private String email;
    private String soDienThoai;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String chucVu;
    private String trangThai;
    private Integer hotelId;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
} 