package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InventoryTransactionDTO {
    private Integer id;
    private Integer inventoryId;
    private BigDecimal soLuong;
    private LocalDateTime thoiGianGiaoDich;
    private String ghiChu;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
} 