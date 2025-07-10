package panacea.website_dat_lich_khach_san.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import panacea.website_dat_lich_khach_san.infrastructure.Enums.LoaiGiaoDichConverter;

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

    @Convert(converter = LoaiGiaoDichConverter.class)
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

    public InventoryTransaction(Integer id, Integer vatPhamId, LoaiGiaoDich loaiGiaoDich, Short soLuong, BigDecimal giaTri, String lyDo, Integer nhanVienId, Integer phongId, LocalDateTime ngayGiaoDich, String soChungTu, UUID uuidId, Object o, Object o1, Object o2) {
    }

    // Enums
    public enum LoaiGiaoDich {
        NHAP("Nhập"),
        XUAT("Xuất"),
        KIEM_KE("Kiểm kê"),
        HONG("Hỏng");

        private final String label;

        LoaiGiaoDich(String label) {
            this.label = label;
        }

        @JsonValue
        public String getLabel() {
            return label;
        }

        @JsonCreator
        public static LoaiGiaoDich fromLabel(String input) {
            for (LoaiGiaoDich type : LoaiGiaoDich.values()) {
                if (type.label.equalsIgnoreCase(input) || type.name().equalsIgnoreCase(input)) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Không tìm thấy loại giao dịch: " + input);
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