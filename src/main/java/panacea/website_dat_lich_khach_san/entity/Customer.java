package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import panacea.website_dat_lich_khach_san.infrastructure.Enums.TrangThaiCustomerConverter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "CUSTOMER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "ma_khach_hang", length = 20, nullable = false, unique = true)
    private String maKhachHang;

    @Column(name = "ho", length = 50, nullable = false)
    private String ho;

    @Column(name = "ten", length = 50, nullable = false)
    private String ten;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh", length = 10)
    private GioiTinh gioiTinh;

    @Column(name = "quoc_tich", length = 50)
    private String quocTich;

    @Column(name = "so_cmnd_cccd", length = 20)
    private String soCmndCccd;

    @Column(name = "so_ho_chieu", length = 20)
    private String soHoChieu;

    @Column(name = "dia_chi", length = 200)
    private String diaChi;

    @Column(name = "loai_khach_hang", length = 20)
    private String loaiKhachHang;

    @Column(name = "diem_tich_luy")
    private Integer diemTichLuy = 0;

    @Column(name = "mat_khau_hash", length = 255, nullable = false)
    private String matKhauHash;

    @Convert(converter = TrangThaiCustomerConverter.class)
    @Column(name = "trang_thai", length = 20)
    private TrangThaiCustomer trangThai = TrangThaiCustomer.HOAT_DONG;

    @Column(name = "uuid_id")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    // Relationships
    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Booking> bookings;

    // Enums
    public enum GioiTinh {
        NAM("Nam"),
        NU("Nữ"),
        KHAC("Khác");

        private final String label;

        GioiTinh(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public static GioiTinh fromString(String input) {
            if (input == null) return null;
            for (GioiTinh gt : GioiTinh.values()) {
                if (gt.label.equalsIgnoreCase(input)) {
                    return gt;
                }
            }
            throw new IllegalArgumentException("Giá trị không hợp lệ cho GioiTinh: " + input);
        }
    }

    public enum TrangThaiCustomer {
        HOAT_DONG("Hoạt động"),
        TAM_KHOA("Tạm khóa"),
        DA_XOA("Đã xóa");

        private final String value;

        TrangThaiCustomer(String value) {
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
        if (this.diemTichLuy == null) {
            this.diemTichLuy = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.lastModifiedDate = System.currentTimeMillis();
    }
}