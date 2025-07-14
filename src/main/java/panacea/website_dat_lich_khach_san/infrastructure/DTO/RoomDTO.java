package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import panacea.website_dat_lich_khach_san.entity.Room;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

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
    
    // Thêm các trường giá theo hạng phòng
    private BigDecimal giaGio;
    private BigDecimal giaNgay;
    private BigDecimal giaQuaDem;

    // Thêm trường lưu đường dẫn ảnh
    private List<String> imageUrls;

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
            
            // Lấy thông tin giá từ RoomPricing
            // Note: Method này không thể truy cập trực tiếp RoomPricingRepository
            // nên sẽ cần được xử lý ở service layer
        }
        
        return dto;
    }
} 