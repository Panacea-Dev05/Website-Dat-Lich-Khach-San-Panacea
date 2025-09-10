package panacea.website_dat_lich_khach_san.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public enum CancellationPolicy {
    FLEXIBLE("Linh hoạt", 0, 100), // Hủy bất kỳ lúc nào, hoàn 100%
    MODERATE("Vừa phải", 24, 50), // Hủy trước 24h hoàn 50%, sau đó không hoàn
    STRICT("Nghiêm ngặt", 72, 0), // Hủy trước 72h hoàn 0%, sau đó không hoàn
    NON_REFUNDABLE("Không hoàn tiền", 0, 0); // Không hoàn tiền trong mọi trường hợp

    private final String label;
    private final int hoursBeforeCheckIn; // Số giờ trước check-in để áp dụng chính sách
    private final int refundPercentage; // Phần trăm hoàn tiền

    CancellationPolicy(String label, int hoursBeforeCheckIn, int refundPercentage) {
        this.label = label;
        this.hoursBeforeCheckIn = hoursBeforeCheckIn;
        this.refundPercentage = refundPercentage;
    }

    public String getLabel() {
        return label;
    }

    public int getHoursBeforeCheckIn() {
        return hoursBeforeCheckIn;
    }

    public int getRefundPercentage() {
        return refundPercentage;
    }

    /**
     * Tính toán số tiền hoàn lại dựa trên chính sách hủy
     * @param totalAmount Tổng số tiền đã thanh toán
     * @param checkInDateTime Thời gian check-in dự kiến
     * @param cancellationDateTime Thời gian hủy
     * @return Số tiền hoàn lại
     */
    public BigDecimal calculateRefund(BigDecimal totalAmount, LocalDateTime checkInDateTime, LocalDateTime cancellationDateTime) {
        if (totalAmount == null || checkInDateTime == null || cancellationDateTime == null) {
            return BigDecimal.ZERO;
        }

        // Tính số giờ từ lúc hủy đến lúc check-in
        long hoursUntilCheckIn = ChronoUnit.HOURS.between(cancellationDateTime, checkInDateTime);

        switch (this) {
            case FLEXIBLE:
                return totalAmount; // Hoàn 100% bất kỳ lúc nào
            
            case MODERATE:
                if (hoursUntilCheckIn >= 24) {
                    return totalAmount.multiply(BigDecimal.valueOf(0.5)); // Hoàn 50%
                }
                return BigDecimal.ZERO; // Không hoàn tiền
            
            case STRICT:
                if (hoursUntilCheckIn >= 72) {
                    return totalAmount.multiply(BigDecimal.valueOf(0.25)); // Hoàn 25% nếu hủy trước 72h
                }
                return BigDecimal.ZERO; // Không hoàn tiền
            
            case NON_REFUNDABLE:
            default:
                return BigDecimal.ZERO; // Không hoàn tiền
        }
    }

    /**
     * Lấy mô tả chi tiết về chính sách hủy
     */
    public String getDescription() {
        switch (this) {
            case FLEXIBLE:
                return "Hủy miễn phí bất kỳ lúc nào. Hoàn tiền 100%.";
            case MODERATE:
                return "Hủy trước 24 giờ: hoàn 50%. Hủy trong vòng 24 giờ: không hoàn tiền.";
            case STRICT:
                return "Hủy trước 72 giờ: hoàn 25%. Hủy trong vòng 72 giờ: không hoàn tiền.";
            case NON_REFUNDABLE:
                return "Không hoàn tiền trong mọi trường hợp.";
            default:
                return "Chính sách không xác định.";
        }
    }

    public static CancellationPolicy fromString(String input) {
        if (input == null) return MODERATE; // Default policy
        for (CancellationPolicy policy : CancellationPolicy.values()) {
            if (policy.label.equalsIgnoreCase(input) || policy.name().equalsIgnoreCase(input)) {
                return policy;
            }
        }
        return MODERATE; // Default policy
    }
}