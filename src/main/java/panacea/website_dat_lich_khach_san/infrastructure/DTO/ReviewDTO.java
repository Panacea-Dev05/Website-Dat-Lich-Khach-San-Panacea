package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReviewDTO {
    private Integer id;
    private Integer bookingId;
    private Integer customerId;
    private Integer hotelId;
    private Integer roomId;
    private Integer staffId;
    private Integer serviceId;
    private Integer diemDanhGia;
    private String noiDung;
    private String trangThai;
    private LocalDateTime thoiGianDanhGia;
    private UUID uuidId;
    private Long createdDate;
} 