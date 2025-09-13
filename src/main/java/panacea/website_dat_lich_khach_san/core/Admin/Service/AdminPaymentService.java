package panacea.website_dat_lich_khach_san.core.Admin.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Payment;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.repository.PaymentRepository;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AdminPaymentService {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private BookingRepository bookingRepository;

    // 1. Tạo thanh toán (transaction cho booking)
    public Payment createPayment(Integer bookingId, BigDecimal soTien, String phuongThuc, String noiDung) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) return null;
        
        // Kiểm tra booking đã bị hủy chưa
        if (booking.getTrangThaiDatPhong() == Booking.TrangThaiDatPhong.DA_HUY) {
            return null; // Không cho phép thanh toán cho booking đã hủy
        }
        Payment payment = new Payment();
        payment.setMaThanhToan("PAY" + UUID.randomUUID().toString().substring(0, 8));
        payment.setBooking(booking);
        payment.setSoTien(soTien);
        payment.setPhuongThuc(phuongThuc);
        payment.setNoiDung(noiDung);
        payment.setTrangThai(Payment.TrangThaiPayment.DANG_XU_LY);
        payment.setNgayThanhToan(LocalDateTime.now());
        return paymentRepository.save(payment);
    }

    // 2. Xử lý thanh toán online (giả lập tích hợp gateway)
    public Payment processOnlinePayment(Integer bookingId, BigDecimal soTien, String gateway, String maGiaoDich) {
        Payment payment = createPayment(bookingId, soTien, gateway, "Thanh toán online qua " + gateway);
        if (payment == null) return null;
        payment.setMaGiaoDich(maGiaoDich);
        payment.setTrangThai(Payment.TrangThaiPayment.DANG_XU_LY);
        return paymentRepository.save(payment);
    }

    // 3. Xác nhận thanh toán (callback từ gateway)
    public Payment confirmPaymentCallback(String maGiaoDich, boolean thanhCong) {
        List<Payment> payments = paymentRepository.findAll().stream()
            .filter(p -> maGiaoDich.equals(p.getMaGiaoDich()))
            .toList();
        if (payments.isEmpty()) return null;
        Payment payment = payments.get(0);
        payment.setTrangThai(thanhCong ? Payment.TrangThaiPayment.THANH_CONG : Payment.TrangThaiPayment.THAT_BAI);
        return paymentRepository.save(payment);
    }

    // 4. Thanh toán tại quầy (cash)
    public Payment payAtReception(Integer bookingId, BigDecimal soTien) {
        return createPayment(bookingId, soTien, "CASH", "Thanh toán tại quầy");
    }

    // 5. Hoàn tiền (refund) - cập nhật payment hiện có
    public Payment refundPayment(Integer paymentId, BigDecimal soTien) {
        Optional<Payment> opt = paymentRepository.findById(paymentId);
        if (opt.isEmpty()) return null;
        Payment payment = opt.get();
        payment.setTrangThai(Payment.TrangThaiPayment.HOAN_TIEN);
        payment.setSoTien(soTien.negate());
        payment.setNoiDung("Refund");
        return paymentRepository.save(payment);
    }
    
    // 5b. Tạo payment hoàn tiền cho booking đã hủy
    public Payment createRefundPayment(Integer bookingId, BigDecimal refundAmount, String reason) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            throw new RuntimeException("Không tìm thấy booking với ID: " + bookingId);
        }
        
        // Chỉ cho phép hoàn tiền cho booking đã hủy
        if (booking.getTrangThaiDatPhong() != Booking.TrangThaiDatPhong.DA_HUY) {
            throw new RuntimeException("Chỉ có thể hoàn tiền cho booking đã hủy");
        }
        
        // Kiểm tra số tiền hoàn lại hợp lệ
        if (refundAmount == null || refundAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Số tiền hoàn phải lớn hơn 0");
        }
        
        // Kiểm tra số tiền hoàn không vượt quá số tiền đã thanh toán
        BigDecimal totalPaid = getTotalPaidAmount(bookingId);
        if (refundAmount.compareTo(totalPaid) > 0) {
            throw new RuntimeException("Số tiền hoàn không được vượt quá số tiền đã thanh toán: " + totalPaid);
        }
        
        // Kiểm tra số tiền hoàn không vượt quá 80% số tiền cọc (vì phí hủy = 20%)
        BigDecimal maxRefund = totalPaid.multiply(new BigDecimal("0.80"));
        if (refundAmount.compareTo(maxRefund) > 0) {
            throw new RuntimeException("Số tiền hoàn không được vượt quá 80% số tiền cọc (tối đa: " + maxRefund + " VNĐ)");
        }
        
        // Kiểm tra đã hoàn tiền chưa
        if (hasRefunded(bookingId)) {
            throw new RuntimeException("Booking này đã được hoàn tiền trước đó");
        }
        
        Payment refundPayment = new Payment();
        refundPayment.setMaThanhToan("REFUND" + UUID.randomUUID().toString().substring(0, 8));
        refundPayment.setBooking(booking);
        refundPayment.setSoTien(refundAmount.negate()); // Số âm để biểu thị hoàn tiền
        refundPayment.setPhuongThuc("REFUND");
        refundPayment.setNoiDung(reason != null ? reason : "Hoàn tiền do hủy đặt phòng");
        refundPayment.setTrangThai(Payment.TrangThaiPayment.HOAN_TIEN);
        refundPayment.setNgayThanhToan(LocalDateTime.now());
        
        // Cập nhật trạng thái thanh toán của booking
        booking.setTrangThaiThanhToan(Booking.TrangThaiThanhToan.HOAN_TIEN);
        bookingRepository.save(booking);
        
        return paymentRepository.save(refundPayment);
    }
    
    // Tính tổng số tiền đã thanh toán cho booking
    public BigDecimal getTotalPaidAmount(Integer bookingId) {
        List<Payment> payments = getPaymentsByBooking(bookingId);
        return payments.stream()
            .filter(p -> p.getTrangThai() == Payment.TrangThaiPayment.THANH_CONG)
            .map(Payment::getSoTien)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    // Kiểm tra booking đã được hoàn tiền chưa
    public boolean hasRefunded(Integer bookingId) {
        List<Payment> payments = getPaymentsByBooking(bookingId);
        return payments.stream()
            .anyMatch(p -> p.getTrangThai() == Payment.TrangThaiPayment.HOAN_TIEN);
    }
    
    // Tính toán số tiền hoàn tiền dựa trên chính sách hủy
    public BigDecimal calculateRefundAmount(Integer bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) return BigDecimal.ZERO;
        
        BigDecimal totalPaid = getTotalPaidAmount(bookingId);
        
        // Phí hủy = 20% số tiền cọc (tổng đã thanh toán)
        BigDecimal cancellationFee = totalPaid.multiply(new BigDecimal("0.20"));
        
        // Số tiền hoàn = Tổng đã thanh toán - Phí hủy
        BigDecimal refundAmount = totalPaid.subtract(cancellationFee);
        
        // Đảm bảo không âm
        return refundAmount.compareTo(BigDecimal.ZERO) > 0 ? refundAmount : BigDecimal.ZERO;
    }

    // 6. Xem lịch sử thanh toán theo booking/customer
    public List<Payment> getPaymentsByBooking(Integer bookingId) {
        return paymentRepository.findAll().stream()
            .filter(p -> p.getBooking() != null && p.getBooking().getId().equals(bookingId))
            .toList();
    }
    public List<Payment> getPaymentsByCustomer(Integer customerId) {
        return paymentRepository.findAll().stream()
            .filter(p -> p.getBooking() != null && p.getBooking().getKhachHang() != null && p.getBooking().getKhachHang().getId().equals(customerId))
            .toList();
    }

    // 7. In hóa đơn (giả lập trả về chuỗi PDF)
    public String generateInvoice(Integer paymentId) {
        Optional<Payment> opt = paymentRepository.findById(paymentId);
        if (opt.isEmpty()) return null;
        Payment payment = opt.get();
        // Giả lập nội dung PDF
        return "INVOICE\nPayment: " + payment.getMaThanhToan() + "\nAmount: " + payment.getSoTien() + "\nDate: " + payment.getNgayThanhToan();
    }
}