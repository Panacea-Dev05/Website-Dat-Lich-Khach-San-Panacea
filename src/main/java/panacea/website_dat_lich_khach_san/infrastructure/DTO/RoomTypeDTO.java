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
        
        // Khởi tạo giá mặc định
        dto.setGiaGio(BigDecimal.ZERO);
        dto.setGiaNgay(BigDecimal.ZERO);
        dto.setGiaQuaDem(BigDecimal.ZERO);
        
        if (pricings != null && !pricings.isEmpty()) {
            System.out.println("[DEBUG] DTO Processing " + pricings.size() + " pricing records");
            
            // Tìm record có giá cao nhất (không null và > 0)
            RoomPricing bestPricing = null;
            for (RoomPricing pricing : pricings) {
                System.out.println("[DEBUG] DTO Checking pricing ID: " + pricing.getId() + 
                                 ", giaNgay: " + pricing.getGiaNgay() + 
                                 ", giaGio: " + pricing.getGiaGio() + 
                                 ", giaQuaDem: " + pricing.getGiaQuaDem());
                
                if (pricing.getGiaNgay() != null && pricing.getGiaNgay().compareTo(BigDecimal.ZERO) > 0) {
                    if (bestPricing == null || pricing.getGiaNgay().compareTo(bestPricing.getGiaNgay()) > 0) {
                        bestPricing = pricing;
                        System.out.println("[DEBUG] DTO New best pricing: " + pricing.getId());
                    }
                }
            }
            
            // Nếu tìm thấy record tốt, sử dụng nó
            if (bestPricing != null) {
                System.out.println("[DEBUG] DTO Using best pricing: " + bestPricing.getId());
                dto.setGiaGio(bestPricing.getGiaGio() != null ? bestPricing.getGiaGio() : BigDecimal.ZERO);
                dto.setGiaNgay(bestPricing.getGiaNgay() != null ? bestPricing.getGiaNgay() : BigDecimal.ZERO);
                dto.setGiaQuaDem(bestPricing.getGiaQuaDem() != null ? bestPricing.getGiaQuaDem() : BigDecimal.ZERO);
            } else {
                // Nếu không tìm thấy record tốt, sử dụng record đầu tiên
                System.out.println("[DEBUG] DTO No best pricing found, using first record");
                RoomPricing pricing = pricings.get(0);
                dto.setGiaGio(pricing.getGiaGio() != null ? pricing.getGiaGio() : BigDecimal.ZERO);
                dto.setGiaNgay(pricing.getGiaNgay() != null ? pricing.getGiaNgay() : BigDecimal.ZERO);
                dto.setGiaQuaDem(pricing.getGiaQuaDem() != null ? pricing.getGiaQuaDem() : BigDecimal.ZERO);
            }
            
            System.out.println("[DEBUG] DTO Final values: giaGio=" + dto.getGiaGio() + 
                             ", giaNgay=" + dto.getGiaNgay() + 
                             ", giaQuaDem=" + dto.getGiaQuaDem());
        } else {
            System.out.println("[DEBUG] DTO No pricing records provided");
        }
        
        return dto;
    }
}