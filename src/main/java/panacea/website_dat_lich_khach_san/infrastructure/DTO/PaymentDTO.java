package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class PaymentDTO {
    private Integer id;
    private Integer bookingId;
    private String hinhThucThanhToan;
    private BigDecimal soTien;
    private LocalDateTime thoiGianThanhToan;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
} 