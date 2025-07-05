package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomTypeCreateDTO {
    private String maLoaiPhong;
    private String tenLoaiPhong;
    private BigDecimal dienTich;
    private Byte soGiuong;
    private String loaiGiuong;
    private Byte sucChuaToiDa;
    private String moTa;
    private String tienNghi;
} 