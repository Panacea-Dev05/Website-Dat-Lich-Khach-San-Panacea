package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ServiceDTO {
    private Integer id;
    private String maDichVu;
    private String tenDichVu;
    private String loaiDichVu;
    private String moTa;
    private BigDecimal donGia;
    private String donViTinh;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
} 