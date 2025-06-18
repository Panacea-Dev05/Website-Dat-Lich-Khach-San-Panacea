package panacea.website_dat_lich_khach_san.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "REVIEW")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "dat_phong_id", nullable = false)
    private Integer datPhongId;

    @Column(name = "khach_hang_id", nullable = false)
    private Integer khachHangId;

    @Column(name = "khach_san_id", nullable = false)
    private Integer khachSanId;

    @Column(name = "diem_tong_quan")
    private Byte diemTongQuan;

    @Column(name = "diem_sach_se")
    private Byte diemSachSe;

    @Column(name = "diem_dich_vu")
    private Byte diemDichVu;

    @Column(name = "diem_vi_tri")
    private Byte diemViTri;

    @Column(name = "diem_gia_ca")
    private Byte diemGiaCa;

    @Column(name = "binh_luan", length = 1000)
    private String binhLuan;

    @Column(name = "ngay_danh_gia")
    private LocalDateTime ngayDanhGia;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    private TrangThaiReview trangThai = TrangThaiReview.CHO_DUYET;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dat_phong_id", insertable = false, updatable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_hang_id", insertable = false, updatable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_san_id", insertable = false, updatable = false)
    private Hotel hotel;

    // Enums
    public enum TrangThaiReview {
        CHO_DUYET("Chờ duyệt"), DA_DUYET("Đã duyệt"), DA_AN("Đã ẩn");

        private final String value;

        TrangThaiReview(String value) {
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
        if (this.ngayDanhGia == null) {
            this.ngayDanhGia = LocalDateTime.now();
        }
    }
}