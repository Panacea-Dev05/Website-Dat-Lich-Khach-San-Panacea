package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class BookingRequestDTO {
    private Integer roomId;
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
} 