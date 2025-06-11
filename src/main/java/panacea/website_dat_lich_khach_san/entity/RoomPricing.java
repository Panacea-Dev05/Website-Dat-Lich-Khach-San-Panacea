package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "ROOM_PRICING")
@Getter
@Setter
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
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDate ngayKetThuc;

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

    // Relationships (commented out since RoomType entity is not available)
    // @ManyToOne(fetch = FetchType.LAZY)
    // @JoinColumn(name = "loai_phong_id", insertable = false, updatable = false)
    // private RoomType roomType;

    // Enums
    public enum LoaiGia {
        BASE("Giá cơ bản"),
        WEEKEND("Cuối tuần"),
        HOLIDAY("Ngày lễ"),
        PEAK_SEASON("Mùa cao điểm");

        private final String value;

        LoaiGia(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
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
        if (this.heSoDieuChinh == null) {
            this.heSoDieuChinh = BigDecimal.ONE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }
}