package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO để trả về response sau khi hủy đặt phòng
 */
public class CancellationResponseDTO {
    private boolean success;
    private String message;
    private Long bookingId;
    private String maDatPhong;
    private BigDecimal cancellationFee;
    private BigDecimal refundAmount;
    private LocalDateTime cancellationDate;
    private String refundNote;

    // Constructors
    public CancellationResponseDTO() {}

    public CancellationResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // Getters and setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getMaDatPhong() {
        return maDatPhong;
    }

    public void setMaDatPhong(String maDatPhong) {
        this.maDatPhong = maDatPhong;
    }

    public BigDecimal getCancellationFee() {
        return cancellationFee;
    }

    public void setCancellationFee(BigDecimal cancellationFee) {
        this.cancellationFee = cancellationFee;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public LocalDateTime getCancellationDate() {
        return cancellationDate;
    }

    public void setCancellationDate(LocalDateTime cancellationDate) {
        this.cancellationDate = cancellationDate;
    }

    public String getRefundNote() {
        return refundNote;
    }

    public void setRefundNote(String refundNote) {
        this.refundNote = refundNote;
    }

    @Override
    public String toString() {
        return "CancellationResponseDTO{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", bookingId=" + bookingId +
                ", maDatPhong='" + maDatPhong + '\'' +
                ", cancellationFee=" + cancellationFee +
                ", refundAmount=" + refundAmount +
                ", cancellationDate=" + cancellationDate +
                '}';
    }
}