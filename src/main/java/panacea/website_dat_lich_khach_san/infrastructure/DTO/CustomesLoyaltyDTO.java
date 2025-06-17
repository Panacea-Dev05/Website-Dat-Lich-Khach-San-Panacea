package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.util.UUID;

@Data
public class CustomesLoyaltyDTO {
    private Integer id;
    private Integer customerId;
    private Integer loyaltyProgramId;
    private Integer diemHienTai;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
} 