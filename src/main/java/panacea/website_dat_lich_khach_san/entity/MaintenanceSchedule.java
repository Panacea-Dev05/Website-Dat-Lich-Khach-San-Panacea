package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "MAINTENANCE_SCHEDULE")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaintenanceSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "phong_id", nullable = false)
    private Integer phongId;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_bao_tri", length = 50)
    private LoaiBaoTri loaiBaoTri;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDateTime ngayKetThuc;

    @Column(name = "nhan_vien_phu_trach")
    private Integer nhanVienPhuTrach;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    private TrangThaiMaintenance trangThai = TrangThaiMaintenance.SCHEDULED;

    @Column(name = "chi_phi", precision = 12, scale = 2)
    private BigDecimal chiPhi = BigDecimal.ZERO;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "muc_do_uu_tien", length = 20)
    private String mucDoUuTien = "Medium";

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", insertable = false, updatable = false)
    private Room room;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhan_vien_phu_trach", insertable = false, updatable = false)
    private Staff staff;

    // Enums
    public enum LoaiBaoTri {
        Routine, Repair, Upgrade, Deep_Clean
    }

    public enum TrangThaiMaintenance {
        SCHEDULED("Scheduled"),
        IN_PROGRESS("In_Progress"),
        COMPLETED("Completed"),
        CANCELLED("Cancelled");

        private final String value;

        TrangThaiMaintenance(String value) {
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
        if (this.chiPhi == null) {
            this.chiPhi = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }
}
