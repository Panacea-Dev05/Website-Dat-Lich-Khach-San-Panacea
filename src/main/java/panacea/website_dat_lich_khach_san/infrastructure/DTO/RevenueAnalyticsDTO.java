package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class RevenueAnalyticsDTO {
    private Integer id;
    private LocalDate ngay;
    private BigDecimal tongDoanhThu;
    private BigDecimal doanhThuPhong;
    private BigDecimal doanhThuDichVu;
    private Short soPhongBan;
    private Short soPhongTrong;
    private BigDecimal tyLeLayDay;
    private BigDecimal giaPhongTrungBinh;
    private BigDecimal doanhThuMoiPhong;
    private Short soKhachMoi;
    private Short soKhachQuayLai;
    private UUID uuidId;
    private Long createdDate;
} 