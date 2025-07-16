package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class ReportRevenueDTO {
    private Integer id;
    private Integer hotelId;
    private LocalDate ngayBaoCao;
    private BigDecimal tongDoanhThu;
    private BigDecimal doanhThuPhong;
    private BigDecimal doanhThuDichVu;
    private String trangThai;
    private UUID uuidId;
    private Long createdDate;
} 