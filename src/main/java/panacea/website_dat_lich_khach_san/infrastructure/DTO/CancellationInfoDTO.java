package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import panacea.website_dat_lich_khach_san.entity.CancellationPolicy;

import java.math.BigDecimal;

/**
 * DTO để trả về thông tin hủy đặt phòng
 */
public class CancellationInfoDTO {
    private Integer bookingId;
    private String maDatPhong;
    private CancellationPolicy cancellationPolicy;
    private String policyDescription;
    private BigDecimal originalAmount;
    private BigDecimal cancellationFee;
    private BigDecimal refundAmount;
    private long hoursUntilCheckIn;
    private boolean canCancel;
    private String cancellationReason;

    // Constructors
    public CancellationInfoDTO() {}

    // Getters and setters
    public Integer getBookingId() {
        return bookingId;
    }

    public void setBookingId(Integer bookingId) {
        this.bookingId = bookingId;
    }

    public String getMaDatPhong() {
        return maDatPhong;
    }

    public void setMaDatPhong(String maDatPhong) {
        this.maDatPhong = maDatPhong;
    }

    public CancellationPolicy getCancellationPolicy() {
        return cancellationPolicy;
    }

    public void setCancellationPolicy(CancellationPolicy cancellationPolicy) {
        this.cancellationPolicy = cancellationPolicy;
    }

    public String getPolicyDescription() {
        return policyDescription;
    }

    public void setPolicyDescription(String policyDescription) {
        this.policyDescription = policyDescription;
    }

    public BigDecimal getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(BigDecimal originalAmount) {
        this.originalAmount = originalAmount;
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

    public long getHoursUntilCheckIn() {
        return hoursUntilCheckIn;
    }

    public void setHoursUntilCheckIn(long hoursUntilCheckIn) {
        this.hoursUntilCheckIn = hoursUntilCheckIn;
    }

    public boolean isCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    @Override
    public String toString() {
        return "CancellationInfoDTO{" +
                "bookingId=" + bookingId +
                ", maDatPhong='" + maDatPhong + '\'' +
                ", cancellationPolicy=" + cancellationPolicy +
                ", originalAmount=" + originalAmount +
                ", cancellationFee=" + cancellationFee +
                ", refundAmount=" + refundAmount +
                ", hoursUntilCheckIn=" + hoursUntilCheckIn +
                ", canCancel=" + canCancel +
                '}';
    }
}