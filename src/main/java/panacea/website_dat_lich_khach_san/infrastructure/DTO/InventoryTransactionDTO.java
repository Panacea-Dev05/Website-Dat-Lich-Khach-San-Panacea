package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import panacea.website_dat_lich_khach_san.entity.InventoryTransaction.LoaiGiaoDich;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransactionDTO {
    private Integer id;
    private Integer vatPhamId;
    private LoaiGiaoDich loaiGiaoDich;
    private Short soLuong;
    private BigDecimal giaTri;
    private String lyDo;
    private Integer nhanVienId;
    private Integer phongId;
    private LocalDateTime ngayGiaoDich;
    private String soChungTu;
    private UUID uuidId;
} 