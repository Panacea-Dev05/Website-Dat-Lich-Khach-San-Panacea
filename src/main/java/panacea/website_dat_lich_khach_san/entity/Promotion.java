package panacea.website_dat_lich_khach_san.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "PROMOTION")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_khuyen_mai", length = 20, nullable = false, unique = true)
    private String maKhuyenMai;

    @Column(name = "ten_khuyen_mai", length = 100, nullable = false)
    private String tenKhuyenMai;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giam_gia", length = 20)
    private LoaiGiamGia loaiGiamGia;

    @Column(name = "gia_tri_giam", precision = 12, scale = 2)
    private BigDecimal giaTriGiam;

    @Column(name = "giam_toi_da", precision = 12, scale = 2)
    private BigDecimal giamToiDa;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Column(name = "so_luong_toi_da")
    private Integer soLuongToiDa;

    @Column(name = "da_su_dung")
    private Integer daSuDung = 0;

    @Column(name = "dieu_kien_ap_dung", length = 500)
    private String dieuKienApDung;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    private TrangThaiPromotion trangThai = TrangThaiPromotion.HOAT_DONG;

    @Column(name = "uuid_id", columnDefinition = "uniqueidentifier")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    // Relationships
    @OneToMany(mappedBy = "promotion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    // Enums
    public enum LoaiGiamGia {
        PHAN_TRAM("Phần trăm"),
        SO_TIEN("Số tiền");

        private final String value;

        LoaiGiamGia(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public enum TrangThaiPromotion {
        HOAT_DONG("Hoạt động"),
        TAM_NGUNG("Tạm ngưng"),
        HET_HAN("Hết hạn");

        private final String value;

        TrangThaiPromotion(String value) {
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
        if (this.daSuDung == null) {
            this.daSuDung = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }
}
