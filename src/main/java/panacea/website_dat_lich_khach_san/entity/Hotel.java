package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

// =============================================
// 1. QUẢN LÝ KHÁCH SẠN - HOTEL MANAGEMENT
// =============================================

@Entity
@Table(name = "HOTEL")
@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_khach_san", length = 20, nullable = false, unique = true)
    private String maKhachSan;

    @Column(name = "ten_khach_san", length = 100, nullable = false)
    private String tenKhachSan;

    @Column(name = "dia_chi", length = 200, nullable = false)
    private String diaChi;

    @Column(name = "thanh_pho", length = 50, nullable = false)
    private String thanhPho;

    @Column(name = "quoc_gia", length = 50, nullable = false)
    private String quocGia;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "website", length = 200)
    private String website;

    @Column(name = "so_sao")
    private Byte soSao;

    @Column(name = "mo_ta", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @Column(name = "chinh_sach_huy", columnDefinition = "NVARCHAR(MAX)")
    private String chinhSachHuy;

    @Column(name = "thoi_gian_nhan_phong")
    private LocalTime thoiGianNhanPhong;

    @Column(name = "thoi_gian_tra_phong")
    private LocalTime thoiGianTraPhong;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    private TrangThaiHotel trangThai = TrangThaiHotel.HOAT_DONG;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    // Relationships

    // Enums
    public enum TrangThaiHotel {
        HOAT_DONG("Hoạt động"),
        BAO_TRI("Bảo trì"),
        DONG_CUA("Đóng cửa");

        private final String value;

        TrangThaiHotel(String value) {
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
        if (this.trangThai == null) {
            this.trangThai = TrangThaiHotel.HOAT_DONG;
        }
        if (this.thoiGianNhanPhong == null) {
            this.thoiGianNhanPhong = LocalTime.of(14, 0);
        }
        if (this.thoiGianTraPhong == null) {
            this.thoiGianTraPhong = LocalTime.of(12, 0);
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }
}