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

    // Lấy tên nhân viên
    public String getStaffName() {
        return "Nhân viên thanh toán";
    }

    // Lấy danh sách tất cả thanh toán
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    // Lấy danh sách booking đang hoạt động
    public List<Booking> getActiveBookings() {
        return bookingRepository.findAll().stream()
            .filter(b -> (b.getTrangThaiThanhToan() == Booking.TrangThaiThanhToan.CHUA_THANH_TOAN || 
                         b.getTrangThaiThanhToan() == Booking.TrangThaiThanhToan.DA_COC) &&
                         b.getTrangThaiDatPhong() != Booking.TrangThaiDatPhong.DA_HUY) // Loại bỏ booking đã hủy
            .collect(java.util.stream.Collectors.toList());
    }
    
    // Lấy danh sách phương thức thanh toán
    public List<String> getPaymentMethods() {
        return Arrays.asList("CASH", "CHUYEN_KHOAN");
    }
    
    // Tạo thanh toán tiền mặt
    public Payment createCashPayment(Integer bookingId, BigDecimal soTien, String phuongThuc, String noiDung, String maGiaoDich) {
        try {
            // Kiểm tra booking có tồn tại không
            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
            if (!bookingOpt.isPresent()) {
                return null;
            }
            
            Booking booking = bookingOpt.get();
            
            // Kiểm tra booking đã bị hủy chưa
            if (booking.getTrangThaiDatPhong() == Booking.TrangThaiDatPhong.DA_HUY) {
                return null; // Không cho phép thanh toán cho booking đã hủy
            }
            
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
    
    // Xác nhận thanh toán tiền mặt
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
    
    // Tính tổng tiền cần thanh toán
    public BigDecimal calculateTotalPaymentAmount(Integer bookingId) {
        try {
            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
            if (!bookingOpt.isPresent()) {
                return BigDecimal.ZERO;
            }
            
            Booking booking = bookingOpt.get();
            
            // Tổng tiền phòng
            BigDecimal tongTienPhong = booking.getTongTienPhong() != null ? booking.getTongTienPhong() : BigDecimal.ZERO;
            
            // Tổng tiền dịch vụ
            BigDecimal tongTienDichVu = booking.getTongTienDichVu() != null ? booking.getTongTienDichVu() : BigDecimal.ZERO;
            
            // Tiền cọc (50% tiền phòng)
            BigDecimal tienCoc = tongTienPhong.divide(BigDecimal.valueOf(2), 0, java.math.RoundingMode.HALF_UP);
            
            // Tổng cần thanh toán = Tiền phòng + Tiền dịch vụ - Tiền cọc
            BigDecimal tongCanThanhToan = tongTienPhong.add(tongTienDichVu).subtract(tienCoc);
            
            return tongCanThanhToan.compareTo(BigDecimal.ZERO) > 0 ? tongCanThanhToan : BigDecimal.ZERO;
            
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }
    
    // Tạo hóa đơn thanh toán
    public String generateInvoice(Integer paymentId) {
        try {
            Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
            if (!paymentOpt.isPresent()) {
                return null;
            }
            
            Payment payment = paymentOpt.get();
            Booking booking = payment.getBooking();
            
            // Tính toán chi tiết
            BigDecimal tongTienPhong = booking.getTongTienPhong() != null ? booking.getTongTienPhong() : BigDecimal.ZERO;
            BigDecimal tongTienDichVu = booking.getTongTienDichVu() != null ? booking.getTongTienDichVu() : BigDecimal.ZERO;
            BigDecimal tienCoc = tongTienPhong.divide(BigDecimal.valueOf(2), 0, java.math.RoundingMode.HALF_UP);
            BigDecimal tongCanThanhToan = calculateTotalPaymentAmount(booking.getId());
            
            StringBuilder invoice = new StringBuilder();
            invoice.append("=== HÓA ĐƠN THANH TOÁN ===\n");
            invoice.append("Mã thanh toán: ").append(payment.getId()).append("\n");
            invoice.append("Mã booking: ").append(booking.getId()).append("\n");
            invoice.append("Khách hàng: ").append(booking.getKhachHang().getHo() + " " + booking.getKhachHang().getTen()).append("\n");
            invoice.append("\n--- CHI TIẾT TÍNH TOÁN ---\n");
            invoice.append("Tổng tiền phòng: ").append(String.format("%,.0f", tongTienPhong)).append(" VND\n");
            invoice.append("Tổng tiền dịch vụ: ").append(String.format("%,.0f", tongTienDichVu)).append(" VND\n");
            invoice.append("Tiền cọc đã trả (50%): ").append(String.format("%,.0f", tienCoc)).append(" VND\n");
            invoice.append("\n--- TỔNG KẾT ---\n");
            invoice.append("Tổng cần thanh toán: ").append(String.format("%,.0f", tongCanThanhToan)).append(" VND\n");
            invoice.append("Số tiền thanh toán này: ").append(String.format("%,.0f", payment.getSoTien())).append(" VND\n");
            invoice.append("\n--- THÔNG TIN THANH TOÁN ---\n");
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
    
    // Lấy thông tin thanh toán theo ID
    public Payment getPaymentById(Integer paymentId) {
        try {
            return paymentRepository.findById(paymentId).orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Lấy trạng thái thanh toán của booking
    public java.util.Map<String, Object> getBookingPaymentStatus(Integer bookingId) {
        try {
            System.out.println("Getting payment status for booking ID: " + bookingId);
            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
            if (!bookingOpt.isPresent()) {
                System.out.println("Booking not found with ID: " + bookingId);
                return null;
            }
            
            Booking booking = bookingOpt.get();
            
            // Tính tổng tiền đã thanh toán thành công
            BigDecimal totalPaid = paymentRepository.findAll().stream()
                .filter(p -> p.getBooking() != null && p.getBooking().getId().equals(bookingId))
                .filter(p -> p.getTrangThai() == TrangThaiPayment.THANH_CONG)
                .map(Payment::getSoTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Tổng tiền cần thanh toán
            BigDecimal totalAmount = booking.getTongThanhToan() != null ? booking.getTongThanhToan() : BigDecimal.ZERO;
            
            java.util.Map<String, Object> result = new java.util.HashMap<>();
            result.put("bookingId", bookingId);
            result.put("maDatPhong", booking.getMaDatPhong());
            result.put("trangThaiThanhToan", booking.getTrangThaiThanhToan().name());
            result.put("trangThaiThanhToanLabel", booking.getTrangThaiThanhToan().getLabel());
            result.put("tongTien", totalAmount);
            result.put("daThanhtoan", totalPaid);
            result.put("conLai", totalAmount.subtract(totalPaid));
            result.put("isPaid", booking.getTrangThaiThanhToan() == Booking.TrangThaiThanhToan.DA_THANH_TOAN);
            
            return result;
        } catch (Exception e) {
            System.err.println("Error in getBookingPaymentStatus for booking ID " + bookingId + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}