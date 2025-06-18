package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AuditLogDTO {
    private Integer id;
    private String bangTacDong;
    private Integer idBanGhi;
    private String hanhDong;
    private String duLieuCu;
    private String duLieuMoi;
    private Integer userId;
    private String userType;
    private String ipAddress;
    private LocalDateTime thoiGianThucHien;
    private String lyDoThayDoi;
    private UUID uuidId;
    private Long createdDate;
} 