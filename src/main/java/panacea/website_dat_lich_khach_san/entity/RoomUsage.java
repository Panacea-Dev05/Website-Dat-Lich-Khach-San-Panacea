package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ROOM_USAGE")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoomUsage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "so_phong", length = 10, nullable = false)
    private String soPhong;

    @Column(name = "ten_vat_pham", length = 100, nullable = false)
    private String tenVatPham;

    @Column(name = "ma_vat_pham", length = 20)
    private String maVatPham;

    @Column(name = "so_luong_su_dung")
    private Integer soLuongSuDung = 1;

    @Column(name = "don_vi_tinh", length = 20)
    private String donViTinh;

    @Column(name = "ngay_su_dung", nullable = false)
    private LocalDateTime ngaySuDung;

    @Column(name = "loai_su_dung", length = 50)
    private String loaiSuDung; // 'Amenities', 'Cleaning', 'Maintenance', 'Food'

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "nhan_vien_ghi_nhan", length = 100)
    private String nhanVienGhiNhan;

    @Column(name = "trang_thai", length = 20)
    private String trangThai = "Hoạt động";

    // Thông tin booking
    @Column(name = "ma_dat_phong", length = 20)
    private String maDatPhong;

    @Column(name = "ten_khach_hang", length = 100)
    private String tenKhachHang;

    @Column(name = "so_dien_thoai", length = 15)
    private String soDienThoai;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    // Relationships
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_id")
    private Room room;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vat_pham_id")
    private InventoryManagement inventoryItem;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

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
        if (this.ngaySuDung == null) {
            this.ngaySuDung = LocalDateTime.now();
        }
        if (this.trangThai == null) {
            this.trangThai = "Hoạt động";
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }
    
    // Getter/Setter cho bookingId thông qua relationship
    public Integer getBookingId() {
        return booking != null ? booking.getId() : null;
    }
    
    public void setBookingId(Integer bookingId) {
        // Chỉ set ID, không tạo object mới
        // Object booking sẽ được set thông qua relationship
    }
}
