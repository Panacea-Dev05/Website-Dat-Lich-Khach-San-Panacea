package panacea.website_dat_lich_khach_san.infrastructure.Enums;

public enum LoaiKhachHang {
    CA_NHAN("Cá nhân"),
    DOANH_NGHIEP("Doanh nghiệp");

    private final String dbValue;
    LoaiKhachHang(String dbValue) { this.dbValue = dbValue; }
    public String getDbValue() { return dbValue; }
} 