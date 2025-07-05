package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import panacea.website_dat_lich_khach_san.entity.RoomType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RoomTypeDTO {
    private Integer id;
    private String maLoaiPhong;
    private String tenLoaiPhong;
    private BigDecimal dienTich;
    private Byte soGiuong;
    private String loaiGiuong;
    private Byte sucChuaToiDa;
    private String moTa;
    private String tienNghi;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;

    public static RoomTypeDTO fromEntity(RoomType roomType) {
        RoomTypeDTO dto = new RoomTypeDTO();
        dto.setId(roomType.getId());
        dto.setMaLoaiPhong(roomType.getMaLoaiPhong());
        dto.setTenLoaiPhong(roomType.getTenLoaiPhong());
        dto.setDienTich(roomType.getDienTich());
        dto.setSoGiuong(roomType.getSoGiuong());
        dto.setLoaiGiuong(roomType.getLoaiGiuong());
        dto.setSucChuaToiDa(roomType.getSucChuaToiDa());
        dto.setMoTa(roomType.getMoTa());
        dto.setTienNghi(roomType.getTienNghi());
        dto.setUuidId(roomType.getUuidId());
        dto.setCreatedDate(roomType.getCreatedDate());
        dto.setLastModifiedDate(roomType.getLastModifiedDate());
        return dto;
    }
} 