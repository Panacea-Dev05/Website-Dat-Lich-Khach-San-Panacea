package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReviewDTO {
    private Integer id;
    private Integer bookingId;
    private Integer customerId;
    private Integer hotelId;
    private Integer roomId;
    private Integer staffId;
    private Integer serviceId;
    private Integer diemDanhGia;
    private String noiDung;
    private String trangThai;
    private LocalDateTime thoiGianDanhGia;
    private UUID uuidId;
    private LocalDateTime createdDate;
    private String customerName;
    private String customerEmail; // Thêm trường email khách hàng
    private String roomNumber;
    private Integer rating;
    private String comment;
    private String status;
    
    // Thêm các trường cho đánh giá chi tiết
    private Byte diemTongQuan;
    private Byte diemSachSe;
    private Byte diemDichVu;
    private Byte diemViTri;
    private Byte diemGiaCa;
    private String binhLuan;
    private LocalDateTime ngayDanhGia;
    private Integer roomTypeId;

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }
    public Integer getRating() { return rating != null ? rating : diemDanhGia; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public LocalDateTime getCreatedDate() { return createdDate; }
    public void setCreatedDate(LocalDateTime createdDate) { this.createdDate = createdDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    // Getter/Setter cho customerEmail
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    
    // Getter/Setter cho các trường đánh giá chi tiết
    public Byte getDiemTongQuan() { return diemTongQuan; }
    public void setDiemTongQuan(Byte diemTongQuan) { this.diemTongQuan = diemTongQuan; }
    
    public Byte getDiemSachSe() { return diemSachSe; }
    public void setDiemSachSe(Byte diemSachSe) { this.diemSachSe = diemSachSe; }
    
    public Byte getDiemDichVu() { return diemDichVu; }
    public void setDiemDichVu(Byte diemDichVu) { this.diemDichVu = diemDichVu; }
    
    public Byte getDiemViTri() { return diemViTri; }
    public void setDiemViTri(Byte diemViTri) { this.diemViTri = diemViTri; }
    
    public Byte getDiemGiaCa() { return diemGiaCa; }
    public void setDiemGiaCa(Byte diemGiaCa) { this.diemGiaCa = diemGiaCa; }
    
    public String getBinhLuan() { return binhLuan; }
    public void setBinhLuan(String binhLuan) { this.binhLuan = binhLuan; }
    
    public LocalDateTime getNgayDanhGia() { return ngayDanhGia; }
    public void setNgayDanhGia(LocalDateTime ngayDanhGia) { this.ngayDanhGia = ngayDanhGia; }
    
    public Integer getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(Integer roomTypeId) { this.roomTypeId = roomTypeId; }
} 