package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Integer id;
    private Integer bookingId;
    private String hinhThucThanhToan;
    private BigDecimal soTien;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime thoiGianThanhToan;
    
    private String trangThai;
    private BigDecimal amount;          // Thêm field này
    private String paymentMethod;       // Thêm field này
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime paymentDate;  // Thêm field này
    
    private String status;
    private UUID uuidId;
    private Long createdDate;
    private String customerName;
    
    // Thêm các field còn thiếu
    private String noiDung;
    private String maGiaoDich;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime ngayTao;
    
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}