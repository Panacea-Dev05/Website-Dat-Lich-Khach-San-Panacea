package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import panacea.website_dat_lich_khach_san.infrastructure.Enums.TrangThaiYeuCau;
import panacea.website_dat_lich_khach_san.infrastructure.Enums.TrangThaiYeuCauJpaConverter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "SUPPLY_REQUEST")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplyRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "vat_pham_id", nullable = false)
    private Integer vatPhamId;

    @Column(name = "so_luong_yeu_cau", nullable = false)
    private Short soLuongYeuCau;

    @Column(name = "ly_do_yeu_cau", length = 500)
    private String lyDoYeuCau;

    @Column(name = "nhan_vien_yeu_cau", nullable = false)
    private Integer nhanVienYeuCau;

    @Convert(converter = TrangThaiYeuCauJpaConverter.class)
    @Column(name = "trang_thai", length = 20)
    private TrangThaiYeuCau trangThai = TrangThaiYeuCau.CHO_DUYET;

    @Column(name = "admin_phe_duyet")
    private Integer adminPheDuyet;

    @Column(name = "ngay_yeu_cau")
    private LocalDateTime ngayYeuCau;

    @Column(name = "ngay_phe_duyet")
    private LocalDateTime ngayPheDuyet;

    @Column(name = "ghi_chu_admin", length = 500)
    private String ghiChuAdmin;

    @Column(name = "muc_do_uu_tien")
    private String mucDoUuTien = "Bình thường"; // "Khẩn cấp", "Cao", "Bình thường", "Thấp"

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vat_pham_id", insertable = false, updatable = false)
    private InventoryManagement inventoryItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nhan_vien_yeu_cau", insertable = false, updatable = false)
    private Staff staffRequester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_phe_duyet", insertable = false, updatable = false)
    private Staff adminApprover;

    @PrePersist
    public void prePersist() {
        if (this.uuidId == null) {
            this.uuidId = UUID.randomUUID();
        }
        if (this.createdDate == null) {
            this.createdDate = System.currentTimeMillis();
        }
        if (this.ngayYeuCau == null) {
            this.ngayYeuCau = LocalDateTime.now();
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }
}