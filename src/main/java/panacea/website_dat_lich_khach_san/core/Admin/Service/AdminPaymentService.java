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
        Booking booking = bookingRepository.findById(Long.valueOf(bookingId)).orElse(null);
        if (booking == null) return null;
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

    // 5. Hoàn tiền (refund)
    public Payment refundPayment(Long paymentId, BigDecimal soTien) {
        Optional<Payment> opt = paymentRepository.findById(paymentId);
        if (opt.isEmpty()) return null;
        Payment payment = opt.get();
        payment.setTrangThai(Payment.TrangThaiPayment.HOAN_TIEN);
        payment.setSoTien(soTien.negate());
        payment.setNoiDung("Refund");
        return paymentRepository.save(payment);
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
    public String generateInvoice(Long paymentId) {
        Optional<Payment> opt = paymentRepository.findById(paymentId);
        if (opt.isEmpty()) return null;
        Payment payment = opt.get();
        // Giả lập nội dung PDF
        return "INVOICE\nPayment: " + payment.getMaThanhToan() + "\nAmount: " + payment.getSoTien() + "\nDate: " + payment.getNgayThanhToan();
    }
} 