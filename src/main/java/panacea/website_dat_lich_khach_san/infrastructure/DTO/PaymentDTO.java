package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Data;

@Data
public class PaymentDTO {
    private Long id;
    private Long bookingId;
    private String hinhThucThanhToan;
    private BigDecimal soTien;
    private LocalDateTime thoiGianThanhToan;
    private String trangThai;
    private BigDecimal amount;          // Thêm field này
    private String paymentMethod;       // Thêm field này
    private LocalDateTime paymentDate;  // Thêm field này
    private String status;
    private UUID uuidId;
    private Long createdDate;
    private String customerName;
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
}