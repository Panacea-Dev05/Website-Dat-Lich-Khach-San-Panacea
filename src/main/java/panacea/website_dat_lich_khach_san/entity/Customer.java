package panacea.website_dat_lich_khach_san.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Check;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "CUSTOMER")

public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_khach_hang", unique = true, nullable = false, length = 20)
    private String maKhachHang;

    @Column(nullable = false, length = 50)
    private String ho;

    @Column(nullable = false, length = 50)
    private String ten;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "ngay_sinh")
    private LocalDate ngaySinh;

    @Column(name = "gioi_tinh", length = 10)
    private String gioiTinh;

    @Column(length = 50)
    private String quocTich;

    @Column(name = "so_cmnd_cccd", length = 20)
    private String soCmndCccd;

    @Column(name = "so_ho_chieu", length = 20)
    private String soHoChieu;

    @Column(name = "dia_chi", length = 200)
    private String diaChi;

    @Column(name = "loai_khach_hang", length = 20)
    private String loaiKhachHang = "Cá nhân";

    @Column(name = "diem_tich_luy")
    private Integer diemTichLuy = 0;

    @Column(name = "mat_khau_hash", nullable = false, length = 255)
    private String matKhauHash;

    @Column(name = "trang_thai", length = 20)
    private String trangThai = "Hoạt động";

    @Column(name = "uuid_id", columnDefinition = "uniqueidentifier")
    private UUID uuidId;

    @Column(name = "created_date")
    private Long createdDate;

    @Column(name = "last_modified_date")
    private Long lastModifiedDate;

    @PrePersist
    public void onCreate() {
        long now = Instant.now().toEpochMilli();
        createdDate = now;
        lastModifiedDate = now;
        uuidId = UUID.randomUUID();
    }

    @PreUpdate
    public void onUpdate() {
        lastModifiedDate = Instant.now().toEpochMilli();
    }
}
