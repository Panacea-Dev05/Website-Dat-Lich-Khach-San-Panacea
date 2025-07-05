package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RoomCreateDTO {
    private String soPhong;
    private Byte tang;
    private String viewPhong;
    private String trangThai;
    private BigDecimal giaCoBan;
    private String ghiChu;
    private Integer hotelId;
    private Integer roomTypeId;
} 