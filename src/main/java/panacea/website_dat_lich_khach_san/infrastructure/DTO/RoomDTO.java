package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import panacea.website_dat_lich_khach_san.entity.Room;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class RoomDTO {
    private Integer id;
    private String soPhong;
    private Byte tang;
    private String viewPhong;
    private String trangThai;
    private BigDecimal giaCoBan;
    private String ghiChu;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
    private Integer hotelId;
    private String hotelName;
    private Integer roomTypeId;
    private String roomTypeName;
    private String maLoaiPhong;
    
    // Thêm thông tin từ RoomType
    private BigDecimal dienTich;
    private Byte soGiuong;
    private String loaiGiuong;
    private Byte sucChuaToiDa;
    private String moTa;
    private String tienNghi;

    public static RoomDTO fromEntity(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setSoPhong(room.getSoPhong());
        dto.setTang(room.getTang());
        dto.setViewPhong(room.getViewPhong());
        dto.setTrangThai(room.getTrangThai() != null ? room.getTrangThai().name() : null);
        dto.setGiaCoBan(room.getGiaCoBan());
        dto.setGhiChu(room.getGhiChu());
        dto.setUuidId(room.getUuidId());
        dto.setCreatedDate(room.getCreatedDate());
        dto.setLastModifiedDate(room.getLastModifiedDate());
        
        if (room.getHotel() != null) {
            dto.setHotelId(room.getHotel().getId());
            dto.setHotelName(room.getHotel().getTenKhachSan());
        }
        
        if (room.getRoomType() != null) {
            dto.setRoomTypeId(room.getRoomType().getId());
            dto.setRoomTypeName(room.getRoomType().getTenLoaiPhong());
            dto.setMaLoaiPhong(room.getRoomType().getMaLoaiPhong());
            dto.setDienTich(room.getRoomType().getDienTich());
            dto.setSoGiuong(room.getRoomType().getSoGiuong());
            dto.setLoaiGiuong(room.getRoomType().getLoaiGiuong());
            dto.setSucChuaToiDa(room.getRoomType().getSucChuaToiDa());
            dto.setMoTa(room.getRoomType().getMoTa());
            dto.setTienNghi(room.getRoomType().getTienNghi());
        }
        
        return dto;
    }
} 