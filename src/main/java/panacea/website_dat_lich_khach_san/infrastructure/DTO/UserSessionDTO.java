package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UserSessionDTO {
    private Integer id;
    private Integer userId;
    private String userType;
    private String sessionToken;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime thoiGianDangNhap;
    private LocalDateTime thoiGianHetHan;
    private LocalDateTime thoiGianHoatDongCuoi;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
} 