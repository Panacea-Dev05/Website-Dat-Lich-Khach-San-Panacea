package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import panacea.website_dat_lich_khach_san.entity.CancellationPolicy;

@Entity
@Table(name = "BOOKING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_dat_phong", length = 20, nullable = false, unique = true)
    private String maDatPhong;

    @ManyToOne
    @JoinColumn(name = "khach_hang_id", nullable = false)
    private Customer khachHang;

    @ManyToOne
    @JoinColumn(name = "promotion_id")
    private Promotion promotion;

    @ManyToOne
    @JoinColumn(name = "loai_phong_id")
    private RoomType roomType;

    @Column(name = "ngay_nhan_phong", nullable = false)
    private LocalDate ngayNhanPhong;

    @Column(name = "ngay_tra_phong", nullable = false)
    private LocalDate ngayTraPhong;

    @Column(name = "so_nguoi_lon")
    private Byte soNguoiLon;

    @Column(name = "so_tre_em")
    private Byte soTreEm = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "booking_type", length = 20)
    private BookingType bookingType = BookingType.DAY;

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
    private TrangThaiDatPhong trangThaiDatPhong = TrangThaiDatPhong.CHO_XAC_NHAN;

    @Column(name = "trang_thai_thanh_toan", length = 20)
    private TrangThaiThanhToan trangThaiThanhToan = TrangThaiThanhToan.CHUA_THANH_TOAN;

    @Column(name = "ghi_chu_khach_hang", length = 500)
    private String ghiChuKhachHang;

    @Column(name = "yeu_cau_dac_biet", length = 1000)
    private String yeuCauDacBiet;

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

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "so_cmnd_cccd_checkin", length = 20)
    private String soCmndCccdCheckIn;

    @Column(name = "ngay_cap_cmnd_checkin")
    private LocalDate ngayCapCmndCheckIn;

    @Column(name = "noi_cap_cmnd_checkin", length = 100)
    private String noiCapCmndCheckIn;

    @Column(name = "so_nguoi_lon_thuc_te")
    private Byte soNguoiLonThucTe;

    @Column(name = "so_tre_em_thuc_te")
    private Byte soTreEmThucTe;

    @Column(name = "ghi_chu_checkin", length = 500)
    private String ghiChuCheckIn;

    @Enumerated(EnumType.STRING)
    @Column(name = "cancellation_policy", length = 20)
    private CancellationPolicy cancellationPolicy = CancellationPolicy.MODERATE;

    @Column(name = "refund_amount", precision = 15, scale = 2)
    private BigDecimal refundAmount = BigDecimal.ZERO;

    @Column(name = "cancellation_fee", precision = 15, scale = 2)
    private BigDecimal cancellationFee = BigDecimal.ZERO;

    // Relationships
    @JsonIgnore
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<BookingDetail> bookingDetails;

    @JsonIgnore
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Payment> payments;

    @JsonIgnore
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ServiceDetail> serviceDetails;

    // Enums
    public enum TrangThaiDatPhong {
        CHO_XAC_NHAN("Chờ xác nhận"),
        DA_XAC_NHAN("Đã xác nhận"),
        DA_NHAN_PHONG("Đã nhận phòng"),
        DA_HOAN_THANH("Đã hoàn thành"),
        DA_HUY("Đã hủy");

        private final String label;
        TrangThaiDatPhong(String label) {
            this.label = label;
        }
        public String getLabel() {
            return label;
        }
        public static TrangThaiDatPhong fromString(String input) {
            if (input == null) return null;
            for (TrangThaiDatPhong ttdp : TrangThaiDatPhong.values()) {
                if (ttdp.label.equalsIgnoreCase(input)) {
                    return ttdp;
                }
            }
            throw new IllegalArgumentException("Giá trị không hợp lệ cho TrangThaiDatPhong: " + input);
        }
    }

    public enum TrangThaiThanhToan {
        CHUA_THANH_TOAN("Chưa thanh toán"),
        DA_COC("Đã cọc"),
        DA_THANH_TOAN("Đã thanh toán"),
        HOAN_TIEN("Hoàn tiền");

        private final String label;
        TrangThaiThanhToan(String label) {
            this.label = label;
        }
        public String getLabel() {
            return label;
        }
        public static TrangThaiThanhToan fromString(String input) {
            if (input == null) return null;
            for (TrangThaiThanhToan t : values()) {
                if (t.label.equalsIgnoreCase(input)) return t;
            }
            throw new IllegalArgumentException("Giá trị không hợp lệ cho TrangThaiThanhToan: " + input);
        }
    }

    public CancellationPolicy getCancellationPolicy() {
        return cancellationPolicy;
    }

    public void setCancellationPolicy(CancellationPolicy cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public BigDecimal getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(BigDecimal cancellationFee) {
        this.cancellationFee = cancellationFee;
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
        if (this.ngayDat == null) {
            this.ngayDat = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }

    // Enum cho loại đặt phòng
    public enum BookingType {
        DAY("Theo ngày"),
        HOUR("Theo giờ"),
        OVERNIGHT("Qua đêm");

        private final String label;
        BookingType(String label) {
            this.label = label;
        }
        public String getLabel() {
            return label;
        }
        public static BookingType fromString(String input) {
            if (input == null) return null;
            for (BookingType bt : BookingType.values()) {
                if (bt.name().equalsIgnoreCase(input) || bt.label.equalsIgnoreCase(input)) {
                    return bt;
                }
            }
            throw new IllegalArgumentException("Giá trị không hợp lệ cho BookingType: " + input);
        }
    }
}
