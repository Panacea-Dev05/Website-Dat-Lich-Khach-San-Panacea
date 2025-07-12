package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.entity.BookingDetail;
import panacea.website_dat_lich_khach_san.entity.Room;

@Data
public class BookingDetailViewDTO {
    private Integer id;
    private String maDatPhong;
    private Integer khachHangId;
    private Integer hotelId;
    private LocalDate ngayNhanPhong;
    private LocalDate ngayTraPhong;
    private Byte soNguoiLon;
    private Byte soTreEm;
    private BigDecimal tongThanhToan;
    private String trangThaiDatPhong;
    private String ghiChuKhachHang;
    private LocalDateTime ngayDat;
    private Long createdDate;
    
    // Thông tin khách hàng
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    
    // Thông tin khách sạn
    private String hotelName;
    
    // Thông tin phòng
    private String roomNumber;
    private String roomTypeName;
    
    public static BookingDetailViewDTO fromEntity(Booking booking, List<BookingDetail> details) {
        BookingDetailViewDTO dto = new BookingDetailViewDTO();
        dto.setId(booking.getId());
        dto.setMaDatPhong(booking.getMaDatPhong());
        dto.setKhachHangId(booking.getKhachHang() != null ? booking.getKhachHang().getId() : null);
        dto.setHotelId(booking.getHotel() != null ? booking.getHotel().getId() : null);
        dto.setNgayNhanPhong(booking.getNgayNhanPhong());
        dto.setNgayTraPhong(booking.getNgayTraPhong());
        dto.setSoNguoiLon(booking.getSoNguoiLon());
        dto.setSoTreEm(booking.getSoTreEm());
        dto.setTongThanhToan(booking.getTongThanhToan());
        dto.setTrangThaiDatPhong(booking.getTrangThaiDatPhong() != null ? booking.getTrangThaiDatPhong().name() : null);
        dto.setGhiChuKhachHang(booking.getGhiChuKhachHang());
        dto.setNgayDat(booking.getNgayDat());
        dto.setCreatedDate(booking.getCreatedDate());
        
        // Set customer info
        if (booking.getKhachHang() != null) {
            dto.setCustomerName(booking.getKhachHang().getHo() + " " + booking.getKhachHang().getTen());
            dto.setCustomerEmail(booking.getKhachHang().getEmail());
            dto.setCustomerPhone(booking.getKhachHang().getSoDienThoai());
        }
        
        // Set hotel info
        if (booking.getHotel() != null) {
            dto.setHotelName(booking.getHotel().getTenKhachSan());
        }
        
        // Set room info from BookingDetail
        if (details != null && !details.isEmpty() && details.get(0).getRoom() != null) {
            Room room = details.get(0).getRoom();
            dto.setRoomNumber(room.getSoPhong());
            if (room.getRoomType() != null) {
                dto.setRoomTypeName(room.getRoomType().getTenLoaiPhong());
            }
        }
        
        return dto;
    }
} 