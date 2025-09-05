package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class BookingDTO {
    private Integer id;
    private String maDatPhong;
    private Integer khachHangId;
    private Integer hotelId;
    private Integer promotionId;
    private LocalDate ngayNhanPhong;
    private LocalDate ngayTraPhong;
    private Byte soNguoiLon;
    private Byte soTreEm;
    private BigDecimal tongTienPhong;
    private BigDecimal tongTienDichVu;
    private BigDecimal giamGiaPromotion;
    private BigDecimal phiThue;
    private BigDecimal tongThanhToan;
    private BigDecimal tienDatCoc;
    private String trangThaiDatPhong;
    private String trangThaiThanhToan;
    private String ghiChuKhachHang;
    private String ghiChuNoiBo;
    private LocalDateTime ngayDat;
    private LocalDateTime ngayXacNhan;
    private LocalDateTime ngayHuy;
    private String lyDoHuy;
    private UUID uuidId;
    private Long createdDate;
    private Long lastModifiedDate;
    private CustomerDTO khachHang;
    private String roomNumber;
    private String roomTypeName;
    private String customerName;

    // Thông tin nhân viên tạo booking
    private String tenNhanVienTao;

    public CustomerDTO getKhachHang() {
        return khachHang;
    }
    public void setKhachHang(CustomerDTO khachHang) {
        this.khachHang = khachHang;
    }
    public String getRoomNumber() {
        return roomNumber;
    }
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getCustomerName() {
        return customerName;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public LocalDate getCheckInDate() {
        return ngayNhanPhong;
    }
    public LocalDate getCheckOutDate() {
        return ngayTraPhong;
    }
    public BigDecimal getTotalAmount() {
        return tongThanhToan;
    }
    public String getStatus() {
        return trangThaiDatPhong;
    }

    public LocalDate getNgayNhanPhong() {
        return ngayNhanPhong;
    }
    public void setNgayNhanPhong(LocalDate ngayNhanPhong) {
        this.ngayNhanPhong = ngayNhanPhong;
    }
    public LocalDate getNgayTraPhong() {
        return ngayTraPhong;
    }
    public void setNgayTraPhong(LocalDate ngayTraPhong) {
        this.ngayTraPhong = ngayTraPhong;
    }

    public String getTenNhanVienTao() {
        return tenNhanVienTao;
    }
    public void setTenNhanVienTao(String tenNhanVienTao) {
        this.tenNhanVienTao = tenNhanVienTao;
    }
} 