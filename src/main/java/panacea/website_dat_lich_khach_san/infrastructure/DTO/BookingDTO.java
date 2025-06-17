package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

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
} 