package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class RoomsAvailabilityDTO {
    private Integer id;
    private Integer roomId;
    private LocalDate ngay;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
} 