package panacea.website_dat_lich_khach_san.core.KhachHang.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.entity.Customer;
import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.entity.RoomType;
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

import panacea.website_dat_lich_khach_san.repository.RoomTypeRepository;
import panacea.website_dat_lich_khach_san.repository.RoomPricingRepositoty;
import panacea.website_dat_lich_khach_san.entity.RoomPricing;


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
import java.util.List;
import java.util.Arrays;
import java.util.Optional;

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
            // Kiểm tra loại booking: single room hoặc multiple room types
            if ("multiple".equals(dto.getRoomSelection()) && dto.getMultiRoomTypes() != null && !dto.getMultiRoomTypes().isEmpty()) {
                return datPhongComboChoKhachHang(dto);
            }
            
            // Logic cũ cho single room type
            var roomType = roomTypeRepository.findById(dto.getRoomTypeId()).orElse(null);
            if (roomType == null) return false;

            // Tạo khách hàng nếu chưa có (giả sử theo email)
            Customer customer = customerRepository.findByEmail(dto.getEmailKhach()).orElseGet(() -> {
                Customer c = new Customer();
                c.setHo(dto.getHoKhach());
                c.setTen(dto.getTenKhach());
                c.setEmail(dto.getEmailKhach());
                c.setSoDienThoai(dto.getSoDienThoai());
                c.setSoCmndCccd(dto.getSoCmndCccd());
                c.setDiaChi(dto.getDiaChi());
                c.setNgaySinh(dto.getNgaySinh());
                if (dto.getGioiTinh() != null && !dto.getGioiTinh().isEmpty()) {
                    c.setGioiTinh("NAM".equals(dto.getGioiTinh()) ? Customer.GioiTinh.NAM : Customer.GioiTinh.NU);
                }
                c.setQuocTich(dto.getQuocTich());
                c.setMaKhachHang("KH" + System.currentTimeMillis());
                c.setMatKhauHash("default");
                return customerRepository.save(c);
            });

            // Get hotel (assuming single hotel model)
            Hotel hotel = hotelRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách sạn"));

            Booking booking = new Booking();
            booking.setKhachHang(customer);
            booking.setHotel(hotel);
            booking.setNgayNhanPhong(dto.getNgayNhanPhong());
            booking.setNgayTraPhong(dto.getNgayTraPhong());
            booking.setSoNguoiLon(dto.getSoNguoiLon());
            booking.setSoTreEm(dto.getSoTreEm());
            booking.setGhiChuKhachHang(dto.getGhiChuKhachHang());
            booking.setYeuCauDacBiet(dto.getYeuCauDacBiet());
            booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.CHO_XAC_NHAN);
            booking.setNgayDat(LocalDateTime.now());
            // Lưu loại phòng khách chọn
            booking.setRoomType(roomType);
            
            // Kiểm tra logic số giường vs số người lớn và số phòng
            int soNguoiLon = dto.getSoNguoiLon() != null ? dto.getSoNguoiLon() : 0;
            int soTreEm = dto.getSoTreEm() != null ? dto.getSoTreEm() : 0;
            int soGiuong = dto.getSoGiuong() != null ? dto.getSoGiuong() : 1;
            int soPhong = dto.getSoPhong() != null ? dto.getSoPhong() : 1;
            
            String ghiChuHeThong = "";
            
            // Cảnh báo nếu 2+ người lớn nhưng chọn 1 giường
            if (soNguoiLon >= 2 && soGiuong == 1) {
                ghiChuHeThong += "LƯU Ý: Khách đặt " + soNguoiLon + " người lớn nhưng chọn 1 giường - có thể cần tư vấn thêm. ";
            }
            
            // Kiểm tra sức chứa và số phòng
            int tongSoKhach = soNguoiLon + soTreEm;
            int sucChuaToiDa = 2; // Sức chứa tối đa mỗi phòng
            int soPhongCanThiet = (int) Math.ceil((double) tongSoKhach / sucChuaToiDa);
            
            if (soPhong < soPhongCanThiet) {
                ghiChuHeThong += String.format("CẢNH BÁO: %d khách cần ít nhất %d phòng nhưng chỉ đặt %d phòng. ", 
                    tongSoKhach, soPhongCanThiet, soPhong);
            }
            
            if (soPhong > 1) {
                ghiChuHeThong += String.format("Khách đặt %d phòng cùng loại. ", soPhong);
            }
            
            // Logic kiểm tra sức chứa đã được xử lý ở trên
            
            // Gán ghi chú hệ thống vào booking
            if (!ghiChuHeThong.isEmpty()) {
                if (dto.getGhiChuKhachHang() != null && !dto.getGhiChuKhachHang().trim().isEmpty()) {
                    booking.setGhiChuKhachHang(ghiChuHeThong + dto.getGhiChuKhachHang());
                } else {
                    booking.setGhiChuKhachHang(ghiChuHeThong + "Nhân viên sẽ liên hệ để xác nhận.");
                }
            }
            
            // Sinh mã đặt phòng tự động
            String maDatPhong = "BOOK" + System.currentTimeMillis();
            booking.setMaDatPhong(maDatPhong);
            // --- TÍNH GIÁ THEO LOẠI THUÊ ---
            String bookingType = dto.getBookingType();
            String loaiGia = dto.getLoaiGia(); // Loại giá từ frontend: giaNgay, giaGio, giaQuaDem
            Integer bookingQuantity = dto.getBookingQuantity() != null && dto.getBookingQuantity() > 0 ? dto.getBookingQuantity() : 1;
            BigDecimal unitPrice = BigDecimal.ZERO;
            
            // Lấy thông tin giá từ RoomPricing
            RoomPricing pricing = roomPricingRepositoty.findFirstByRoomType_IdAndLoaiGia(roomType.getId(), RoomPricing.LoaiGia.BASE);
            if (pricing != null) {
                // Ưu tiên sử dụng loaiGia từ frontend nếu có
                if (loaiGia != null && !loaiGia.isEmpty()) {
                    if ("giaNgay".equals(loaiGia)) unitPrice = pricing.getGiaNgay();
                    else if ("giaGio".equals(loaiGia)) unitPrice = pricing.getGiaGio();
                    else if ("giaQuaDem".equals(loaiGia)) unitPrice = pricing.getGiaQuaDem();
                } else {
                    // Fallback về logic cũ nếu không có loaiGia
                    if ("ngay".equals(bookingType)) unitPrice = pricing.getGiaNgay();
                    else if ("gio".equals(bookingType)) unitPrice = pricing.getGiaGio();
                    else if ("dem".equals(bookingType)) unitPrice = pricing.getGiaQuaDem();
                }
            }
            // Tính tổng tiền cho nhiều phòng
            BigDecimal tongTienPhong = unitPrice.multiply(BigDecimal.valueOf(bookingQuantity)).multiply(BigDecimal.valueOf(soPhong));
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
                    dichVuHtml.toString(),

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

    public List<RoomTypeDTO> getAllRoomTypesForCustomer() {
        List<panacea.website_dat_lich_khach_san.entity.RoomType> roomTypes = roomTypeRepository.findAll();
        List<RoomTypeDTO> result = new ArrayList<>();
        for (var rt : roomTypes) {
            // Debug log để kiểm tra dienTich
            System.out.println("[DEBUG] RoomType ID: " + rt.getId() + ", Ten: " + rt.getTenLoaiPhong() + ", DienTich: " + rt.getDienTich());
            
            // Lấy giá BASE
            RoomPricing pricing = roomPricingRepositoty.findFirstByRoomType_IdAndLoaiGia(rt.getId(), RoomPricing.LoaiGia.BASE);
            List<RoomPricing> pricings = pricing != null ? List.of(pricing) : new ArrayList<>();
            
            RoomTypeDTO dto = RoomTypeDTO.fromEntityWithPricing(rt, pricings);
            
            // Debug log sau khi convert
            System.out.println("[DEBUG] DTO DienTich: " + dto.getDienTich());
            
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
        
        // Debug log để kiểm tra dienTich
        System.out.println("[DEBUG] Single RoomType ID: " + rt.getId() + ", Ten: " + rt.getTenLoaiPhong() + ", DienTich: " + rt.getDienTich());
        
        // Lấy giá BASE
        RoomPricing pricing = roomPricingRepositoty.findFirstByRoomType_IdAndLoaiGia(rt.getId(), panacea.website_dat_lich_khach_san.entity.RoomPricing.LoaiGia.BASE);
        List<RoomPricing> pricings = pricing != null ? List.of(pricing) : new ArrayList<>();
        
        RoomTypeDTO dto = RoomTypeDTO.fromEntityWithPricing(rt, pricings);
        
        // Debug log sau khi convert
        System.out.println("[DEBUG] Single DTO DienTich: " + dto.getDienTich());
        
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

    /**
     * Lấy danh sách 4 loại phòng chính cho khách hàng (Standard, Superior, Deluxe, Suite)
     */
    public List<RoomType> getMainRoomTypes() {
        // Giả sử mã loại phòng là: STANDARD, SUPERIOR, DELUXE, SUITE
        List<String> mainCodes = Arrays.asList("STANDARD", "SUPERIOR", "DELUXE", "SUITE");
        return roomTypeRepository.findAll().stream()
                .filter(rt -> mainCodes.contains(rt.getMaLoaiPhong()))
                .toList();
    }

    /**
     * Lấy chi tiết loại phòng theo id
     */
    public RoomType getRoomTypeById(Integer id) {
        Optional<RoomType> opt = roomTypeRepository.findById(id);
        return opt.orElse(null);
    }

    /**
     * Lấy giá cơ bản (BASE) của loại phòng
     */
    public java.math.BigDecimal getRoomBasePriceByRoomTypeId(Integer roomTypeId) {
        return roomPricingRepositoty.findFirstByRoomTypeIdAndLoaiGiaOrderByNgayBatDauDesc(roomTypeId, RoomPricing.LoaiGia.BASE)
                .map(RoomPricing::getGiaTri)
                .orElse(null);
    }

    public List<Booking> getBookingsByEmail(String email) {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) {
            return bookingRepository.findByKhachHang(customer.get());
        }
        return new ArrayList<>();
    }
    
    /**
     * Xử lý đặt phòng combo nhiều loại phòng khác nhau
     */
    public boolean datPhongComboChoKhachHang(BookingRequestDTO dto) {
        try {
            // Tạo khách hàng nếu chưa có
            Customer customer = customerRepository.findByEmail(dto.getEmailKhach()).orElseGet(() -> {
                Customer c = new Customer();
                c.setHo(dto.getHoKhach());
                c.setTen(dto.getTenKhach());
                c.setEmail(dto.getEmailKhach());
                c.setSoDienThoai(dto.getSoDienThoai());
                c.setSoCmndCccd(dto.getSoCmndCccd());
                c.setDiaChi(dto.getDiaChi());
                c.setNgaySinh(dto.getNgaySinh());
                if (dto.getGioiTinh() != null && !dto.getGioiTinh().isEmpty()) {
                    c.setGioiTinh("NAM".equals(dto.getGioiTinh()) ? Customer.GioiTinh.NAM : Customer.GioiTinh.NU);
                }
                c.setQuocTich(dto.getQuocTich());
                c.setMaKhachHang("KH" + System.currentTimeMillis());
                c.setMatKhauHash("default");
                return customerRepository.save(c);
            });
            
            // Get hotel (assuming single hotel model)
            Hotel hotel = hotelRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách sạn"));

            // Tạo booking chính cho combo
            Booking mainBooking = new Booking();
            mainBooking.setKhachHang(customer);
            mainBooking.setHotel(hotel);
            mainBooking.setNgayNhanPhong(dto.getNgayNhanPhong());
            mainBooking.setNgayTraPhong(dto.getNgayTraPhong());
            mainBooking.setSoNguoiLon(dto.getSoNguoiLon());
            mainBooking.setSoTreEm(dto.getSoTreEm());
            mainBooking.setGhiChuKhachHang(dto.getGhiChuKhachHang());
            mainBooking.setYeuCauDacBiet(dto.getYeuCauDacBiet());
            mainBooking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.CHO_XAC_NHAN);
            mainBooking.setNgayDat(LocalDateTime.now());
            
            // Sinh mã đặt phòng cho combo
            String maDatPhong = "COMBO" + System.currentTimeMillis();
            mainBooking.setMaDatPhong(maDatPhong);
            
            // Tính tổng tiền cho tất cả các loại phòng
            BigDecimal tongTienCombo = BigDecimal.ZERO;
            StringBuilder ghiChuCombo = new StringBuilder("COMBO PHÒNG: ");
            
            for (BookingRequestDTO.MultiRoomTypeRequest roomTypeReq : dto.getMultiRoomTypes()) {
                // Tìm room type theo tên
                RoomType roomType = findRoomTypeByName(roomTypeReq.getRoomType());
                if (roomType == null) {
                    continue; // Bỏ qua nếu không tìm thấy loại phòng
                }
                
                // Lấy giá của loại phòng này
                BigDecimal unitPrice = BigDecimal.valueOf(roomTypeReq.getPrice() != null ? roomTypeReq.getPrice() : 0);
                Integer quantity = roomTypeReq.getQuantity() != null ? roomTypeReq.getQuantity() : 1;
                
                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
                tongTienCombo = tongTienCombo.add(subtotal);
                
                // Thêm vào ghi chú
                ghiChuCombo.append(String.format("%d x %s (%.0f VNĐ), ", 
                    quantity, roomType.getTenLoaiPhong(), unitPrice));
            }
            
            // Set thông tin tổng cho booking chính
            mainBooking.setTongTienPhong(tongTienCombo);
            mainBooking.setTongThanhToan(tongTienCombo);
            BigDecimal tienDatCoc = tongTienCombo.divide(BigDecimal.valueOf(2), 0, java.math.RoundingMode.HALF_UP);
            mainBooking.setTienDatCoc(tienDatCoc);
            
            // Cập nhật ghi chú
            String finalNote = ghiChuCombo.toString();
            if (finalNote.endsWith(", ")) {
                finalNote = finalNote.substring(0, finalNote.length() - 2);
            }
            if (dto.getGhiChuKhachHang() != null && !dto.getGhiChuKhachHang().trim().isEmpty()) {
                mainBooking.setGhiChuKhachHang(finalNote + ". " + dto.getGhiChuKhachHang());
            } else {
                mainBooking.setGhiChuKhachHang(finalNote + ". Nhân viên sẽ liên hệ để xác nhận chi tiết.");
            }
            
            // Lưu booking
            bookingRepository.save(mainBooking);
            
            // Gửi email thông báo (có thể tùy chỉnh template cho combo)
            sendComboBookingEmail(dto, customer, tongTienCombo, finalNote);
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Tìm room type theo tên (single, double, suite)
     */
    private RoomType findRoomTypeByName(String roomTypeName) {
        switch (roomTypeName.toLowerCase()) {
            case "single":
                return roomTypeRepository.findByMaLoaiPhong("STANDARD").orElse(null);
            case "double":
                return roomTypeRepository.findByMaLoaiPhong("SUPERIOR").orElse(null);
            case "suite":
                return roomTypeRepository.findByMaLoaiPhong("SUITE").orElse(null);
            default:
                return null;
        }
    }
    
    /**
     * Gửi email cho booking combo
     */
    private void sendComboBookingEmail(BookingRequestDTO dto, Customer customer, BigDecimal tongTien, String comboDetails) {
        if (mailSender != null) {
            try {
                String subject = "[Panacea Hotel] Yêu cầu đặt combo phòng đã được gửi";
                String text = String.format(
                    "<h2>Cảm ơn %s đã đặt combo phòng tại Panacea Hotel!</h2>" +
                    "<p>Thông tin đặt phòng combo của bạn:</p>" +
                    "<ul>" +
                    "<li>Khách sạn: Panacea Hotel</li>" +
                    "<li>Combo phòng: %s</li>" +
                    "<li>Ngày nhận phòng: %s</li>" +
                    "<li>Ngày trả phòng: %s</li>" +
                    "<li>Số người lớn: %d</li>" +
                    "<li>Số trẻ em: %d</li>" +
                    "<li>Tổng tiền: %s VNĐ</li>" +
                    "<li>Ghi chú: %s</li>" +
                    "</ul>" +
                    "<p>Nhân viên sẽ liên hệ để xác nhận chi tiết và hướng dẫn thanh toán.</p>" +
                    "<p>Cảm ơn bạn đã tin tưởng Panacea Hotel!</p>",
                    dto.getTenKhach(),
                    comboDetails,
                    dto.getNgayNhanPhong(),
                    dto.getNgayTraPhong(),
                    dto.getSoNguoiLon(),
                    dto.getSoTreEm(),
                    new java.text.DecimalFormat("#,###").format(tongTien),
                    dto.getGhiChuKhachHang() != null ? dto.getGhiChuKhachHang() : "Không có"
                );
                
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(dto.getEmailKhach());
                helper.setSubject(subject);
                helper.setText(text, true);
                helper.setFrom("noreply@panaceahotel.com");
                
                mailSender.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}