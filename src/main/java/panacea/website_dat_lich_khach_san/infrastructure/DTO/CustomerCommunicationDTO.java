package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CustomerCommunicationDTO {
    private Integer id;
    private Integer customerId;
    private String loaiThongBao;
    private String noiDung;
    private String trangThai;
    private LocalDateTime thoiGianGui;
    private UUID uuidId;
    private Long createdDate;
} 