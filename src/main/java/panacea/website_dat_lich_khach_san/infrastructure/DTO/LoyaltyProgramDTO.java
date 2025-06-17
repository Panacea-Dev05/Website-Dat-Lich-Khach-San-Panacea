package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class LoyaltyProgramDTO {
    private Integer id;
    private String tenChuongTrinh;
    private String capDo;
    private Integer diemToiThieu;
    private BigDecimal tiLeTichDiem;
    private String uuDai;
    private String mauSacThe;
    private String moTa;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
} 