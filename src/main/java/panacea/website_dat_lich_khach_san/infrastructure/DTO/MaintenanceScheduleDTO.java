package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MaintenanceScheduleDTO {
    private Integer id;
    private Integer phongId;
    private String loaiBaoTri;
    private String moTa;
    private LocalDateTime ngayBatDau;
    private LocalDateTime ngayKetThuc;
    private Integer nhanVienPhuTrach;
    private String trangThai;
    private BigDecimal chiPhi;
    private String ghiChu;
    private String mucDoUuTien;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
} 