package panacea.website_dat_lich_khach_san.core.KhachHang.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.entity.Customer;
import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.BookingRequestDTO;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.repository.CustomerRepository;
import panacea.website_dat_lich_khach_san.repository.HotelRepository;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayOutputStream;
import org.springframework.core.io.ByteArrayResource;
import java.math.BigDecimal;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;

@Service
public class KhachHangService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired(required = false)
    private JavaMailSender mailSender;

    public boolean datPhongChoKhachHang(BookingRequestDTO dto) {
        try {
            Room room = roomRepository.findById(Integer.valueOf(dto.getRoomId())).orElse(null);
            Hotel hotel = hotelRepository.findById(dto.getHotelId()).orElse(null);
            if (room == null || hotel == null) return false;

            // Tạo khách hàng nếu chưa có (giả sử theo email)
            Customer customer = customerRepository.findByEmail(dto.getEmailKhach()).orElseGet(() -> {
                Customer c = new Customer();
                c.setHo(dto.getHoKhach());
                c.setTen(dto.getTenKhach());
                c.setEmail(dto.getEmailKhach());
                c.setSoDienThoai(dto.getSoDienThoai());
                c.setMaKhachHang("KH" + System.currentTimeMillis());
                c.setMatKhauHash("default"); // hoặc sinh random nếu muốn
                // Có thể set thêm các trường mặc định khác nếu cần
                return customerRepository.save(c);
            });

            Booking booking = new Booking();
            booking.setKhachHang(customer);
            booking.setHotel(hotel);
            booking.setNgayNhanPhong(dto.getNgayNhanPhong());
            booking.setNgayTraPhong(dto.getNgayTraPhong());
            booking.setSoNguoiLon(dto.getSoNguoiLon());
            booking.setSoTreEm(dto.getSoTreEm());
            booking.setGhiChuKhachHang(dto.getGhiChuKhachHang());
            booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.CHO_XAC_NHAN);
            booking.setNgayDat(LocalDateTime.now());
            // Sinh mã đặt phòng tự động
            String maDatPhong = "BOOK" + System.currentTimeMillis();
            booking.setMaDatPhong(maDatPhong);
            // Đảm bảo các trường số không null/hợp lệ (>= 1 nếu constraint > 0)
            if (booking.getSoNguoiLon() == null || booking.getSoNguoiLon() < 1) booking.setSoNguoiLon((byte) 1);
            if (booking.getSoTreEm() == null || booking.getSoTreEm() < 0) booking.setSoTreEm((byte) 0);
            if (booking.getTongTienPhong() == null || booking.getTongTienPhong().compareTo(BigDecimal.ONE) < 0)
                booking.setTongTienPhong(new BigDecimal("10000")); // hoặc lấy giá phòng thực tế
            if (booking.getTongThanhToan() == null || booking.getTongThanhToan().compareTo(BigDecimal.ONE) < 0)
                booking.setTongThanhToan(booking.getTongTienPhong());
            if (booking.getTongTienDichVu() == null || booking.getTongTienDichVu().compareTo(BigDecimal.ZERO) < 0)
                booking.setTongTienDichVu(BigDecimal.ZERO);
            if (booking.getGiamGiaPromotion() == null || booking.getGiamGiaPromotion().compareTo(BigDecimal.ZERO) < 0)
                booking.setGiamGiaPromotion(BigDecimal.ZERO);
            if (booking.getPhiThue() == null || booking.getPhiThue().compareTo(BigDecimal.ZERO) < 0)
                booking.setPhiThue(BigDecimal.ZERO);
            if (booking.getTienDatCoc() == null || booking.getTienDatCoc().compareTo(BigDecimal.ZERO) < 0)
                booking.setTienDatCoc(BigDecimal.ZERO);
            // TODO: set thêm các trường khác nếu cần

            bookingRepository.save(booking);

            // --- Đọc ảnh QR Momo tĩnh từ resources ---
            InputStreamSource qrImage = new ClassPathResource("static/img/momo-qr.png");

            // Gửi mail cho khách hàng
            if (mailSender != null) {
                String subject = "[Panacea Hotel] Yêu cầu đặt phòng đã được gửi";
                StringBuilder dichVuHtml = new StringBuilder();
                if (dto.getDichVu() != null && !dto.getDichVu().isEmpty()) {
                    dichVuHtml.append("<li><b>Dịch vụ đăng ký:</b> <ul>");
                    for (String dv : dto.getDichVu()) {
                        dichVuHtml.append("<li>").append(dv).append("</li>");
                    }
                    dichVuHtml.append("</ul></li>");
                }
                String text = String.format(
                    "<h2>Cảm ơn %s đã đặt phòng tại Panacea Hotel!</h2>" +
                    "<p>Thông tin đặt phòng của bạn:</p>" +
                    "<ul>" +
                    "<li>Khách sạn: Panacea Hotel</li>" +
                    "<li>Ngày nhận phòng: %s</li>" +
                    "<li>Ngày trả phòng: %s</li>" +
                    "<li>Số người lớn: %d</li>" +
                    "<li>Số trẻ em: %d</li>" +
                    "<li>Ghi chú: %s</li>" +
                    "%s" +
                    "</ul>" +
                    "<p>Vui lòng thanh toán qua Momo bằng cách quét mã QR dưới đây:</p>" +
                    "<img src='cid:qr_momo' width='250' height='250'/>" +
                    "<p><b>Số tiền cần chuyển: </b>" + booking.getTongThanhToan() + " VNĐ</p>" +
                    "<p><b>Nội dung chuyển khoản: </b>DatPhong_" + maDatPhong + "</p>" +
                    "<p><b>Lưu ý:</b> Sau khi chuyển khoản, vui lòng giữ lại biên lai để đối chiếu khi nhận phòng.</p>" +
                    "<p>Yêu cầu của bạn đang chờ xác nhận từ nhân viên. Chúng tôi sẽ gửi email xác nhận khi đặt phòng được duyệt.</p>" +
                    "<br><b>Panacea Hotel</b>",
                    dto.getTenKhach(),
                    dto.getNgayNhanPhong(),
                    dto.getNgayTraPhong(),
                    dto.getSoNguoiLon(),
                    dto.getSoTreEm(),
                    dto.getGhiChuKhachHang() != null ? dto.getGhiChuKhachHang() : "Không có",
                    dichVuHtml.toString()
                );
                sendMailWithQRFile(dto.getEmailKhach(), subject, text, qrImage);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void sendMail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        mailSender.send(message);
    }

    // Hàm sinh QR code từ chuỗi (dùng ZXing)
    private byte[] generateQRCodeImage(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Gửi mail kèm QR code (ảnh inline từ file)
    private void sendMailWithQRFile(String to, String subject, String html, InputStreamSource qrImage) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        if (qrImage != null) {
            helper.addInline("qr_momo", qrImage, "image/png");
        }
        mailSender.send(message);
    }
} 