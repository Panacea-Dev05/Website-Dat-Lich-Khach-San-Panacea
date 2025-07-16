package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "ROOM_PRICING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomPricing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loai_phong_id", nullable = false)
    private RoomType roomType;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_gia", length = 20)
    private LoaiGia loaiGia;

    @Column(name = "gia_tri", precision = 12, scale = 2)
    private BigDecimal giaTri;

    @Column(name = "gia_gio", precision = 12, scale = 2)
    private BigDecimal giaGio;

    @Column(name = "gia_ngay", precision = 12, scale = 2)
    private BigDecimal giaNgay;

    @Column(name = "gia_qua_dem", precision = 12, scale = 2)
    private BigDecimal giaQuaDem;

    // --- Thêm cột giá phụ thu quá giờ ---
    @Column(name = "gia_phu_thu_qua_gio", precision = 12, scale = 2)
    private BigDecimal giaPhuThuQuaGio;
    // --- Kết thúc phần thêm mới ---

    @Column(name = "ngay_bat_dau", nullable = false)
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc", nullable = false)
    private LocalDate ngayKetThuc;

    @Column(name = "ap_dung_cho", length = 100)
    private String apDungCho = "All";

    @Column(name = "he_so_dieu_chinh", precision = 3, scale = 2)
    private BigDecimal heSoDieuChinh = BigDecimal.ONE;

    @Column(name = "trang_thai", length = 20)
    private String trangThai = "Hoạt động";

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    // Enums và các phương thức @PrePersist, @PreUpdate không thay đổi
    public enum LoaiGia {
        BASE("Giá cơ bản"),
        WEEKEND("Cuối tuần"),
        HOLIDAY("Ngày lễ"),
        PEAK_SEASON("Mùa cao điểm");

        private final String value;

        LoaiGia(String value) {
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
        if (this.heSoDieuChinh == null) {
            this.heSoDieuChinh = BigDecimal.ONE;
        }
        if (this.giaGio == null) {
            this.giaGio = BigDecimal.ZERO;
        }
        if (this.giaNgay == null) {
            this.giaNgay = BigDecimal.ZERO;
        }
        if (this.giaQuaDem == null) {
            this.giaQuaDem = BigDecimal.ZERO;
        }
        // Thêm giá trị mặc định cho cột mới
        if (this.giaPhuThuQuaGio == null) {
            this.giaPhuThuQuaGio = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }
}