package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "BOOKING_HISTORY")
public class BookingHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ma_dat_phong", length = 50)
    private String maDatPhong;

    @Column(name = "ten_khach_hang", length = 100)
    private String tenKhachHang;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "so_dien_thoai", length = 20)
    private String soDienThoai;

    @Column(name = "ten_khach_san", length = 100)
    private String tenKhachSan;

    @Column(name = "so_phong", length = 20)
    private String soPhong;

    @Column(name = "ngay_nhan_phong")
    private LocalDate ngayNhanPhong;

    @Column(name = "ngay_tra_phong")
    private LocalDate ngayTraPhong;

    @Column(name = "so_nguoi_lon")
    private Byte soNguoiLon;

    @Column(name = "so_tre_em")
    private Byte soTreEm;

    @Column(name = "tong_thanh_toan")
    private BigDecimal tongThanhToan;

    @Column(name = "ngay_dat")
    private LocalDateTime ngayDat;

    @Column(name = "ngay_hoan_thanh")
    private LocalDateTime ngayHoanThanh;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    // ... có thể bổ sung thêm các trường khác nếu cần ...

    // Getter & Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMaDatPhong() { return maDatPhong; }
    public void setMaDatPhong(String maDatPhong) { this.maDatPhong = maDatPhong; }
    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }
    public String getTenKhachSan() { return tenKhachSan; }
    public void setTenKhachSan(String tenKhachSan) { this.tenKhachSan = tenKhachSan; }
    public String getSoPhong() { return soPhong; }
    public void setSoPhong(String soPhong) { this.soPhong = soPhong; }
    public LocalDate getNgayNhanPhong() { return ngayNhanPhong; }
    public void setNgayNhanPhong(LocalDate ngayNhanPhong) { this.ngayNhanPhong = ngayNhanPhong; }
    public LocalDate getNgayTraPhong() { return ngayTraPhong; }
    public void setNgayTraPhong(LocalDate ngayTraPhong) { this.ngayTraPhong = ngayTraPhong; }
    public Byte getSoNguoiLon() { return soNguoiLon; }
    public void setSoNguoiLon(Byte soNguoiLon) { this.soNguoiLon = soNguoiLon; }
    public Byte getSoTreEm() { return soTreEm; }
    public void setSoTreEm(Byte soTreEm) { this.soTreEm = soTreEm; }
    public BigDecimal getTongThanhToan() { return tongThanhToan; }
    public void setTongThanhToan(BigDecimal tongThanhToan) { this.tongThanhToan = tongThanhToan; }
    public LocalDateTime getNgayDat() { return ngayDat; }
    public void setNgayDat(LocalDateTime ngayDat) { this.ngayDat = ngayDat; }
    public LocalDateTime getNgayHoanThanh() { return ngayHoanThanh; }
    public void setNgayHoanThanh(LocalDateTime ngayHoanThanh) { this.ngayHoanThanh = ngayHoanThanh; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }
} 