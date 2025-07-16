package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "LOYALTY_PROGRAM")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoyaltyProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_chuong_trinh", nullable = false, length = 100)
    private String tenChuongTrinh;

    @Enumerated(EnumType.STRING)
    @Column(name = "cap_do", length = 20)
    private LoyaltyLevel capDo;

    @Column(name = "diem_toi_thieu")
    private Integer diemToiThieu;

    @Column(name = "ti_le_tich_diem", precision = 3, scale = 2)
    private BigDecimal tiLeTichDiem;

    @Column(name = "uu_dai", columnDefinition = "NVARCHAR(MAX)")
    private String uuDai; // JSON format

    @Column(name = "mau_sac_the", length = 7)
    private String mauSacThe; // Hex color code

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", length = 20)
    @Builder.Default
    private Status trangThai = Status.HOAT_DONG;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;


    public enum LoyaltyLevel {
        BRONZE("Bronze"),
        SILVER("Silver"),
        GOLD("Gold"),
        PLATINUM("Platinum");

        private final String displayName;

        LoyaltyLevel(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

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
