package panacea.website_dat_lich_khach_san.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

@Entity
@Table(name = "PAYMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_thanh_toan", length = 20, nullable = false, unique = true)
    private String maThanhToan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dat_phong_id", nullable = false)
    private Booking booking;

    @Column(name = "so_tien", precision = 15, scale = 2, nullable = false)
    private BigDecimal soTien;

    @Column(name = "phuong_thuc", length = 50, nullable = false)
    private String phuongThuc;

    @Column(name = "ma_giao_dich", length = 100)
    private String maGiaoDich;

    @Convert(converter = TrangThaiPaymentConverter.class)
    @Column(name = "trang_thai", length = 20)
    private TrangThaiPayment trangThai = TrangThaiPayment.DANG_XU_LY;

    @Column(name = "ngay_thanh_toan")
    private LocalDateTime ngayThanhToan;

    @Column(name = "noi_dung", length = 200)
    private String noiDung;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    // Enums
    public enum TrangThaiPayment {
        DANG_XU_LY("Đang xử lý"),
        THANH_CONG("Thành công"),
        THAT_BAI("Thất bại"),
        HOAN_TIEN("Hoàn tiền");

        private final String value;

        TrangThaiPayment(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static TrangThaiPayment fromLabel(String label) {
            for (TrangThaiPayment ttp : TrangThaiPayment.values()) {
                if (ttp.getValue().equalsIgnoreCase(label)) {
                    return ttp;
                }
            }
            throw new IllegalArgumentException("Không hợp lệ: " + label);
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
        if (this.ngayThanhToan == null) {
            this.ngayThanhToan = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }
}