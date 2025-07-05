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
import org.springframework.mail.SimpleMailMessage;

@Service
public class QuanLyDatPhongService {
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
            
            Room room = roomRepository.findById(roomId)
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
            Room room = roomRepository.findById(roomId).orElse(null);
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

    public Page<Booking> getPagedBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    public List<Room> getAvailableRoomsByHotel(Long hotelId) {
        return roomRepository.findByHotelIdAndTrangThai(hotelId.intValue(), Room.TrangThaiPhong.SAN_SANG);
    }
} 