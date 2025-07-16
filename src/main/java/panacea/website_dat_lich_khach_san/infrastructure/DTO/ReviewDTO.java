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
    private String roomNumber;
    private Integer rating;
    private String comment;
    private String status;

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
} 