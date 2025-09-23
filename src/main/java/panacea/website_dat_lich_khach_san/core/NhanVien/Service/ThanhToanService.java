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
            
            // Tổng tiền tổng cộng
            BigDecimal tongTienTongCong = tongTienPhong.add(tongTienDichVu);
            
            // Tiền cọc
            BigDecimal tienCoc = booking.getTienDatCoc() != null ? booking.getTienDatCoc() : tongTienPhong.divide(BigDecimal.valueOf(2), 0, java.math.RoundingMode.HALF_UP);
            
            // Tính tổng tiền đã thanh toán thành công
            BigDecimal tongDaThanhToan = paymentRepository.findAll().stream()
                .filter(p -> p.getBooking() != null && p.getBooking().getId().equals(bookingId))
                .filter(p -> p.getTrangThai() == TrangThaiPayment.THANH_CONG)
                .map(Payment::getSoTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Tổng cần thanh toán = Tổng tiền - Tiền cọc - Đã thanh toán
            BigDecimal tongCanThanhToan = tongTienTongCong.subtract(tienCoc).subtract(tongDaThanhToan);
            
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
            BigDecimal tongTienTongCong = tongTienPhong.add(tongTienDichVu);
            BigDecimal tienCoc = booking.getTienDatCoc() != null ? booking.getTienDatCoc() : tongTienPhong.divide(BigDecimal.valueOf(2), 0, java.math.RoundingMode.HALF_UP);
            
            // Tính tổng tiền đã thanh toán trước đó
            BigDecimal tongDaThanhToan = paymentRepository.findAll().stream()
                .filter(p -> p.getBooking() != null && p.getBooking().getId().equals(booking.getId()))
                .filter(p -> p.getTrangThai() == TrangThaiPayment.THANH_CONG)
                .filter(p -> !p.getId().equals(payment.getId())) // Loại trừ payment hiện tại
                .map(Payment::getSoTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // Tổng cần thanh toán = Tổng tiền - Tiền cọc - Đã thanh toán
            BigDecimal tongCanThanhToan = tongTienTongCong.subtract(tienCoc).subtract(tongDaThanhToan);
            
            StringBuilder invoice = new StringBuilder();
            invoice.append("<!DOCTYPE html>");
            invoice.append("<html><head>");
            invoice.append("<meta charset='UTF-8'>");
            invoice.append("<title>Hóa đơn thanh toán</title>");
            invoice.append("<style>");
            invoice.append("body { font-family: Arial, sans-serif; margin: 0; padding: 20px; text-align: center; }");
            invoice.append(".invoice-container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border: 1px solid #ddd; }");
            invoice.append(".header { border-bottom: 2px solid #333; padding-bottom: 20px; margin-bottom: 30px; }");
            invoice.append(".section { margin: 20px 0; text-align: left; }");
            invoice.append(".section h3 { color: #333; border-bottom: 1px solid #ccc; padding-bottom: 10px; }");
            invoice.append(".row { display: flex; justify-content: space-between; margin: 10px 0; }");
            invoice.append(".label { font-weight: bold; }");
            invoice.append(".amount { color: #d32f2f; font-weight: bold; }");
            invoice.append("@media print { body { margin: 0; } .invoice-container { border: none; box-shadow: none; } }");
            invoice.append("</style></head><body>");
            invoice.append("<div class='invoice-container'>");
            invoice.append("<div class='header'>");
            invoice.append("<h1>HÓA ĐƠN THANH TOÁN</h1>");
            invoice.append("</div>");
            invoice.append("<div class='section'>");
            invoice.append("<div class='row'><span class='label'>Mã thanh toán:</span><span>").append(payment.getId()).append("</span></div>");
            invoice.append("<div class='row'><span class='label'>Mã booking:</span><span>").append(booking.getId()).append("</span></div>");
            invoice.append("<div class='row'><span class='label'>Khách hàng:</span><span>").append(booking.getKhachHang().getHo() + " " + booking.getKhachHang().getTen()).append("</span></div>");
            invoice.append("</div>");
            invoice.append("<div class='section'>");
            invoice.append("<h3>CHI TIẾT TÍNH TOÁN</h3>");
            invoice.append("<div class='row'><span class='label'>Tổng tiền phòng:</span><span class='amount'>").append(String.format("%,.0f", tongTienPhong)).append(" VND</span></div>");
            invoice.append("<div class='row'><span class='label'>Tổng tiền dịch vụ:</span><span class='amount'>").append(String.format("%,.0f", tongTienDichVu)).append(" VND</span></div>");
            invoice.append("<div class='row'><span class='label'>Tổng cộng:</span><span class='amount'>").append(String.format("%,.0f", tongTienTongCong)).append(" VND</span></div>");
            invoice.append("<div class='row'><span class='label'>Tiền cọc đã trả:</span><span class='amount'>").append(String.format("%,.0f", tienCoc)).append(" VND</span></div>");
            invoice.append("<div class='row'><span class='label'>Đã thanh toán trước:</span><span class='amount'>").append(String.format("%,.0f", tongDaThanhToan)).append(" VND</span></div>");
            invoice.append("</div>");
            invoice.append("<div class='section'>");
            invoice.append("<h3>TỔNG KẾT</h3>");
            invoice.append("<div class='row'><span class='label'>Còn lại cần thanh toán:</span><span class='amount'>").append(String.format("%,.0f", tongCanThanhToan)).append(" VND</span></div>");
            invoice.append("<div class='row'><span class='label'>Số tiền thanh toán này:</span><span class='amount'>").append(String.format("%,.0f", payment.getSoTien())).append(" VND</span></div>");
            invoice.append("</div>");
            invoice.append("<div class='section'>");
            invoice.append("<h3>THÔNG TIN THANH TOÁN</h3>");
            invoice.append("<div class='row'><span class='label'>Phương thức:</span><span>").append(payment.getPhuongThuc()).append("</span></div>");
            invoice.append("<div class='row'><span class='label'>Trạng thái:</span><span>").append(payment.getTrangThai()).append("</span></div>");
            invoice.append("<div class='row'><span class='label'>Ngày tạo:</span><span>").append(payment.getCreatedDate()).append("</span></div>");
            invoice.append("<div class='row'><span class='label'>Nhân viên:</span><span>").append(getStaffName()).append("</span></div>");
            invoice.append("</div>");
            invoice.append("</div></body></html>");
            
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