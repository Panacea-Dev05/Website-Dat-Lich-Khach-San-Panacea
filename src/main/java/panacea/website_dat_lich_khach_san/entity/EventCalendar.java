package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "EVENT_CALENDAR")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EventCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "khach_san_id", nullable = false)
    private Integer khachSanId;

    @Column(name = "ten_su_kien", length = 200, nullable = false)
    private String tenSuKien;

    @Column(name = "loai_su_kien", length = 100)
    private String loaiSuKien;

    @Column(name = "mo_ta", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDateTime ngayBatDau;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDateTime ngayKetThuc;

    @Column(name = "tac_dong_gia")
    private Boolean tacDongGia = false;

    @Column(name = "he_so_tac_dong", precision = 5, scale = 2)
    private BigDecimal heSoTacDong = BigDecimal.ONE;

    @Column(name = "trang_thai", length = 20)
    private String trangThai = "Hoạt động";

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_san_id", insertable = false, updatable = false)
    @JsonIgnore
    private Hotel hotel;

    // Enums
    public enum TrangThaiSuKien {
        HOAT_DONG("Hoạt động"),
        TAM_DUNG("Tạm dừng"),
        DA_KET_THUC("Đã kết thúc");

        private final String label;

        TrangThaiSuKien(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static TrangThaiSuKien fromString(String text) {
            for (TrangThaiSuKien status : TrangThaiSuKien.values()) {
                if (status.label.equalsIgnoreCase(text)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("No constant with text " + text + " found");
        }
    }

    public enum LoaiSuKien {
        LE_HOI("Lễ hội"),
        HOI_NGHI("Hội nghị"),
        GIAI_TRI("Giải trí"),
        THE_THAO("Thể thao"),
        VAN_HOA("Văn hóa"),
        KINH_DOANH("Kinh doanh"),
        KHAC("Khác");

        private final String label;

        LoaiSuKien(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static LoaiSuKien fromString(String text) {
            for (LoaiSuKien type : LoaiSuKien.values()) {
                if (type.label.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("No constant with text " + text + " found");
        }
    }

    // Helper methods
    public boolean isActive() {
        return "Hoạt động".equals(this.trangThai);
    }

    public boolean isCurrentEvent() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(ngayBatDau) && now.isBefore(ngayKetThuc) && isActive();
    }

    public boolean isUpcomingEvent() {
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(ngayBatDau) && isActive();
    }

    public boolean isPastEvent() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(ngayKetThuc);
    }

    @PrePersist
    protected void onCreate() {
        if (uuidId == null) {
            uuidId = UUID.randomUUID();
        }
        long currentTime = System.currentTimeMillis();
        if (createdDate == null) {
            createdDate = currentTime;
        }
        lastModifiedDate = currentTime;
    }

    @PreUpdate
    protected void onUpdate() {
        lastModifiedDate = System.currentTimeMillis();
    }
}