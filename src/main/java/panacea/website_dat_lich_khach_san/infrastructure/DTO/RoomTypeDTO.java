package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.List;

import lombok.Data;
import panacea.website_dat_lich_khach_san.entity.RoomType;
import panacea.website_dat_lich_khach_san.entity.RoomPricing;

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
    private Integer soLuongPhong;
    
    // Thêm các trường giá
    private BigDecimal giaGio;
    private BigDecimal giaNgay;
    private BigDecimal giaQuaDem;

    // Thêm trường lưu đường dẫn ảnh
    private List<String> imageUrls;

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
    
    public static RoomTypeDTO fromEntityWithPricing(RoomType roomType, List<RoomPricing> pricings) {
        RoomTypeDTO dto = fromEntity(roomType);
        
        if (pricings != null && !pricings.isEmpty()) {
            // Lấy giá đầu tiên (có thể cải thiện logic này để lấy giá hiện tại)
            RoomPricing pricing = pricings.get(0);
            dto.setGiaGio(pricing.getGiaGio());
            dto.setGiaNgay(pricing.getGiaNgay());
            dto.setGiaQuaDem(pricing.getGiaQuaDem());
        }
        
        return dto;
    }
}