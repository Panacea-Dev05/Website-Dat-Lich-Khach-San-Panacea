package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.util.UUID;

@Data
public class RoomImagesDTO {
    private Integer id;
    private Integer phongId;
    private Integer loaiPhongId;
    private String urlHinhAnh;
    private String tenHinhAnh;
    private String moTa;
    private Byte thuTuHienThi;
    private Boolean laHinhChinh;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
} 