package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class PromotionDTO {
    private Integer id;
    private String maKhuyenMai;
    private String tenKhuyenMai;
    private String moTa;
    private String loaiGiamGia;
    private BigDecimal giaTriGiam;
    private BigDecimal giamToiDa;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private Integer soLuongToiDa;
    private Integer daSuDung;
    private String dieuKienApDung;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
} 