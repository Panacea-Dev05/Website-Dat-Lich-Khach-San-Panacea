package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.util.UUID;

@Data
public class RolePermissionDTO {
    private Integer id;
    private String vaiTro;
    private Integer quyenId;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
} 