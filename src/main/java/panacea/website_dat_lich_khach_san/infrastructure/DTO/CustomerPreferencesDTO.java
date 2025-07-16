package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.util.UUID;

@Data
public class CustomerPreferencesDTO {
    private Integer id;
    private Integer customerId;
    private String loaiPhongYeuThich;
    private String tienNghiYeuThich;
    private String khac;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
} 