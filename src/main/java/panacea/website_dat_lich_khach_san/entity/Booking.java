package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "BOOKING")

public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_dat_phong", unique = true, nullable = false, length = 20)
    private String maDatPhong;

    @ManyToOne
    @JoinColumn(name = "khach_hang_id", nullable = false)
    private Customer khachHang;

//      DO phần tui ko có cái entity Hotel này
//    @ManyToOne
//    @JoinColumn(name = "khach_san_id", nullable = false)
//    private Hotel khachSan;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @Column(name = "ngay_nhan_phong")
    private LocalDate ngayNhanPhong;

    @Column(name = "ngay_tra_phong")
    private LocalDate ngayTraPhong;

    @Column(name = "so_nguoi_lon")
    private Byte soNguoiLon;

    @Column(name = "so_tre_em")
    private Byte soTreEm = 0;

    @Column(name = "tong_tien_phong", precision = 15, scale = 2)
    private BigDecimal tongTienPhong;

    @Column(name = "tong_tien_dich_vu", precision = 15, scale = 2)
    private BigDecimal tongTienDichVu = BigDecimal.ZERO;

    @Column(name = "giam_gia_promotion", precision = 15, scale = 2)
    private BigDecimal giamGiaPromotion = BigDecimal.ZERO;

    @Column(name = "phi_thue", precision = 15, scale = 2)
    private BigDecimal phiThue = BigDecimal.ZERO;

    @Column(name = "tong_thanh_toan", precision = 15, scale = 2)
    private BigDecimal tongThanhToan;

    @Column(name = "tien_dat_coc", precision = 15, scale = 2)
    private BigDecimal tienDatCoc = BigDecimal.ZERO;

    @Column(name = "trang_thai_dat_phong", length = 20)
    private String trangThaiDatPhong = "Chờ xác nhận";

    @Column(name = "trang_thai_thanh_toan", length = 20)
    private String trangThaiThanhToan = "Chưa thanh toán";

    @Column(name = "ghi_chu_khach_hang", length = 500)
    private String ghiChuKhachHang;

    @Column(name = "ghi_chu_noi_bo", length = 500)
    private String ghiChuNoiBo;

    @Column(name = "ngay_dat")
    private LocalDateTime ngayDat;

    @Column(name = "ngay_xac_nhan")
    private LocalDateTime ngayXacNhan;

    @Column(name = "ngay_huy")
    private LocalDateTime ngayHuy;

    @Column(name = "ly_do_huy", length = 500)
    private String lyDoHuy;

    @Column(name = "uuid_id", columnDefinition = "uniqueidentifier")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    @PrePersist
    public void onCreate() {
        long now = Instant.now().toEpochMilli();
        createdDate = now;
        lastModifiedDate = now;
        uuidId = UUID.randomUUID();
        ngayDat = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        lastModifiedDate = Instant.now().toEpochMilli();
    }
}
