package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "HOTEL_AMENITIES")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HotelAmenities {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "khach_san_id", nullable = false)
    private Integer khachSanId;

    @Column(name = "ten_tien_ich", length = 100, nullable = false)
    private String tenTienIch;

    @Column(name = "loai_tien_ich", length = 50)
    private String loaiTienIch;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "gio_mo")
    private LocalTime gioMo;

    @Column(name = "gio_dong")
    private LocalTime gioDong;

    @Column(name = "phi_su_dung", precision = 10, scale = 2)
    private BigDecimal phiSuDung = BigDecimal.ZERO;

    @Column(name = "yeu_cau_dat_truoc")
    private Boolean yeuCauDatTruoc = false;

    @Column(name = "trang_thai", length = 20)
    private String trangThai = "Hoạt động";

    @Column(name = "icon", length = 50)
    private String icon;

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
        if (this.lastModifiedDate == null) {
            this.lastModifiedDate = System.currentTimeMillis();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }
}