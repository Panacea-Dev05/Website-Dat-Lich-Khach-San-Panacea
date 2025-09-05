package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class BookingRequestDTO {
    private Integer roomTypeId; // Khách chỉ chọn loại phòng
    private Integer hotelId;
    private LocalDate ngayNhanPhong;
    private LocalDate ngayTraPhong;
    private Byte soNguoiLon;
    private Byte soTreEm;
    private String tenKhach;
    private String emailKhach;
    private String soDienThoai;
    private String ghiChuKhachHang;
    private String hoKhach;
    private String bookingType; // Loại thuê: ngay, gio, dem
    private Integer bookingQuantity; // Số lượng (ngày/giờ/đêm)
    private List<String> dichVu;
} 