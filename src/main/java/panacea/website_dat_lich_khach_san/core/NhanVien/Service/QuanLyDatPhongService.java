package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.entity.BookingDetail;
import panacea.website_dat_lich_khach_san.entity.Customer;
import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.repository.BookingDetailRepository;
import panacea.website_dat_lich_khach_san.repository.CustomerRepository;
import panacea.website_dat_lich_khach_san.repository.HotelRepository;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageImpl;
import java.util.stream.Collectors;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Scheduled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class QuanLyDatPhongService {
    private static final Logger logger = LoggerFactory.getLogger(QuanLyDatPhongService.class);
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private HotelRepository hotelRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    public String getStaffName() {
        return "Nguyễn Văn A";
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }
    
    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }
    
    public boolean confirmBooking(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_XAC_NHAN);
            booking.setNgayXacNhan(LocalDateTime.now());
            bookingRepository.save(booking);
            
            // Gửi email thông báo xác nhận cho khách hàng
            sendConfirmationEmail(booking);
            
            return true;
        }
        return false;
    }
    
    public boolean cancelBooking(Long bookingId) {
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_HUY);
            booking.setNgayHuy(LocalDateTime.now());
            bookingRepository.save(booking);
            
            // Gửi email thông báo hủy cho khách hàng
            sendCancellationEmail(booking);
            
            return true;
        }
        return false;
    }
    
    private void sendConfirmationEmail(Booking booking) {
        if (mailSender == null) return;
        
        try {
            String subject = "Xác nhận đặt phòng thành công - Panacea Hotel";
            String content = String.format("""
                <html>
                <body>
                    <h2>Xin chào %s!</h2>
                    <p>Chúng tôi rất vui mừng thông báo rằng đặt phòng của bạn đã được xác nhận thành công.</p>
                    
                    <h3>Thông tin đặt phòng:</h3>
                    <ul>
                        <li><strong>Mã đặt phòng:</strong> %s</li>
                        <li><strong>Khách sạn:</strong> %s</li>
                        <li><strong>Ngày nhận phòng:</strong> %s</li>
                        <li><strong>Ngày trả phòng:</strong> %s</li>
                        <li><strong>Số người lớn:</strong> %d</li>
                        <li><strong>Số trẻ em:</strong> %d</li>
                        <li><strong>Tổng thanh toán:</strong> %,.0f VND</li>
                    </ul>
                    
                    <p>Vui lòng đến khách sạn đúng giờ để làm thủ tục check-in. Nếu có bất kỳ thay đổi nào, vui lòng liên hệ với chúng tôi.</p>
                    
                    <p>Trân trọng,<br>Đội ngũ Panacea Hotel</p>
                </body>
                </html>
                """, 
                booking.getKhachHang().getHo() + " " + booking.getKhachHang().getTen(),
                booking.getMaDatPhong(),
                booking.getHotel().getTenKhachSan(),
                booking.getNgayNhanPhong(),
                booking.getNgayTraPhong(),
                booking.getSoNguoiLon(),
                booking.getSoTreEm(),
                booking.getTongThanhToan()
            );
            
            sendEmail(booking.getKhachHang().getEmail(), subject, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void sendCancellationEmail(Booking booking) {
        if (mailSender == null) return;
        
        try {
            String subject = "Thông báo hủy đặt phòng - Panacea Hotel";
            String content = String.format("""
                <html>
                <body>
                    <h2>Xin chào %s!</h2>
                    <p>Chúng tôi rất tiếc phải thông báo rằng đặt phòng của bạn đã bị hủy.</p>
                    
                    <h3>Thông tin đặt phòng đã hủy:</h3>
                    <ul>
                        <li><strong>Mã đặt phòng:</strong> %s</li>
                        <li><strong>Khách sạn:</strong> %s</li>
                        <li><strong>Ngày nhận phòng:</strong> %s</li>
                        <li><strong>Ngày trả phòng:</strong> %s</li>
                    </ul>
                    
                    <p>Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi.</p>
                    
                    <p>Trân trọng,<br>Đội ngũ Panacea Hotel</p>
                </body>
                </html>
                """, 
                booking.getKhachHang().getHo() + " " + booking.getKhachHang().getTen(),
                booking.getMaDatPhong(),
                booking.getHotel().getTenKhachSan(),
                booking.getNgayNhanPhong(),
                booking.getNgayTraPhong()
            );
            
            sendEmail(booking.getKhachHang().getEmail(), subject, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void sendEmail(String to, String subject, String htmlContent) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true);
        mailSender.send(message);
    }
    
    public boolean addCustomerBooking(Map<String, Object> requestData) {
        try {
            // Extract customer data
            @SuppressWarnings("unchecked")
            Map<String, Object> customerData = (Map<String, Object>) requestData.get("customer");
            
            // Create or find customer
            Customer customer = new Customer();
            customer.setHo((String) customerData.get("ho"));
            customer.setTen((String) customerData.get("ten"));
            customer.setEmail((String) customerData.get("email"));
            customer.setSoDienThoai((String) customerData.get("soDienThoai"));
            customer.setSoCmndCccd((String) customerData.get("soCmndCccd"));
            customer.setDiaChi((String) customerData.get("diaChi"));
            customer.setTrangThai(Customer.TrangThaiCustomer.HOAT_DONG);
            customer.setLoaiKhachHang(panacea.website_dat_lich_khach_san.infrastructure.Enums.LoaiKhachHang.CA_NHAN);
            customer.setDiemTichLuy(0);
            customer.setMatKhauHash("default_password_hash"); // Temporary password
            
            // Generate customer code
            String maKhachHang = "KH" + System.currentTimeMillis();
            customer.setMaKhachHang(maKhachHang);
            
            customer = customerRepository.save(customer);
            
            // Get hotel and room
            Integer hotelId = Integer.valueOf(requestData.get("hotelId").toString());
            Long roomId = Long.valueOf(requestData.get("roomId").toString());
            
            Hotel hotel = hotelRepository.findById(hotelId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách sạn"));
            
            Room room = roomRepository.findById(roomId.intValue())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));
            
            // Create booking
            Booking booking = new Booking();
            booking.setKhachHang(customer);
            booking.setHotel(hotel);
            booking.setNgayNhanPhong(LocalDate.parse(requestData.get("ngayNhanPhong").toString()));
            booking.setNgayTraPhong(LocalDate.parse(requestData.get("ngayTraPhong").toString()));
            booking.setSoNguoiLon(Byte.valueOf(requestData.get("soNguoiLon").toString()));
            booking.setSoTreEm(Byte.valueOf(requestData.get("soTreEm").toString()));
            booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.CHO_XAC_NHAN);
            booking.setNgayDat(LocalDateTime.now());
            
            // Generate booking code
            String maDatPhong = "BP" + System.currentTimeMillis();
            booking.setMaDatPhong(maDatPhong);
            
            // Calculate total payment
            long days = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.parse(requestData.get("ngayNhanPhong").toString()),
                LocalDate.parse(requestData.get("ngayTraPhong").toString())
            );
            BigDecimal tongThanhToan = room.getGiaCoBan().multiply(BigDecimal.valueOf(days));
            booking.setTongThanhToan(tongThanhToan);
            
            bookingRepository.save(booking);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean confirmBookingAndAssignRoom(Long bookingId, Long roomId) {
        try {
            Booking booking = bookingRepository.findById(bookingId).orElse(null);
            Room room = roomRepository.findById(roomId.intValue()).orElse(null);
            if (booking == null || room == null) return false;
            // Tạo BookingDetail
            BookingDetail detail = new BookingDetail();
            detail.setDatPhongId(booking.getId());
            detail.setPhongId(room.getId());
            bookingDetailRepository.save(detail);
            // Cập nhật trạng thái
            booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_XAC_NHAN);
            room.setTrangThai(Room.TrangThaiPhong.DANG_SU_DUNG);
            bookingRepository.save(booking);
            roomRepository.save(room);
            // Gửi email xác nhận
            Customer customer = booking.getKhachHang();
            if (customer != null && customer.getEmail() != null) {
                String emailBody = String.format(
                    "Xin chào %s!\n\n" +
                    "Chúng tôi rất vui mừng thông báo rằng đặt phòng của bạn đã được xác nhận thành công.\n\n" +
                    "Thông tin đặt phòng:\n" +
                    "Mã đặt phòng: %s\n" +
                    "Khách sạn: %s\n" +
                    "Phòng: %s\n" +
                    "Ngày nhận phòng: %s\n" +
                    "Ngày trả phòng: %s\n" +
                    "Số người lớn: %d\n" +
                    "Số trẻ em: %d\n" +
                    "Tổng thanh toán: %s VND\n\n" +
                    "Vui lòng đến khách sạn đúng giờ để làm thủ tục check-in. Nếu có bất kỳ thay đổi nào, vui lòng liên hệ với chúng tôi.\n\n" +
                    "Trân trọng,\n" +
                    "Đội ngũ Panacea Hotel",
                    customer.getHo() + " " + customer.getTen(),
                    booking.getMaDatPhong(),
                    booking.getHotel().getTenKhachSan(),
                    room.getSoPhong(),
                    booking.getNgayNhanPhong(),
                    booking.getNgayTraPhong(),
                    booking.getSoNguoiLon(),
                    booking.getSoTreEm(),
                    booking.getTongThanhToan() != null ? booking.getTongThanhToan().toString() : "0"
                );
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(customer.getEmail());
                message.setSubject("Xác nhận đặt phòng khách sạn");
                message.setText(emailBody);
                mailSender.send(message);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkoutBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) return false;
        if (booking.getTrangThaiDatPhong() != Booking.TrangThaiDatPhong.DA_NHAN_PHONG
            && booking.getTrangThaiDatPhong() != Booking.TrangThaiDatPhong.DA_XAC_NHAN) return false;
        // Cập nhật trạng thái booking
        booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_HOAN_THANH);
        booking.setTrangThaiThanhToan(Booking.TrangThaiThanhToan.DA_THANH_TOAN);
        bookingRepository.save(booking);
        // Cập nhật trạng thái phòng
        java.util.List<panacea.website_dat_lich_khach_san.entity.BookingDetail> details = bookingDetailRepository.findByDatPhongId(booking.getId());
        for (panacea.website_dat_lich_khach_san.entity.BookingDetail detail : details) {
            if (detail.getPhongId() != null) {
                Room room = roomRepository.findById(detail.getPhongId()).orElse(null);
                if (room != null) {
                    room.setTrangThai(Room.TrangThaiPhong.SAN_SANG);
                    roomRepository.save(room);
                }
            }
        }
        // Gửi email cảm ơn
        try {
            LocalDate expiry = LocalDate.now().plusDays(30);
            sendThankYouEmail(booking, expiry);
        } catch (Exception e) {
            logger.error("Lỗi gửi email cảm ơn sau checkout: {}", e.getMessage(), e);
        }
        return true;
    }

    private void sendThankYouEmail(Booking booking, LocalDate expiryDate) {
        if (mailSender == null) return;
        Customer customer = booking.getKhachHang();
        if (customer == null || customer.getEmail() == null) return;
        String subject = "Khách sạn Panacea xin gửi lời cảm ơn chân thành đến Quý khách!";
        String content = String.format("""
            <html><body>
            <p>Kính gửi Quý khách <b>%s</b>,</p>
            <p>Thay mặt toàn thể đội ngũ nhân viên Khách sạn Panacea, chúng tôi xin gửi lời cảm ơn chân thành nhất đến Quý khách đã tin tưởng lựa chọn khách sạn chúng tôi làm điểm dừng chân trong chuyến đi vừa qua.</p>
            <p>Chúng tôi hy vọng Quý khách đã có những trải nghiệm thật sự thoải mái, thư giãn và đáng nhớ tại Panacea. Sự hài lòng của Quý khách là niềm vinh hạnh và là nguồn động lực lớn nhất để chúng tôi không ngừng nỗ lực nâng cao chất lượng dịch vụ mỗi ngày.</p>
            <p>Để bày tỏ lòng tri ân và mong được chào đón Quý khách trở lại trong những lần tiếp theo, Khách sạn Panacea xin gửi tặng Quý khách mã ưu đãi <b>PANACEA15</b> giảm giá 15%% cho lần đặt phòng kế tiếp.<br/>(Lưu ý: Mã ưu đãi có hiệu lực đến ngày <b>%s</b>)</p>
            <p>Chúng tôi sẽ vô cùng biết ơn nếu Quý khách có thể dành một vài phút để lại đánh giá về trải nghiệm của mình trên trang <a href=\"https://www.tripadvisor.com/Hotel_Review-g293925-d25279460-Reviews-Panacea_Hotel-Ho_Chi_Minh_City.html\" target=\"_blank\">TripAdvisor</a> hoặc <a href=\"https://goo.gl/maps/your-google-maps-link\" target=\"_blank\">Google</a>. Những góp ý của Quý khách là vô giá để chúng tôi hoàn thiện hơn.</p>
            <p>Một lần nữa, xin chân thành cảm ơn Quý khách. Chúc Quý khách một ngày tốt lành và mong sớm được phục vụ Quý khách trong tương lai không xa.</p>
            <br/>
            <p>Trân trọng,</p>
            <b>Nguyễn Văn A</b><br/>
            Giám đốc Quan hệ Khách hàng<br/>
            Khách sạn Panacea<br/>
            Địa chỉ: 123 Đường ABC, Quận 1, TP. Hồ Chí Minh<br/>
            Điện thoại: 0123 456 789<br/>
            Website: <a href=\"https://panacea-hotel.com\" target=\"_blank\">https://panacea-hotel.com</a>
            </body></html>
            """,
            customer.getHo() + " " + customer.getTen(),
            expiryDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );
        try {
            sendEmail(customer.getEmail(), subject, content);
        } catch (Exception e) {
            logger.error("Lỗi gửi email cảm ơn: {}", e.getMessage(), e);
        }
    }

    public Page<Booking> getPagedBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    public Page<Booking> filterBookings(String keyword, java.time.LocalDate ngayNhan, Pageable pageable) {
        java.util.List<Booking> all = bookingRepository.findAll();
        java.util.List<Booking> filtered = all.stream()
            .filter(b -> {
                boolean match = true;
                if (keyword != null && !keyword.isBlank()) {
                    String lower = keyword.toLowerCase();
                    match &= (b.getKhachHang().getHo() + " " + b.getKhachHang().getTen()).toLowerCase().contains(lower)
                        || (b.getKhachHang().getEmail() != null && b.getKhachHang().getEmail().toLowerCase().contains(lower))
                        || (b.getKhachHang().getSoDienThoai() != null && b.getKhachHang().getSoDienThoai().toLowerCase().contains(lower));
                }
                if (ngayNhan != null) {
                    match &= b.getNgayNhanPhong() != null && b.getNgayNhanPhong().isEqual(ngayNhan);
                }
                return match;
            })
            .collect(Collectors.toList());
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        java.util.List<Booking> pageContent = start > end ? java.util.Collections.emptyList() : filtered.subList(start, end);
        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    public List<Room> getAvailableRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelIdAndTrangThai(hotelId.intValue(), Room.TrangThaiPhong.SAN_SANG);
    }

    // Tự động hủy booking chưa thanh toán sau 1 ngày
    @Scheduled(cron = "0 0 * * * *") // mỗi giờ (hoặc chỉnh lại cho test)
    public void autoCancelUnpaidBookings() {
        logger.info("[AutoCancel] Bắt đầu kiểm tra booking chưa thanh toán...");
        java.time.Instant now = java.time.Instant.now();
        java.util.List<Booking> unpaidBookings = bookingRepository.findByTrangThaiDatPhong(Booking.TrangThaiDatPhong.CHO_XAC_NHAN);
        for (Booking booking : unpaidBookings) {
            java.time.Instant created = null;
            if (booking.getCreatedDate() != null) {
                created = java.time.Instant.ofEpochMilli(booking.getCreatedDate());
            } else if (booking.getNgayDat() != null) {
                created = booking.getNgayDat().atZone(java.time.ZoneId.systemDefault()).toInstant();
            }
            logger.info("[AutoCancel] Kiểm tra booking {} - created: {} - trạng thái: {}", booking.getMaDatPhong(), created, booking.getTrangThaiDatPhong());
            if (created == null) {
                logger.warn("[AutoCancel] Booking {} không có thời gian tạo, bỏ qua!", booking.getMaDatPhong());
                continue;
            }
            if (created.plus(java.time.Duration.ofMinutes(1)).isBefore(now)) {
                booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_HUY);
                booking.setLyDoHuy("Tự động hủy do quá hạn thanh toán (1 phút test)");
                booking.setNgayHuy(java.time.LocalDateTime.now());
                bookingRepository.save(booking);
                logger.info("[AutoCancel] Đã hủy booking {} do quá hạn thanh toán!", booking.getMaDatPhong());
                // Gửi email thông báo hủy nếu có
                try {
                    Customer customer = booking.getKhachHang();
                    if (customer != null && customer.getEmail() != null) {
                        String emailBody = String.format(
                            "Xin chào %s!\n\n" +
                            "Đặt phòng của bạn (mã: %s) đã bị hủy do không thanh toán trong vòng 1 phút (test).\n" +
                            "Nếu có thắc mắc, vui lòng liên hệ khách sạn.\n\nTrân trọng,\nPanacea Hotel",
                            customer.getHo() + " " + customer.getTen(),
                            booking.getMaDatPhong()
                        );
                        org.springframework.mail.SimpleMailMessage message = new org.springframework.mail.SimpleMailMessage();
                        message.setTo(customer.getEmail());
                        message.setSubject("Đặt phòng bị hủy do quá hạn thanh toán (test)");
                        message.setText(emailBody);
                        mailSender.send(message);
                        logger.info("[AutoCancel] Đã gửi email hủy booking {} tới {}", booking.getMaDatPhong(), customer.getEmail());
                    } else {
                        logger.warn("[AutoCancel] Booking {} không có email khách hàng!", booking.getMaDatPhong());
                    }
                } catch (Exception ex) {
                    logger.error("[AutoCancel] Lỗi gửi email cho booking {}: {}", booking.getMaDatPhong(), ex.getMessage(), ex);
                }
            } else {
                logger.info("[AutoCancel] Booking {} chưa quá hạn, chưa hủy.", booking.getMaDatPhong());
            }
        }
        logger.info("[AutoCancel] Kết thúc kiểm tra booking chưa thanh toán.");
    }
} 