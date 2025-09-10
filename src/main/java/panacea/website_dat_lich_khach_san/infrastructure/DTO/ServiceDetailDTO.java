package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ServiceDetailDTO {
    private Integer id;
    private Integer bookingId;
    private Integer serviceId;
    private String serviceName; // Thêm trường tên dịch vụ
    private Integer roomId; // Thêm trường ID phòng
    private String roomNumber; // Thêm trường số phòng
    private Integer soLuong;
    private BigDecimal donGia;
    private BigDecimal thanhTien;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
}