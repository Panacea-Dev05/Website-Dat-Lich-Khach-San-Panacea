package panacea.website_dat_lich_khach_san.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "INVENTORY_MANAGEMENT")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryManagement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "khach_san_id", nullable = false)
    private Integer khachSanId;

    @Column(name = "ten_vat_pham", length = 100, nullable = false)
    private String tenVatPham;

    @Column(name = "ma_vat_pham", length = 20, unique = true)
    private String maVatPham;

    @Column(name = "loai_vat_pham", length = 50)
    private String loaiVatPham; // 'Amenities', 'Cleaning', 'Maintenance', 'Food'

    @Column(name = "don_vi_tinh", length = 20)
    private String donViTinh;

    @Column(name = "so_luong_ton")
    private Short soLuongTon = 0;

    @Column(name = "so_luong_toi_thieu")
    private Short soLuongToiThieu = 0;

    @Column(name = "gia_nhap", precision = 10, scale = 2)
    private BigDecimal giaNhap;

    @Column(name = "nha_cung_cap", length = 100)
    private String nhaCungCap;

    @Column(name = "ngay_nhap_cuoi")
    private LocalDate ngayNhapCuoi;

    @Column(name = "han_su_dung")
    private LocalDate hanSuDung;

    @Column(name = "vi_tri_kho", length = 50)
    private String viTriKho;

    @Column(name = "trang_thai", length = 20)
    private String trangThai = "Hoạt động";

    @Column(name = "uuid_id", columnDefinition = "uniqueidentifier")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khach_san_id", insertable = false, updatable = false)
    private Hotel hotel;

    @OneToMany(mappedBy = "inventoryItem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<InventoryTransaction> transactions;

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
        if (this.soLuongTon == null) {
            this.soLuongTon = 0;
        }
        if (this.soLuongToiThieu == null) {
            this.soLuongToiThieu = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }
}
