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
import panacea.website_dat_lich_khach_san.repository.RoomPricingRepositoty;
import panacea.website_dat_lich_khach_san.repository.RoomImagesRepositoty;
import panacea.website_dat_lich_khach_san.repository.RoomTypeRepository;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomTypeDTO;
import panacea.website_dat_lich_khach_san.entity.RoomPricing;
import java.util.ArrayList;
import java.util.List;

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
    @Autowired
    private RoomPricingRepositoty roomPricingRepositoty;
    @Autowired
    private RoomImagesRepositoty roomImagesRepositoty;
    @Autowired
    private RoomTypeRepository roomTypeRepository;

    public boolean datPhongChoKhachHang(BookingRequestDTO dto) {
        try {
            // KHÔNG lấy room theo id nữa, chỉ lấy loại phòng
            var roomType = roomTypeRepository.findById(dto.getRoomTypeId()).orElse(null);
            if (roomType == null) return false;

            // Tạo khách hàng nếu chưa có (giả sử theo email)
            Customer customer = customerRepository.findByEmail(dto.getEmailKhach()).orElseGet(() -> {
                Customer c = new Customer();
                c.setHo(dto.getHoKhach());
                c.setTen(dto.getTenKhach());
                c.setEmail(dto.getEmailKhach());
                c.setSoDienThoai(dto.getSoDienThoai());
                c.setMaKhachHang("KH" + System.currentTimeMillis());
                c.setMatKhauHash("default");
                return customerRepository.save(c);
            });

            Booking booking = new Booking();
            booking.setKhachHang(customer);
            booking.setNgayNhanPhong(dto.getNgayNhanPhong());
            booking.setNgayTraPhong(dto.getNgayTraPhong());
            booking.setSoNguoiLon(dto.getSoNguoiLon());
            booking.setSoTreEm(dto.getSoTreEm());
            booking.setGhiChuKhachHang(dto.getGhiChuKhachHang());
            booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.CHO_XAC_NHAN);
            booking.setNgayDat(LocalDateTime.now());
            // Lưu loại phòng khách chọn
            booking.setRoomType(roomType);
            // Sinh mã đặt phòng tự động
            String maDatPhong = "BOOK" + System.currentTimeMillis();
            booking.setMaDatPhong(maDatPhong);
            // --- TÍNH GIÁ THEO LOẠI THUÊ ---
            String bookingType = dto.getBookingType();
            Integer bookingQuantity = dto.getBookingQuantity() != null && dto.getBookingQuantity() > 0 ? dto.getBookingQuantity() : 1;
            BigDecimal unitPrice = BigDecimal.ZERO;
            RoomPricing pricing = roomPricingRepositoty.findFirstByRoomType_IdAndLoaiGia(roomType.getId(), RoomPricing.LoaiGia.BASE);
            if (pricing != null) {
                if ("ngay".equals(bookingType)) unitPrice = pricing.getGiaNgay();
                else if ("gio".equals(bookingType)) unitPrice = pricing.getGiaGio();
                else if ("dem".equals(bookingType)) unitPrice = pricing.getGiaQuaDem();
            }
            BigDecimal tongTienPhong = unitPrice.multiply(BigDecimal.valueOf(bookingQuantity));
            BigDecimal tienDatCoc = tongTienPhong.divide(BigDecimal.valueOf(2), 0, java.math.RoundingMode.HALF_UP);
            booking.setTongTienPhong(tongTienPhong);
            booking.setTongThanhToan(tongTienPhong); // Nếu chưa có dịch vụ/phí khác
            booking.setTienDatCoc(tienDatCoc);
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
                String bookingTypeLabel = "Theo ngày";
                if ("gio".equals(dto.getBookingType())) bookingTypeLabel = "Theo giờ";
                else if ("dem".equals(dto.getBookingType())) bookingTypeLabel = "Theo đêm";
                String text = String.format(
                    "<h2>Cảm ơn %s đã đặt phòng tại Panacea Hotel!</h2>" +
                    "<p>Thông tin đặt phòng của bạn:</p>" +
                    "<ul>" +
                    "<li>Khách sạn: Panacea Hotel</li>" +
                    "<li>Loại phòng: %s</li>" +
                    "<li>Loại thuê: %s</li>" +
                    "<li>Số lượng: %d</li>" +
                    "<li>Ngày nhận phòng: %s</li>" +
                    "<li>Ngày trả phòng: %s</li>" +
                    "<li>Số người lớn: %d</li>" +
                    "<li>Số trẻ em: %d</li>" +
                    "<li>Ghi chú: %s</li>" +
                    "%s" +
                    "</ul>" +
                    "<p>Vui lòng thanh toán qua Momo bằng cách quét mã QR dưới đây:</p>" +
                    "<img src='cid:qr_momo' width='250' height='250'/>" +
                    "<p><b>Số tiền cần thanh toán khi trả phòng: </b>" + booking.getTongThanhToan() + " VNĐ</p>" +
                    "<p><b>Tiền đặt cọc (50%%): </b>" + booking.getTienDatCoc() + " VNĐ</p>" +
                    "<p><b>Nội dung chuyển khoản: </b>DatPhong_" + maDatPhong + "</p>" +
                    "<p><b>Lưu ý:</b> Sau khi chuyển khoản, vui lòng giữ lại biên lai để đối chiếu khi nhận phòng.</p>" +
                    "<p>Yêu cầu của bạn đang chờ xác nhận từ nhân viên. Chúng tôi sẽ gửi email xác nhận khi đặt phòng được duyệt.</p>" +
                    "<br><b>Panacea Hotel</b>",
                    dto.getTenKhach(),
                    roomType.getTenLoaiPhong(),
                    bookingTypeLabel,
                    dto.getBookingQuantity() != null ? dto.getBookingQuantity() : 1,
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

    public List<RoomTypeDTO> getAllRoomTypesForCustomer() {
        List<panacea.website_dat_lich_khach_san.entity.RoomType> roomTypes = roomTypeRepository.findAll();
        List<RoomTypeDTO> result = new ArrayList<>();
        for (var rt : roomTypes) {
            RoomTypeDTO dto = RoomTypeDTO.fromEntity(rt);
            // Lấy giá BASE
            RoomPricing pricing = roomPricingRepositoty.findFirstByRoomType_IdAndLoaiGia(rt.getId(), RoomPricing.LoaiGia.BASE);
            if (pricing != null) {
                dto.setGiaNgay(pricing.getGiaNgay());
                dto.setGiaGio(pricing.getGiaGio());
                dto.setGiaQuaDem(pricing.getGiaQuaDem());
            }
            // Lấy ảnh
            var images = roomImagesRepositoty.findByLoaiPhong_Id(rt.getId());
            List<String> urls = new ArrayList<>();
            for (var img : images) {
                urls.add(img.getUrlHinhAnh());
            }
            dto.setImageUrls(urls);
            result.add(dto);
        }
        return result;
    }

    public RoomTypeDTO getRoomTypeDTOById(Integer id) {
        var rtOpt = roomTypeRepository.findById(id);
        if (rtOpt.isEmpty()) return null;
        var rt = rtOpt.get();
        RoomTypeDTO dto = RoomTypeDTO.fromEntity(rt);
        // Lấy giá BASE
        RoomPricing pricing = roomPricingRepositoty.findFirstByRoomType_IdAndLoaiGia(rt.getId(), panacea.website_dat_lich_khach_san.entity.RoomPricing.LoaiGia.BASE);
        if (pricing != null) {
            dto.setGiaNgay(pricing.getGiaNgay());
            dto.setGiaGio(pricing.getGiaGio());
            dto.setGiaQuaDem(pricing.getGiaQuaDem());
        }
        // Log debug giá
        System.out.println("[DEBUG] Giá phòng DTO: id=" + id + ", giaNgay=" + dto.getGiaNgay() + ", giaGio=" + dto.getGiaGio() + ", giaQuaDem=" + dto.getGiaQuaDem());
        // Lấy ảnh
        var images = roomImagesRepositoty.findByLoaiPhong_Id(rt.getId());
        List<String> urls = new ArrayList<>();
        for (var img : images) {
            urls.add(img.getUrlHinhAnh());
        }
        dto.setImageUrls(urls);
        return dto;
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