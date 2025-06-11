package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDate;
import java.util.UUID;

@Builder
@Entity
@Table(name = "CUSTOMER_LOYALTY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomesLoyalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_hang_id", nullable = false)
    private Customer khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chuong_trinh_id", nullable = false)
    private LoyaltyProgram chuongTrinh;

    @Column(name = "ngay_tham_gia")
    @Builder.Default
    private LocalDate ngayThamGia = LocalDate.now();

    @Column(name = "diem_hien_tai")
    @Builder.Default
    private Integer diemHienTai = 0;

    @Column(name = "diem_da_su_dung")
    @Builder.Default
    private Integer diemDaSuDung = 0;

    @Column(name = "cap_do_hien_tai", length = 20)
    private String capDoHienTai;

    @Column(name = "ngay_len_cap")
    private LocalDate ngayLenCap;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    @Builder.Default
    private Status trangThai = Status.HOAT_DONG;

    @Column(name = "uuid_id")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    public enum Status {
        HOAT_DONG("Hoạt động"),
        TAM_NGUNG("Tạm ngưng");

        private final String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @PrePersist
    protected void onCreate() {
        if (uuidId == null) {
            uuidId = UUID.randomUUID();
        }
        createdDate = System.currentTimeMillis();
        lastModifiedDate = System.currentTimeMillis();
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = System.currentTimeMillis();
    }
}
