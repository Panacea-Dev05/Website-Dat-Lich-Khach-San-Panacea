package panacea.website_dat_lich_khach_san.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "STAFF")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_nhan_vien", length = 20, nullable = false, unique = true)
    private String maNhanVien;

    @Column(name = "ho_ten", length = 100, nullable = false)
    private String hoTen;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "chuc_vu", length = 50, nullable = false)
    private String chucVu;

    @Column(name = "khach_san_id")
    private Integer khachSanId;

    @Enumerated(EnumType.STRING)
    @Column(name = "quyen_han", length = 50)
    private QuyenHan quyenHan = QuyenHan.Staff;

    @Column(name = "mat_khau_hash", length = 255)
    private String matKhauHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    private TrangThaiStaff trangThai = TrangThaiStaff.HOAT_DONG;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_san_id", insertable = false, updatable = false)
    private Hotel hotel;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MaintenanceSchedule> maintenanceSchedules;

    @OneToMany(mappedBy = "staff", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HousekeepingLog> housekeepingLogs;

    // Enum cho Quyền hạn
    public enum QuyenHan {
        Admin, Manager, Staff, Viewer
    }

    // Enum cho Trạng thái
    public enum TrangThaiStaff {
        HOAT_DONG("Hoạt động"),
        NGHI_VIEC("Nghỉ việc");

        private final String value;

        TrangThaiStaff(String value) {
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
        if (this.quyenHan == null) {
            this.quyenHan = QuyenHan.Staff;
        }
        if (this.trangThai == null) {
            this.trangThai = TrangThaiStaff.HOAT_DONG;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }
}
