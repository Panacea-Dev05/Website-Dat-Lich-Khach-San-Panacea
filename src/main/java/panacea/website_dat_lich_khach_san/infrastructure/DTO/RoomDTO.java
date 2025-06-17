package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RoomDTO {
    private Integer id;
    private String soPhong;
    private Byte tang;
    private String viewPhong;
    private String trangThai;
    private BigDecimal giaCoBan;
    private String ghiChu;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
    private Integer hotelId;
    private Integer roomTypeId;
} 