package panacea.website_dat_lich_khach_san.core.NhanVien.DTO;

import panacea.website_dat_lich_khach_san.infrastructure.Enums.TrangThaiYeuCau;

import java.time.LocalDateTime;
import java.util.UUID;

public class SupplyRequestDTO {
    private Integer id;
    private Integer vatPhamId;
    private String tenVatPham;
    private Integer soLuongTon;
    private Short soLuongYeuCau;
    private String lyDoYeuCau;
    private Integer nhanVienYeuCau;
    private String tenNhanVienYeuCau;
    private TrangThaiYeuCau trangThai;
    private Integer adminPheDuyet;
    private String tenAdminPheDuyet;
    private LocalDateTime ngayYeuCau;
    private LocalDateTime ngayPheDuyet;
    private String ghiChuAdmin;
    private String mucDoUuTien;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;

    // Constructors
    public SupplyRequestDTO() {}

    public SupplyRequestDTO(Integer id, Integer vatPhamId, String tenVatPham, Integer soLuongTon,
                           Short soLuongYeuCau, String lyDoYeuCau, Integer nhanVienYeuCau,
                           String tenNhanVienYeuCau, TrangThaiYeuCau trangThai, Integer adminPheDuyet,
                           String tenAdminPheDuyet, LocalDateTime ngayYeuCau, LocalDateTime ngayPheDuyet,
                           String ghiChuAdmin, String mucDoUuTien, UUID uuidId, Long createdDate,
                           Long lastModifiedDate) {
        this.id = id;
        this.vatPhamId = vatPhamId;
        this.tenVatPham = tenVatPham;
        this.soLuongTon = soLuongTon;
        this.soLuongYeuCau = soLuongYeuCau;
        this.lyDoYeuCau = lyDoYeuCau;
        this.nhanVienYeuCau = nhanVienYeuCau;
        this.tenNhanVienYeuCau = tenNhanVienYeuCau;
        this.trangThai = trangThai;
        this.adminPheDuyet = adminPheDuyet;
        this.tenAdminPheDuyet = tenAdminPheDuyet;
        this.ngayYeuCau = ngayYeuCau;
        this.ngayPheDuyet = ngayPheDuyet;
        this.ghiChuAdmin = ghiChuAdmin;
        this.mucDoUuTien = mucDoUuTien;
        this.uuidId = uuidId;
        this.createdDate = createdDate;
        this.lastModifiedDate = lastModifiedDate;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVatPhamId() {
        return vatPhamId;
    }

    public void setVatPhamId(Integer vatPhamId) {
        this.vatPhamId = vatPhamId;
    }

    public String getTenVatPham() {
        return tenVatPham;
    }

    public void setTenVatPham(String tenVatPham) {
        this.tenVatPham = tenVatPham;
    }

    public Integer getSoLuongTon() {
        return soLuongTon;
    }

    public void setSoLuongTon(Integer soLuongTon) {
        this.soLuongTon = soLuongTon;
    }

    public Short getSoLuongYeuCau() {
        return soLuongYeuCau;
    }

    public void setSoLuongYeuCau(Short soLuongYeuCau) {
        this.soLuongYeuCau = soLuongYeuCau;
    }

    public String getLyDoYeuCau() {
        return lyDoYeuCau;
    }

    public void setLyDoYeuCau(String lyDoYeuCau) {
        this.lyDoYeuCau = lyDoYeuCau;
    }

    public Integer getNhanVienYeuCau() {
        return nhanVienYeuCau;
    }

    public void setNhanVienYeuCau(Integer nhanVienYeuCau) {
        this.nhanVienYeuCau = nhanVienYeuCau;
    }

    public String getTenNhanVienYeuCau() {
        return tenNhanVienYeuCau;
    }

    public void setTenNhanVienYeuCau(String tenNhanVienYeuCau) {
        this.tenNhanVienYeuCau = tenNhanVienYeuCau;
    }

    public TrangThaiYeuCau getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(TrangThaiYeuCau trangThai) {
        this.trangThai = trangThai;
    }

    public Integer getAdminPheDuyet() {
        return adminPheDuyet;
    }

    public void setAdminPheDuyet(Integer adminPheDuyet) {
        this.adminPheDuyet = adminPheDuyet;
    }

    public String getTenAdminPheDuyet() {
        return tenAdminPheDuyet;
    }

    public void setTenAdminPheDuyet(String tenAdminPheDuyet) {
        this.tenAdminPheDuyet = tenAdminPheDuyet;
    }

    public LocalDateTime getNgayYeuCau() {
        return ngayYeuCau;
    }

    public void setNgayYeuCau(LocalDateTime ngayYeuCau) {
        this.ngayYeuCau = ngayYeuCau;
    }

    public LocalDateTime getNgayPheDuyet() {
        return ngayPheDuyet;
    }

    public void setNgayPheDuyet(LocalDateTime ngayPheDuyet) {
        this.ngayPheDuyet = ngayPheDuyet;
    }

    public String getGhiChuAdmin() {
        return ghiChuAdmin;
    }

    public void setGhiChuAdmin(String ghiChuAdmin) {
        this.ghiChuAdmin = ghiChuAdmin;
    }

    public String getMucDoUuTien() {
        return mucDoUuTien;
    }

    public void setMucDoUuTien(String mucDoUuTien) {
        this.mucDoUuTien = mucDoUuTien;
    }

    public UUID getUuidId() {
        return uuidId;
    }

    public void setUuidId(UUID uuidId) {
        this.uuidId = uuidId;
    }

    public Long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Long createdDate) {
        this.createdDate = createdDate;
    }

    public Long getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Long lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}