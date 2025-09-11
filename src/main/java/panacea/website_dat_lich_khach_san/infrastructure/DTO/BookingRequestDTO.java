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
    private String soCmndCccd;
    private String diaChi;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String quocTich;
    private String bookingType; // Loại thuê: ngay, gio, dem
    private String loaiGia; // Loại giá: giaNgay, giaGio, giaQuaDem
    private Integer bookingQuantity; // Số lượng (ngày/giờ/đêm)
    private List<String> dichVu;
    private String yeuCauDacBiet; // Yêu cầu đặc biệt từ khách hàng
    private Integer soGiuong; // Số giường khách chọn (1 hoặc 2)
    private Integer soPhong; // Số phòng cần đặt (mặc định 1)
    
    // Thêm trường cho combo nhiều loại phòng
    private String roomSelection; // "single" hoặc "multiple"
    private List<MultiRoomTypeRequest> multiRoomTypes; // Danh sách combo phòng
    
    @Data
    public static class MultiRoomTypeRequest {
        private String roomType; // "single", "double", "suite"
        private Integer quantity; // Số lượng phòng loại này
        private Integer price; // Giá của loại phòng này
    }
}