package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class SpecialRequestDTO {
    private Integer id;
    private Integer datPhongId;
    private String loaiYeuCau;
    private String noiDungYeuCau;
    private String mucDoUuTien;
    private String trangThai;
    private Integer nhanVienXuLy;
    private LocalDateTime thoiGianXuLy;
    private String ghiChuXuLy;
    private BigDecimal phiPhuThu;
    private UUID uuidId;
    private Long createdDate;
} 