package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Payment;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.entity.Payment.TrangThaiPayment;
import panacea.website_dat_lich_khach_san.repository.PaymentRepository;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;

@Service
public class ThanhToanService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private BookingRepository bookingRepository;

    public String getStaffName() {
        return "Nguyễn Văn A";
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    // Lấy danh sách booking đang hoạt động (chưa thanh toán hoặc thanh toán một phần)
    public List<Booking> getActiveBookings() {
        return bookingRepository.findAll().stream()
            .filter(b -> b.getTrangThaiThanhToan() == Booking.TrangThaiThanhToan.CHUA_THANH_TOAN || 
                        b.getTrangThaiThanhToan() == Booking.TrangThaiThanhToan.DA_COC)
            .collect(java.util.stream.Collectors.toList());
    }
    
    // Lấy danh sách phương thức thanh toán cho nhân viên (chỉ cash và chuyển khoản)
    public List<String> getPaymentMethods() {
        return Arrays.asList("CASH", "CHUYEN_KHOAN");
    }
    
    // Tạo thanh toán cash (nhân viên chỉ được tạo thanh toán tại quầy)
    public Payment createCashPayment(Integer bookingId, BigDecimal soTien, String phuongThuc, String noiDung, String maGiaoDich) {
        try {
            // Kiểm tra booking có tồn tại không
            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId.longValue());
            if (!bookingOpt.isPresent()) {
                return null;
            }
            
            Booking booking = bookingOpt.get();
            
            // Kiểm tra phương thức thanh toán hợp lệ cho nhân viên
            if (!"CASH".equals(phuongThuc) && !"CHUYEN_KHOAN".equals(phuongThuc)) {
                return null;
            }
            
            // Tạo payment mới
            Payment payment = new Payment();
            payment.setBooking(booking);
            payment.setSoTien(soTien);
            payment.setPhuongThuc(phuongThuc);
            payment.setNoiDung(noiDung != null ? noiDung : "Thanh toán tại quầy");
            payment.setMaGiaoDich(maGiaoDich != null && !maGiaoDich.trim().isEmpty() ? maGiaoDich : null);
            
            // Tạo mã thanh toán tự động
            String maThanhToan = "PAY" + System.currentTimeMillis();
            System.out.println("Generated maThanhToan: " + maThanhToan);
            payment.setMaThanhToan(maThanhToan);
            System.out.println("Payment object before save: " + payment.getMaThanhToan());
            
            payment.setTrangThai(TrangThaiPayment.DANG_XU_LY);
            payment.setNgayThanhToan(LocalDateTime.now());
            payment.setCreatedDate(System.currentTimeMillis());
            payment.setLastModifiedDate(System.currentTimeMillis());
            
            return paymentRepository.save(payment);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Xác nhận thanh toán cash (nhân viên chỉ được xác nhận thanh toán tại quầy)
    public Payment confirmCashPayment(Integer paymentId) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
            if (!paymentOpt.isPresent()) {
                return null;
            }
            
            Payment payment = paymentOpt.get();
            
            // Kiểm tra chỉ được xác nhận thanh toán cash/chuyển khoản
            if (!"CASH".equals(payment.getPhuongThuc()) && !"CHUYEN_KHOAN".equals(payment.getPhuongThuc())) {
                return null;
            }
            
            // Kiểm tra trạng thái hiện tại
            if (payment.getTrangThai() != TrangThaiPayment.DANG_XU_LY) {
                return null;
            }
            
            // Cập nhật trạng thái
            payment.setTrangThai(TrangThaiPayment.THANH_CONG);
            payment.setLastModifiedDate(System.currentTimeMillis());
            
            // Cập nhật trạng thái booking nếu cần
            Booking booking = payment.getBooking();
            if (booking != null) {
                // Tính tổng số tiền đã thanh toán
                BigDecimal totalPaid = paymentRepository.findAll().stream()
                    .filter(p -> p.getBooking() != null && p.getBooking().getId().equals(booking.getId()))
                    .filter(p -> p.getTrangThai() == TrangThaiPayment.THANH_CONG)
                    .map(Payment::getSoTien)
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .add(payment.getSoTien());
                
                // Cập nhật trạng thái booking
                 if (totalPaid.compareTo(booking.getTongThanhToan()) >= 0) {
                     booking.setTrangThaiThanhToan(Booking.TrangThaiThanhToan.DA_THANH_TOAN);
                 } else {
                     booking.setTrangThaiThanhToan(Booking.TrangThaiThanhToan.DA_COC);
                 }
                bookingRepository.save(booking);
            }
            
            return paymentRepository.save(payment);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Tạo hóa đơn đơn giản
    public String generateInvoice(Integer paymentId) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
            if (!paymentOpt.isPresent()) {
                return null;
            }
            
            Payment payment = paymentOpt.get();
            Booking booking = payment.getBooking();
            
            StringBuilder invoice = new StringBuilder();
            invoice.append("=== HÓA ĐƠN THANH TOÁN ===\n");
            invoice.append("Mã thanh toán: ").append(payment.getId()).append("\n");
            invoice.append("Mã booking: ").append(booking.getId()).append("\n");
            invoice.append("Khách hàng: ").append(booking.getKhachHang().getHo() + " " + booking.getKhachHang().getTen()).append("\n");
            invoice.append("Số tiền: ").append(payment.getSoTien()).append(" VND\n");
            invoice.append("Phương thức: ").append(payment.getPhuongThuc()).append("\n");
            invoice.append("Trạng thái: ").append(payment.getTrangThai()).append("\n");
            invoice.append("Ngày tạo: ").append(payment.getCreatedDate()).append("\n");
            invoice.append("Nhân viên: ").append(getStaffName()).append("\n");
            invoice.append("========================\n");
            
            return invoice.toString();
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Lấy chi tiết thanh toán
    public Payment getPaymentById(Integer paymentId) {
        try {
            return paymentRepository.findById(paymentId).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}