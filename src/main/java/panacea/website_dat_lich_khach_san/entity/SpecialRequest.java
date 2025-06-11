package panacea.website_dat_lich_khach_san.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "SPECIAL_REQUEST")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpecialRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "dat_phong_id", nullable = false)
    private Integer datPhongId;

    @Column(name = "loai_yeu_cau", length = 50)
    private String loaiYeuCau; // 'Room_Setup', 'Dietary', 'Accessibility', 'Service'

    @Column(name = "noi_dung_yeu_cau", length = 500)
    private String noiDungYeuCau;

    @Column(name = "muc_do_uu_tien", length = 20)
    private String mucDoUuTien = "Medium";

    @Column(name = "trang_thai", length = 20)
    private String trangThai = "Pending";

    @Column(name = "nhan_vien_xu_ly")
    private Integer nhanVienXuLy;

    @Column(name = "thoi_gian_xu_ly")
    private LocalDateTime thoiGianXuLy;

    @Column(name = "ghi_chu_xu_ly", length = 500)
    private String ghiChuXuLy;

    @Column(name = "phi_phu_thu", precision = 10, scale = 2)
    private BigDecimal phiPhuThu = BigDecimal.ZERO;

    @Column(name = "uuid_id", columnDefinition = "uniqueidentifier")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dat_phong_id", insertable = false, updatable = false)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhan_vien_xu_ly", insertable = false, updatable = false)
    private Staff staff;

    @PrePersist
    public void prePersist() {
        if (this.uuidId == null) {
            this.uuidId = UUID.randomUUID();
        }
        if (this.createdDate == null) {
            this.createdDate = System.currentTimeMillis();
        }
        if (this.phiPhuThu == null) {
            this.phiPhuThu = BigDecimal.ZERO;
        }
    }
}