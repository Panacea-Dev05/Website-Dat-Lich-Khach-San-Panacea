package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class RoomPricingDTO {
    private Integer id;
    private Integer roomTypeId;
    private String loaiGia;
    private BigDecimal giaTri;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private String apDungCho;
    private BigDecimal heSoDieuChinh;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
} 