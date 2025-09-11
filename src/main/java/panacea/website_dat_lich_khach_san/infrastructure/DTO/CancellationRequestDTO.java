package panacea.website_dat_lich_khach_san.infrastructure.DTO;

/**
 * DTO để nhận request hủy đặt phòng
 */
public class CancellationRequestDTO {
    private Long bookingId;
    private String cancellationReason;
    private String customerEmail; // Để xác thực quyền hủy

    // Constructors
    public CancellationRequestDTO() {}

    public CancellationRequestDTO(Long bookingId, String cancellationReason) {
        this.bookingId = bookingId;
        this.cancellationReason = cancellationReason;
    }

    public CancellationRequestDTO(Long bookingId, String cancellationReason, String customerEmail) {
        this.bookingId = bookingId;
        this.cancellationReason = cancellationReason;
        this.customerEmail = customerEmail;
    }

    // Getters and setters
    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    @Override
    public String toString() {
        return "CancellationRequestDTO{" +
                "bookingId=" + bookingId +
                ", cancellationReason='" + cancellationReason + '\'' +
                ", customerEmail='" + customerEmail + '\'' +
                '}';
    }
}