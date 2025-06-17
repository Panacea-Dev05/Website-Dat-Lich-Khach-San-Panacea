package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.util.UUID;

@Data
public class PermissionDTO {
    private Integer id;
    private String maQuyen;
    private String tenQuyen;
    private String moTa;
    private String nhomQuyen;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
} 