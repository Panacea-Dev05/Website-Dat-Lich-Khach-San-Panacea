package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "REVENUE_ANALYTICS", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"khach_san_id", "ngay"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RevenueAnalytics {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "khach_san_id", nullable = false)
    private Integer khachSanId;

    @Column(name = "ngay", nullable = false)
    private LocalDate ngay;

    @Column(name = "tong_doanh_thu", precision = 15, scale = 2)
    private BigDecimal tongDoanhThu = BigDecimal.ZERO;

    @Column(name = "doanh_thu_phong", precision = 15, scale = 2)
    private BigDecimal doanhThuPhong = BigDecimal.ZERO;

    @Column(name = "doanh_thu_dich_vu", precision = 15, scale = 2)
    private BigDecimal doanhThuDichVu = BigDecimal.ZERO;

    @Column(name = "so_phong_ban")
    private Short soPhongBan = 0;

    @Column(name = "so_phong_trong")
    private Short soPhongTrong = 0;

    @Column(name = "ty_le_lay_day", precision = 5, scale = 2)
    private BigDecimal tyLeLayDay = BigDecimal.ZERO; // Occupancy rate %

    @Column(name = "gia_phong_trung_binh", precision = 12, scale = 2)
    private BigDecimal giaPhongTrungBinh = BigDecimal.ZERO; // ADR

    @Column(name = "doanh_thu_moi_phong", precision = 12, scale = 2)
    private BigDecimal doanhThuMoiPhong = BigDecimal.ZERO; // RevPAR

    @Column(name = "so_khach_moi")
    private Short soKhachMoi = 0;

    @Column(name = "so_khach_quay_lai")
    private Short soKhachQuayLai = 0;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_san_id", insertable = false, updatable = false)
    private Hotel hotel;

    @PrePersist
    public void prePersist() {
        if (this.uuidId == null) {
            this.uuidId = UUID.randomUUID();
        }
        if (this.createdDate == null) {
            this.createdDate = System.currentTimeMillis();
        }
    }
}