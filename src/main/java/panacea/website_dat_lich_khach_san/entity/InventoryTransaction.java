package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "INVENTORY_TRANSACTION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventoryTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "vat_pham_id", nullable = false)
    private Integer vatPhamId;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai_giao_dich", length = 20)
    private LoaiGiaoDich loaiGiaoDich;

    @Column(name = "so_luong", nullable = false)
    private Short soLuong;

    @Column(name = "gia_tri", precision = 10, scale = 2)
    private BigDecimal giaTri;

    @Column(name = "ly_do", length = 200)
    private String lyDo;

    @Column(name = "nhan_vien_id")
    private Integer nhanVienId;

    @Column(name = "phong_id")
    private Integer phongId; // Nếu xuất cho phòng cụ thể

    @Column(name = "ngay_giao_dich")
    private LocalDateTime ngayGiaoDich;

    @Column(name = "so_chung_tu", length = 50)
    private String soChungTu;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vat_pham_id", insertable = false, updatable = false)
    private InventoryManagement inventoryItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhan_vien_id", insertable = false, updatable = false)
    private Staff staff;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id", insertable = false, updatable = false)
    private Room room;

    // Enums
    public enum LoaiGiaoDich {
        NHAP("Nhập"), XUAT("Xuất"), KIEM_KE("Kiểm kê"), HONG("Hỏng");

        private final String value;

        LoaiGiaoDich(String value) {
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
        if (this.ngayGiaoDich == null) {
            this.ngayGiaoDich = LocalDateTime.now();
        }
    }
}