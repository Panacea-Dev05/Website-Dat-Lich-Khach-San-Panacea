package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class InventoryManagementDTO {
    private Integer id;
    private String tenVatTu;
    private String loaiVatTu;
    private BigDecimal soLuongTon;
    private String donViTinh;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
} 