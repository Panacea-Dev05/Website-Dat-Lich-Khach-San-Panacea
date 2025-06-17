package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class RoomAvailabilityDTO {
    private Integer id;
    private Integer phongId;
    private LocalDate ngay;
    private String trangThai;
    private BigDecimal giaBan;
    private Byte soPhongConLai;
    private String ghiChu;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
} 