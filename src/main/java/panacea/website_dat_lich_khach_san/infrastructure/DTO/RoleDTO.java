package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.util.UUID;

@Data
public class RoleDTO {
    private Integer id;
    private String tenVaiTro;
    private String moTa;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
} 