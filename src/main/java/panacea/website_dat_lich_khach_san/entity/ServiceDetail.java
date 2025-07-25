package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "SERVICE_DETAIL")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "dat_phong_id", nullable = false)
    private Integer datPhongId;

    @Column(name = "dich_vu_id", nullable = false)
    private Integer dichVuId;

    @Column(name = "so_luong")
    private Short soLuong;

    @Column(name = "don_gia_thuc_te", precision = 12, scale = 2)
    private BigDecimal donGiaThucTe;

    @Column(name = "ngay_su_dung")
    private LocalDateTime ngaySuDung;

    @Column(name = "ghi_chu", length = 200)
    private String ghiChu;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dat_phong_id", insertable = false, updatable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dich_vu_id", insertable = false, updatable = false)
    private ServiceEntity service;

    @PrePersist
    public void prePersist() {
        if (this.uuidId == null) {
            this.uuidId = UUID.randomUUID();
        }
        if (this.createdDate == null) {
            this.createdDate = System.currentTimeMillis();
        }
        if (this.ngaySuDung == null) {
            this.ngaySuDung = LocalDateTime.now();
        }
    }
}
