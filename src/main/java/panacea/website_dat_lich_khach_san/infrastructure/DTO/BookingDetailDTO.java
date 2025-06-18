package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class BookingDetailDTO {
    private Integer id;
    private Integer bookingId;
    private Integer roomId;
    private String tenKhach;
    private String soCmndCccd;
    private String soHoChieu;
    private String ghiChu;
    private BigDecimal giaPhong;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
} 