package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PROMOTION")

public class Promotion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_khuyen_mai", unique = true, nullable = false, length = 20)
    private String maKhuyenMai;

    @Column(name = "ten_khuyen_mai", nullable = false, length = 100)
    private String tenKhuyenMai;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "loai_giam_gia", length = 20)
    private String loaiGiamGia;

    @Column(name = "gia_tri_giam", precision = 12, scale = 2)
    private BigDecimal giaTriGiam;

    @Column(name = "giam_toi_da", precision = 12, scale = 2)
    private BigDecimal giamToiDa;

    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "so_luong_toi_da")
    private Integer soLuongToiDa;

    @Column(name = "da_su_dung")
    private Integer daSuDung = 0;

    @Column(name = "dieu_kien_ap_dung", length = 500)
    private String dieuKienApDung;

    @Column(name = "trang_thai", length = 20)
    private String trangThai = "Hoạt động";

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
    }

    @PreUpdate
    public void onUpdate() {
        lastModifiedDate = Instant.now().toEpochMilli();
    }
}
