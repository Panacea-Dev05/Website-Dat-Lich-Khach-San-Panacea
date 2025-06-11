package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "REVENUE_ANALYTICS")

public class ReportRevenue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

//    DO phần tui ko có cái entity Hotel này
//    @ManyToOne
//    @JoinColumn(name = "khach_san_id", nullable = false)
//    private Hotel khachSan;

    @Column(name = "ngay", nullable = false)
    private LocalDate ngay;

    @Column(name = "tong_doanh_thu")
    private BigDecimal tongDoanhThu;

    @Column(name = "doanh_thu_phong")
    private BigDecimal doanhThuPhong;

    @Column(name = "doanh_thu_dich_vu")
    private BigDecimal doanhThuDichVu;

    @Column(name = "so_phong_ban")
    private Short soPhongBan;

    @Column(name = "so_phong_trong")
    private Short soPhongTrong;

    @Column(name = "ty_le_lay_day")
    private BigDecimal tyLeLayDay;

    @Column(name = "gia_phong_trung_binh")
    private BigDecimal giaPhongTrungBinh;

    @Column(name = "doanh_thu_moi_phong")
    private BigDecimal doanhThuMoiPhong;

    @Column(name = "so_khach_moi")
    private Short soKhachMoi;

    @Column(name = "so_khach_quay_lai")
    private Short soKhachQuayLai;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

}
