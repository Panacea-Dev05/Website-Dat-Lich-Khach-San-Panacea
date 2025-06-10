package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ROOM_PRICING")

@Setter
@Getter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "loai_phong_id", nullable = false)
    private Integer loaiPhongId;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_gia", length = 20)
    private LoaiGia loaiGia;

    @Column(name = "gia_tri", precision = 12, scale = 2)
    private BigDecimal giaTri;

    @Column(name = "ngay_bat_dau", nullable = false)
    private java.time.LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private java.time.LocalDate ngayKetThuc;

    @Column(name = "ap_dung_cho", length = 100)
    private String apDungCho = "All";

    @Column(name = "he_so_dieu_chinh", precision = 3, scale = 2)
    private BigDecimal heSoDieuChinh = BigDecimal.ONE;

    @Column(name = "trang_thai", length = 20)
    private String trangThai = "Hoạt động";

    @Column(name = "uuid_id", columnDefinition = "uniqueidentifier")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loai_phong_id", insertable = false, updatable = false)
    private RoomType roomType;

    // Enums
    public enum LoaiGia {
        Base, Weekend, Holiday, Peak_Season
    }

    @PrePersist
    public void prePersist() {
        if (this.uuidId == null) {
            this.uuidId = UUID.randomUUID();
        }
        if (this.createdDate == null) {
            this.createdDate = System.currentTimeMillis();
        }
        if (this.lastModifiedDate == null) {
            this.lastModifiedDate = System.currentTimeMillis();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }
}