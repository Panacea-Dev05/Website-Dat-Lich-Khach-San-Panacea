package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.util.UUID;

@Data
public class HotelAmenitiesDTO {
    private Integer id;
    private Integer hotelId;
    private String tenTienNghi;
    private String moTa;
    private String trangThai;
    
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
} 