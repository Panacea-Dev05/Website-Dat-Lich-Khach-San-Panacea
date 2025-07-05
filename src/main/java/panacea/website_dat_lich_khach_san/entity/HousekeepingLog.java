package panacea.website_dat_lich_khach_san.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "HOUSEKEEPING_LOG")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HousekeepingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "phong_id", nullable = false)
    private Integer phongId;

    @Column(name = "nhan_vien_id", nullable = false)
    private Integer nhanVienId;

    @Column(name = "ngay_don_phong")
    private LocalDate ngayDonPhong;

    @Column(name = "thoi_gian_bat_dau")
    private LocalDateTime thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc")
    private LocalDateTime thoiGianKetThuc;

    @Column(name = "trang_thai_truoc", length = 20)
    private String trangThaiTruoc;

    @Column(name = "trang_thai_sau", length = 20)
    private String trangThaiSau;

    @Column(name = "van_de_phat_hien", length = 500)
    private String vanDePhatHien;

    @Column(name = "danh_sach_vat_pham", columnDefinition = "NVARCHAR(MAX)")
    private String danhSachVatPham; // JSON array

    @Column(name = "chat_luong_don_phong")
    private Byte chatLuongDonPhong;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "hinh_anh_truoc", length = 500)
    private String hinhAnhTruoc; // URLs

    @Column(name = "hinh_anh_sau", length = 500)
    private String hinhAnhSau; // URLs

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", insertable = false, updatable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhan_vien_id", insertable = false, updatable = false)
    private Staff staff;

    @PrePersist
    public void prePersist() {
        if (this.uuidId == null) {
            this.uuidId = UUID.randomUUID();
        }
        if (this.createdDate == null) {
            this.createdDate = System.currentTimeMillis();
        }
        if (this.ngayDonPhong == null) {
            this.ngayDonPhong = LocalDate.now();
        }
    }
}