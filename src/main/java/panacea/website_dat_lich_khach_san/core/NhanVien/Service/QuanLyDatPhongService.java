package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.entity.BookingDetail;
import panacea.website_dat_lich_khach_san.entity.Customer;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.repository.*;
import panacea.website_dat_lich_khach_san.entity.BookingHistory;
import panacea.website_dat_lich_khach_san.repository.ServiceDetailRepository;
import panacea.website_dat_lich_khach_san.entity.ServiceDetail;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.ServiceDetailDTO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import panacea.website_dat_lich_khach_san.infrastructure.DTO.BookingDetailViewDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.CancellationInfoDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.CancellationRequestDTO;
import panacea.website_dat_lich_khach_san.service.CancellationService;
import panacea.website_dat_lich_khach_san.repository.ServiceRepository;

@Service
public class QuanLyDatPhongService {
    private static final Logger logger = LoggerFactory.getLogger(QuanLyDatPhongService.class);
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private BookingDetailRepository bookingDetailRepository;

    @Autowired
    private BookingHistoryRepository bookingHistoryRepository;

    @Autowired
    private ServiceDetailRepository serviceDetailRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private CancellationService cancellationServiceBean;
    
    @Autowired
    private HotelRepository hotelRepository;

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
            
            // Lấy thông tin hủy đặt phòng
            CancellationInfoDTO cancellationInfo;
            try {
                cancellationInfo = cancellationServiceBean.getCancellationInfo(booking.getId().longValue());
            } catch (RuntimeException e) {
                logger.warn("Không thể hủy booking {} - {}", booking.getMaDatPhong(), e.getMessage());
                return false;
            }
            
            // Kiểm tra xem có thể hủy không
            if (!cancellationInfo.isCanCancel()) {
                logger.warn("Không thể hủy booking {} - {}", booking.getMaDatPhong(), cancellationInfo.getCancellationReason());
                return false;
            }
            
            // Thực hiện hủy đặt phòng
            CancellationRequestDTO request = new CancellationRequestDTO();
            request.setBookingId(booking.getId().longValue());
            request.setCancellationReason("Hủy bởi nhân viên");
            
            try {
                cancellationServiceBean.cancelBooking(request);
            } catch (RuntimeException e) {
                logger.error("Lỗi khi hủy booking {}: {}", booking.getMaDatPhong(), e.getMessage());
                return false;
            }
            
            // Gửi email thông báo hủy cho khách hàng với thông tin hoàn tiền
            sendCancellationEmailWithRefund(booking, cancellationInfo);
            
            // Copy sang bảng lịch sử và xóa booking gốc
            try {
                BookingHistory history = new BookingHistory();
                history.setMaDatPhong(booking.getMaDatPhong());
                history.setTenKhachHang(booking.getKhachHang() != null ? booking.getKhachHang().getHo() + " " + booking.getKhachHang().getTen() : null);
                history.setEmail(booking.getKhachHang() != null ? booking.getKhachHang().getEmail() : null);
                history.setSoDienThoai(booking.getKhachHang() != null ? booking.getKhachHang().getSoDienThoai() : null);
                history.setTenKhachSan(null); // Removed booking.getHotel().getTenKhachSan()
                // Lấy số phòng đầu tiên (nếu có)
                java.util.List<panacea.website_dat_lich_khach_san.entity.BookingDetail> details = bookingDetailRepository.findByDatPhongId(booking.getId());
                if (!details.isEmpty()) {
                    Room room = roomRepository.findById(details.get(0).getPhongId()).orElse(null);
                    if (room != null) history.setSoPhong(room.getSoPhong());
                }
                history.setNgayNhanPhong(booking.getNgayNhanPhong());
                history.setNgayTraPhong(booking.getNgayTraPhong());
                history.setSoNguoiLon(booking.getSoNguoiLon());
                history.setSoTreEm(booking.getSoTreEm());
                history.setTongThanhToan(booking.getTongThanhToan());
                history.setNgayDat(booking.getNgayDat());
                history.setNgayHoanThanh(java.time.LocalDateTime.now());
                history.setGhiChu(booking.getGhiChuKhachHang());
                history.setTrangThai("DA_HUY");
                bookingHistoryRepository.save(history);
                bookingRepository.delete(booking);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
                        <li><strong>Khách sạn:</strong> Panacea Hotel</li>
                        <li><strong>Ngày nhận phòng:</strong> %s</li>
                        <li><strong>Ngày trả phòng:</strong> %s</li>
                        <li><strong>Số người lớn:</strong> %d</li>
                        <li><strong>Số trẻ em:</strong> %d</li>
                        <li><strong>Tổng thanh toán:</strong> %,.0f VND</li>
                    </ul>
                    
                    <p>Vui lòng đến Panacea Hotel đúng giờ để làm thủ tục check-in. Nếu có bất kỳ thay đổi nào, vui lòng liên hệ với chúng tôi.</p>
                    
                    <p>Trân trọng,<br>Đội ngũ Panacea Hotel</p>
                </body>
                </html>
                """, 
                booking.getKhachHang().getHo() + " " + booking.getKhachHang().getTen(),
                booking.getMaDatPhong(),
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
                    <p>Chúng tôi rất tiếc phải thông báo rằng đặt phòng của bạn tại Panacea Hotel đã bị hủy.</p>
                    
                    <h3>Thông tin đặt phòng đã hủy:</h3>
                    <ul>
                        <li><strong>Mã đặt phòng:</strong> %s</li>
                        <li><strong>Khách sạn:</strong> Panacea Hotel</li>
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
                booking.getNgayNhanPhong(),
                booking.getNgayTraPhong()
            );
            
            sendEmail(booking.getKhachHang().getEmail(), subject, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void sendCancellationEmailWithRefund(Booking booking, CancellationInfoDTO cancellationInfo) {
        if (mailSender == null) return;
        
        try {
            String subject = "Thông báo hủy đặt phòng và hoàn tiền - Panacea Hotel";
            String content = String.format("""
                <html>
                <body>
                    <h2>Xin chào %s!</h2>
                    <p>Chúng tôi xin thông báo rằng đặt phòng của bạn tại Panacea Hotel đã được hủy thành công.</p>
                    
                    <h3>Thông tin đặt phòng đã hủy:</h3>
                    <ul>
                        <li><strong>Mã đặt phòng:</strong> %s</li>
                        <li><strong>Khách sạn:</strong> Panacea Hotel</li>
                        <li><strong>Ngày nhận phòng:</strong> %s</li>
                        <li><strong>Ngày trả phòng:</strong> %s</li>
                        <li><strong>Tổng tiền đã thanh toán:</strong> %,.0f VND</li>
                    </ul>
                    
                    <h3>Thông tin hoàn tiền:</h3>
                    <ul>
                        <li><strong>Chính sách hủy:</strong> %s</li>
                        <li><strong>Số tiền hoàn lại:</strong> %,.0f VND (%s)</li>
                        <li><strong>Phí hủy:</strong> %,.0f VND</li>
                        <li><strong>Thời gian còn lại đến check-in:</strong> %d giờ</li>
                    </ul>
                    
                    <p>Số tiền hoàn lại sẽ được chuyển về tài khoản của bạn trong vòng 3-5 ngày làm việc.</p>
                    
                    <p>Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi.</p>
                    
                    <p>Trân trọng,<br>Đội ngũ Panacea Hotel</p>
                </body>
                </html>
                """, 
                booking.getKhachHang().getHo() + " " + booking.getKhachHang().getTen(),
                booking.getMaDatPhong(),
                booking.getNgayNhanPhong(),
                booking.getNgayTraPhong(),
                cancellationInfo.getOriginalAmount(),
                cancellationInfo.getCancellationPolicy().getLabel(),
                cancellationInfo.getRefundAmount(),
                cancellationInfo.getCancellationPolicy().getRefundPercentage(),
                cancellationInfo.getCancellationFee(),
                cancellationInfo.getHoursUntilCheckIn()
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
            Customer customer = null;
            String email = (String) customerData.get("email");
            if (email != null && !email.isBlank()) {
                customer = customerRepository.findByEmail(email).orElse(null);
            }
            if (customer == null) {
                customer = new Customer();
                customer.setHo((String) customerData.get("ho"));
                customer.setTen((String) customerData.get("ten"));
                customer.setEmail(email);
                customer.setSoDienThoai((String) customerData.get("soDienThoai"));
                customer.setSoCmndCccd((String) customerData.get("soCmndCccd"));
                customer.setDiaChi((String) customerData.get("diaChi"));
                
                // Set new fields
                String ngaySinhStr = (String) customerData.get("ngaySinh");
                if (ngaySinhStr != null && !ngaySinhStr.isBlank()) {
                    try {
                        customer.setNgaySinh(java.time.LocalDate.parse(ngaySinhStr));
                    } catch (Exception e) {
                        // Handle date parsing error if needed
                    }
                }
                String gioiTinhStr = (String) customerData.get("gioiTinh");
                if (gioiTinhStr != null && !gioiTinhStr.isBlank()) {
                    try {
                        customer.setGioiTinh("NAM".equals(gioiTinhStr) ? Customer.GioiTinh.NAM : Customer.GioiTinh.NU);
                    } catch (Exception e) {
                        // Handle enum conversion error if needed
                    }
                }
                customer.setQuocTich((String) customerData.get("quocTich"));
                customer.setTrangThai(Customer.TrangThaiCustomer.HOAT_DONG);
                customer.setLoaiKhachHang("CA_NHAN");
                customer.setDiemTichLuy(0);
                customer.setMatKhauHash("default_password_hash"); // Temporary password
                // Generate customer code
                String maKhachHang = "KH" + System.currentTimeMillis();
                customer.setMaKhachHang(maKhachHang);
                customer = customerRepository.save(customer);
            }
            
            // Get hotel and rooms
            Object roomIdsObj = requestData.get("roomIds");
            if (roomIdsObj == null) throw new IllegalArgumentException("Thiếu trường roomIds trong requestData");
            
            @SuppressWarnings("unchecked")
            List<Integer> roomIds = (List<Integer>) roomIdsObj;
            if (roomIds.isEmpty()) throw new IllegalArgumentException("Danh sách phòng không được để trống");
            
            // Validate all rooms exist
            List<Room> rooms = new ArrayList<>();
            for (Integer roomId : roomIds) {
                Room room = roomRepository.findById(roomId)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + roomId));
                rooms.add(room);
            }
            
            // Get hotel (assuming single hotel model)
            Hotel hotel = hotelRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách sạn"));

            // Create booking
            Booking booking = new Booking();
            booking.setKhachHang(customer);
            booking.setHotel(hotel);
            Object ngayNhanPhongObj = requestData.get("ngayNhanPhong");
            if (ngayNhanPhongObj == null) throw new IllegalArgumentException("Thiếu trường ngayNhanPhong trong requestData");
            booking.setNgayNhanPhong(LocalDate.parse(ngayNhanPhongObj.toString()));
            Object ngayTraPhongObj = requestData.get("ngayTraPhong");
            if (ngayTraPhongObj == null) throw new IllegalArgumentException("Thiếu trường ngayTraPhong trong requestData");
            booking.setNgayTraPhong(LocalDate.parse(ngayTraPhongObj.toString()));
            Object soNguoiLonObj = requestData.get("soNguoiLon");
            if (soNguoiLonObj == null) throw new IllegalArgumentException("Thiếu trường soNguoiLon trong requestData");
            booking.setSoNguoiLon(Byte.valueOf(soNguoiLonObj.toString()));
            Object soTreEmObj = requestData.get("soTreEm");
            if (soTreEmObj == null) throw new IllegalArgumentException("Thiếu trường soTreEm trong requestData");
            booking.setSoTreEm(Byte.valueOf(soTreEmObj.toString()));
            
            // Xử lý số giường
            Object soGiuongObj = requestData.get("soGiuong");
            if (soGiuongObj != null) {
                // Có thể lưu thông tin số giường vào ghi chú hoặc trường mở rộng
                String ghiChu = booking.getGhiChuNoiBo() != null ? booking.getGhiChuNoiBo() : "";
                ghiChu += (ghiChu.isEmpty() ? "" : "; ") + "Số giường: " + soGiuongObj.toString();
                booking.setGhiChuNoiBo(ghiChu);
            }
            
            // Lấy isWalkIn từ requestData
            boolean isWalkIn = false;
            if (requestData.containsKey("isWalkIn")) {
                Object walkInObj = requestData.get("isWalkIn");
                if (walkInObj instanceof Boolean) isWalkIn = (Boolean) walkInObj;
                else if (walkInObj instanceof String) isWalkIn = Boolean.parseBoolean((String) walkInObj);
            }

            // Khi tạo booking:
            if (isWalkIn) {
                booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_NHAN_PHONG);
            } else {
                booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.CHO_XAC_NHAN);
            }
            booking.setNgayDat(LocalDateTime.now());
            
            // Generate booking code
            String maDatPhong = "BP" + System.currentTimeMillis();
            booking.setMaDatPhong(maDatPhong);
            
            // Calculate total payment for all rooms
            long days = java.time.temporal.ChronoUnit.DAYS.between(
                LocalDate.parse(ngayNhanPhongObj.toString()),
                LocalDate.parse(ngayTraPhongObj.toString())
            );
            BigDecimal tongThanhToan = BigDecimal.ZERO;
            for (Room room : rooms) {
                tongThanhToan = tongThanhToan.add(room.getGiaCoBan().multiply(BigDecimal.valueOf(days)));
            }
            booking.setTongThanhToan(tongThanhToan);
            
            bookingRepository.save(booking);
            
            // Sau khi bookingRepository.save(booking):
            if (isWalkIn) {
                // Gán tất cả phòng trạng thái ĐANG_SỬ_DỤNG và tạo BookingDetail
                for (Room room : rooms) {
                    room.setTrangThai(Room.TrangThaiPhong.DANG_SU_DUNG);
                    roomRepository.save(room);
                    
                    // Tạo BookingDetail cho từng phòng
                    BookingDetail detail = new BookingDetail();
                    detail.setDatPhongId(booking.getId());
                    detail.setPhongId(room.getId());
                    bookingDetailRepository.save(detail);
                }
                // Không gửi mail xác nhận, không cần đặt cọc
                return true;
            } else {
                // Đối với booking thường, chỉ tạo BookingDetail mà không cập nhật trạng thái phòng
                for (Room room : rooms) {
                    BookingDetail detail = new BookingDetail();
                    detail.setDatPhongId(booking.getId());
                    detail.setPhongId(room.getId());
                    bookingDetailRepository.save(detail);
                }
            }
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean confirmBookingAndAssignMultipleRooms(Long bookingId, java.util.List<Long> roomIds) {
        logger.info("[CONFIRM_MULTIPLE] Bắt đầu xác nhận bookingId={}, roomIds={}", bookingId, roomIds);
        try {
            Booking booking = bookingRepository.findById(bookingId).orElse(null);
            if (booking == null) {
                logger.warn("[CONFIRM_MULTIPLE] Không tìm thấy bookingId={}", bookingId);
                return false;
            }
            
            // Validate tất cả các phòng tồn tại
            java.util.List<Room> rooms = new java.util.ArrayList<>();
            for (Long roomId : roomIds) {
                Room room = roomRepository.findById(roomId.intValue()).orElse(null);
                if (room == null) {
                    logger.warn("[CONFIRM_MULTIPLE] Không tìm thấy roomId={}", roomId);
                    return false;
                }
                rooms.add(room);
            }
            
            // Tạo BookingDetail cho từng phòng nếu chưa có
            for (Room room : rooms) {
                boolean exists = bookingDetailRepository.findByDatPhongId(booking.getId())
                    .stream().anyMatch(d -> d.getPhongId() != null && d.getPhongId().equals(room.getId()));
                if (!exists) {
                    BookingDetail detail = new BookingDetail();
                    detail.setDatPhongId(booking.getId());
                    detail.setPhongId(room.getId());
                    bookingDetailRepository.save(detail);
                    logger.info("[CONFIRM_MULTIPLE] Đã tạo BookingDetail cho bookingId={}, roomId={}", bookingId, room.getId());
                } else {
                    logger.info("[CONFIRM_MULTIPLE] BookingDetail đã tồn tại cho bookingId={}, roomId={}", bookingId, room.getId());
                }
                
                // Cập nhật trạng thái phòng
                room.setTrangThai(Room.TrangThaiPhong.DA_DAT);
                roomRepository.save(room);
            }
            
            // Cập nhật trạng thái booking
            booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_XAC_NHAN);
            bookingRepository.save(booking);
            
            // Gửi email xác nhận với thông tin tất cả các phòng
            Customer customer = booking.getKhachHang();
            if (customer != null && customer.getEmail() != null) {
                StringBuilder roomInfo = new StringBuilder();
                for (Room room : rooms) {
                    roomInfo.append("Phòng: ").append(room.getSoPhong()).append("\n");
                }
                
                String emailBody = String.format(
                    "Xin chào %s!\n\n" +
                    "Chúng tôi rất vui mừng thông báo rằng đặt phòng của bạn đã được xác nhận thành công.\n\n" +
                    "Thông tin đặt phòng:\n" +
                    "Mã đặt phòng: %s\n" +
                    "Khách sạn: %s\n" +
                    "%s" +
                    "Ngày nhận phòng: %s\n" +
                    "Ngày trả phòng: %s\n" +
                    "Số người lớn: %d\n" +
                    "Số trẻ em: %d\n" +
                    "Tổng thanh toán: %s VND\n\n" +
                    "Cảm ơn bạn đã chọn khách sạn của chúng tôi!\n\n" +
                    "Trân trọng,\n" +
                    "Đội ngũ Panacea Hotel",
                    customer.getHo() + " " + customer.getTen(),
                    booking.getMaDatPhong(),
                    "Panacea Hotel",
                    roomInfo.toString(),
                    booking.getNgayNhanPhong(),
                    booking.getNgayTraPhong(),
                    booking.getSoNguoiLon(),
                    booking.getSoTreEm(),
                    booking.getTongTienPhong()
                );
                
                try {
                    sendEmail(
                        customer.getEmail(),
                        "Xác nhận đặt phòng - " + booking.getMaDatPhong(),
                        emailBody
                    );
                    logger.info("[CONFIRM_MULTIPLE] Đã gửi email xác nhận cho {}", customer.getEmail());
                } catch (Exception e) {
                    logger.error("[CONFIRM_MULTIPLE] Lỗi khi gửi email cho {}: {}", customer.getEmail(), e.getMessage());
                }
            }
            
            logger.info("[CONFIRM_MULTIPLE] Đã cập nhật trạng thái booking và {} phòng cho bookingId={}", rooms.size(), bookingId);
            return true;
        } catch (Exception e) {
            logger.error("[CONFIRM_MULTIPLE] Lỗi khi xác nhận booking: ", e);
            return false;
        }
    }
    
    public boolean confirmBookingAndAssignRoom(Long bookingId, Long roomId) {
        logger.info("[CONFIRM] Bắt đầu xác nhận bookingId={}, roomId={}", bookingId, roomId);
        try {
            Booking booking = bookingRepository.findById(bookingId).orElse(null);
            Room room = roomRepository.findById(roomId.intValue()).orElse(null);
            if (booking == null) {
                logger.warn("[CONFIRM] Không tìm thấy bookingId={}", bookingId);
                return false;
            }
            if (room == null) {
                logger.warn("[CONFIRM] Không tìm thấy roomId={}", roomId);
                return false;
            }
            // Chỉ tạo BookingDetail nếu chưa có
            boolean exists = bookingDetailRepository.findByDatPhongId(booking.getId())
                .stream().anyMatch(d -> d.getPhongId() != null && d.getPhongId().equals(room.getId()));
            if (!exists) {
                BookingDetail detail = new BookingDetail();
                detail.setDatPhongId(booking.getId());
                detail.setPhongId(room.getId());
                bookingDetailRepository.save(detail);
                logger.info("[CONFIRM] Đã tạo BookingDetail cho bookingId={}, roomId={}", bookingId, roomId);
            } else {
                logger.info("[CONFIRM] BookingDetail đã tồn tại cho bookingId={}, roomId={}", bookingId, roomId);
            }
            // Cập nhật trạng thái
            booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_XAC_NHAN);
            // Chỉ cập nhật trạng thái phòng được gán cho booking này
            if (room != null) {
                room.setTrangThai(Room.TrangThaiPhong.DA_DAT); // Đã đặt
                roomRepository.save(room);
            }
            bookingRepository.save(booking);
            logger.info("[CONFIRM] Đã cập nhật trạng thái booking và phòng cho bookingId={}, roomId={}", bookingId, roomId);
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
                    null, // Removed booking.getHotel().getTenKhachSan()
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
                logger.info("[CONFIRM] Đã gửi email xác nhận cho khách hàng: {}", customer.getEmail());
            } else {
                logger.warn("[CONFIRM] Không gửi được email xác nhận vì thiếu thông tin khách hàng hoặc email");
            }
            logger.info("[CONFIRM] Xác nhận đặt phòng thành công cho bookingId={}, roomId={}", bookingId, roomId);
            return true;
        } catch (Exception e) {
            logger.error("[CONFIRM] Lỗi khi xác nhận đặt phòng: {}", e.getMessage(), e);
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
                    room.setTrangThai(Room.TrangThaiPhong.DON_DEP); // Dọn dẹp
                    roomRepository.save(room);
                }
            }
        }
        // Copy sang bảng lịch sử và xóa booking gốc
        try {
            BookingHistory history = new BookingHistory();
            history.setMaDatPhong(booking.getMaDatPhong());
            history.setTenKhachHang(booking.getKhachHang() != null ? booking.getKhachHang().getHo() + " " + booking.getKhachHang().getTen() : null);
            history.setEmail(booking.getKhachHang() != null ? booking.getKhachHang().getEmail() : null);
            history.setSoDienThoai(booking.getKhachHang() != null ? booking.getKhachHang().getSoDienThoai() : null);
            history.setTenKhachSan(null); // Removed booking.getHotel().getTenKhachSan()
            // Lấy số phòng đầu tiên (nếu có)
            if (!details.isEmpty()) {
                Room room = roomRepository.findById(details.get(0).getPhongId()).orElse(null);
                if (room != null) history.setSoPhong(room.getSoPhong());
            }
            history.setNgayNhanPhong(booking.getNgayNhanPhong());
            history.setNgayTraPhong(booking.getNgayTraPhong());
            history.setSoNguoiLon(booking.getSoNguoiLon());
            history.setSoTreEm(booking.getSoTreEm());
            history.setTongThanhToan(booking.getTongThanhToan());
            history.setNgayDat(booking.getNgayDat());
            history.setNgayHoanThanh(java.time.LocalDateTime.now());
            history.setGhiChu(booking.getGhiChuKhachHang());
            history.setTrangThai("HOAN_THANH");
            bookingHistoryRepository.save(history);
            bookingRepository.delete(booking);
        } catch (Exception e) {
            e.printStackTrace();
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

    /**
     * Tự động phân bổ nhiều phòng cho booking vượt sức chứa
     * @param bookingId ID của booking
     * @param soPhongCanThiet Số phòng cần thiết
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean assignMultipleRooms(Long bookingId, int soPhongCanThiet) {
        logger.info("[ASSIGN_MULTIPLE] Bắt đầu phân bổ {} phòng cho bookingId={}", soPhongCanThiet, bookingId);
        
        try {
            Booking booking = bookingRepository.findById(bookingId).orElse(null);
            if (booking == null) {
                logger.warn("[ASSIGN_MULTIPLE] Không tìm thấy bookingId={}", bookingId);
                return false;
            }

            // Lấy loại phòng từ booking
            if (booking.getRoomType() == null) {
                logger.warn("[ASSIGN_MULTIPLE] Booking {} không có loại phòng", bookingId);
                return false;
            }

            // Tìm phòng trống cùng loại trong khoảng thời gian đặt
            List<Room> availableRooms = getAvailableRoomsByType(
                booking.getRoomType().getId(), 
                booking.getNgayNhanPhong(), 
                booking.getNgayTraPhong()
            );

            if (availableRooms.size() < soPhongCanThiet) {
                logger.warn("[ASSIGN_MULTIPLE] Chỉ có {} phòng trống, cần {}", availableRooms.size(), soPhongCanThiet);
                return false;
            }

            // Gán từng phòng cho booking
            for (int i = 0; i < soPhongCanThiet; i++) {
                Room room = availableRooms.get(i);
                
                // Kiểm tra xem phòng đã được gán chưa
                boolean exists = bookingDetailRepository.findByDatPhongId(booking.getId())
                    .stream().anyMatch(d -> d.getPhongId() != null && d.getPhongId().equals(room.getId()));
                
                if (!exists) {
                    BookingDetail detail = new BookingDetail();
                    detail.setDatPhongId(booking.getId());
                    detail.setPhongId(room.getId());
                    bookingDetailRepository.save(detail);
                    
                    // Cập nhật trạng thái phòng
                    room.setTrangThai(Room.TrangThaiPhong.DA_DAT);
                    roomRepository.save(room);
                    
                    logger.info("[ASSIGN_MULTIPLE] Đã gán phòng {} cho booking {}", room.getSoPhong(), bookingId);
                }
            }

            // Cập nhật tổng tiền phòng theo số phòng đã gán
            BigDecimal giaPhongGoc = booking.getTongTienPhong();
            if (giaPhongGoc != null && giaPhongGoc.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal tongTienMoi = giaPhongGoc.multiply(BigDecimal.valueOf(soPhongCanThiet));
                booking.setTongTienPhong(tongTienMoi);
                booking.setTongThanhToan(tongTienMoi); // Cập nhật tổng thanh toán
                
                // Cập nhật tiền cọc (50% tổng tiền)
                BigDecimal tienCocMoi = tongTienMoi.divide(BigDecimal.valueOf(2), 0, java.math.RoundingMode.HALF_UP);
                booking.setTienDatCoc(tienCocMoi);
            }

            // Cập nhật ghi chú
            String ghiChuMoi = "Đã phân bổ " + soPhongCanThiet + " phòng cho " + 
                              (booking.getSoNguoiLon() + booking.getSoTreEm()) + " khách. ";
            if (booking.getGhiChuKhachHang() != null) {
                booking.setGhiChuKhachHang(ghiChuMoi + booking.getGhiChuKhachHang());
            } else {
                booking.setGhiChuKhachHang(ghiChuMoi);
            }

            // Cập nhật trạng thái booking
            booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_XAC_NHAN);
            bookingRepository.save(booking);

            logger.info("[ASSIGN_MULTIPLE] Đã phân bổ thành công {} phòng cho booking {}", soPhongCanThiet, bookingId);
            return true;
            
        } catch (Exception e) {
            logger.error("[ASSIGN_MULTIPLE] Lỗi khi phân bổ phòng cho booking {}: {}", bookingId, e.getMessage());
            return false;
        }
    }

    /**
     * Lấy danh sách phòng trống theo loại phòng và khoảng thời gian
     */
    private List<Room> getAvailableRoomsByType(Integer roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        // Tìm tất cả phòng thuộc loại phòng này
        List<Room> allRooms = roomRepository.findAll().stream()
            .filter(room -> room.getRoomType() != null && room.getRoomType().getId().equals(roomTypeId))
            .filter(room -> room.getTrangThai() == Room.TrangThaiPhong.SAN_SANG)
            .collect(Collectors.toList());

        // Lọc ra những phòng không bị đặt trong khoảng thời gian này
        return allRooms.stream()
            .filter(room -> !isRoomBookedInPeriod(room.getId(), checkIn, checkOut))
            .collect(Collectors.toList());
    }

    /**
     * Kiểm tra phòng có bị đặt trong khoảng thời gian không
     */
    private boolean isRoomBookedInPeriod(Integer roomId, LocalDate checkIn, LocalDate checkOut) {
        List<BookingDetail> bookingDetails = bookingDetailRepository.findAll().stream()
            .filter(detail -> detail.getPhongId() != null && detail.getPhongId().equals(roomId))
            .collect(Collectors.toList());

        for (BookingDetail detail : bookingDetails) {
            Booking booking = detail.getBooking();
            if (booking != null && 
                booking.getTrangThaiDatPhong() != Booking.TrangThaiDatPhong.DA_HUY &&
                booking.getTrangThaiDatPhong() != Booking.TrangThaiDatPhong.DA_HOAN_THANH) {
                
                LocalDate bookingCheckIn = booking.getNgayNhanPhong();
                LocalDate bookingCheckOut = booking.getNgayTraPhong();
                
                // Kiểm tra overlap thời gian
                if (!(checkOut.isBefore(bookingCheckIn) || checkIn.isAfter(bookingCheckOut))) {
                    return true; // Phòng đã bị đặt
                }
            }
        }
        return false;
    }

    private void sendThankYouEmail(Booking booking, LocalDate expiryDate) {
        if (mailSender == null) return;
        Customer customer = booking.getKhachHang();
        if (customer == null || customer.getEmail() == null) return;
        String subject = "Panacea Hotel xin gửi lời cảm ơn chân thành đến Quý khách!";
        String content = String.format("""
            <html><body>
            <p>Kính gửi Quý khách <b>%s</b>,</p>
            <p>Thay mặt toàn thể đội ngũ nhân viên Panacea Hotel, chúng tôi xin gửi lời cảm ơn chân thành nhất đến Quý khách đã tin tưởng lựa chọn Panacea Hotel làm điểm dừng chân trong chuyến đi vừa qua.</p>
            <p>Chúng tôi hy vọng Quý khách đã có những trải nghiệm thật sự thoải mái, thư giãn và đáng nhớ tại Panacea Hotel. Sự hài lòng của Quý khách là niềm vinh hạnh và là nguồn động lực lớn nhất để chúng tôi không ngừng nỗ lực nâng cao chất lượng dịch vụ mỗi ngày.</p>
            <p>Để bày tỏ lòng tri ân và mong được chào đón Quý khách trở lại trong những lần tiếp theo, Panacea Hotel xin gửi tặng Quý khách mã ưu đãi <b>PANACEA15</b> giảm giá 15%% cho lần đặt phòng kế tiếp.<br/>(Lưu ý: Mã ưu đãi có hiệu lực đến ngày <b>%s</b>)</p>
            <p>Chúng tôi sẽ vô cùng biết ơn nếu Quý khách có thể dành một vài phút để lại đánh giá về trải nghiệm của mình trên trang <a href=\"https://www.tripadvisor.com/Hotel_Review-g293925-d25279460-Reviews-Panacea_Hotel-Ho_Chi_Minh_City.html\" target=\"_blank\">TripAdvisor</a> hoặc <a href=\"https://goo.gl/maps/your-google-maps-link\" target=\"_blank\">Google</a>. Những góp ý của Quý khách là vô giá để chúng tôi hoàn thiện hơn.</p>
            <p>Một lần nữa, xin chân thành cảm ơn Quý khách. Chúc Quý khách một ngày tốt lành và mong sớm được phục vụ Quý khách trong tương lai không xa.</p>
            <br/>
            <p>Trân trọng,</p>
            <b>Nguyễn Văn A</b><br/>
            Giám đốc Quan hệ Khách hàng<br/>
            Panacea Hotel<br/>
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
    
    public Page<BookingDetailViewDTO> getBookingDetailViewDTOs(String keyword, java.time.LocalDate ngayNhan, Pageable pageable) {
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
        
        java.util.List<BookingDetailViewDTO> dtoList = filtered.stream()
            .map(booking -> {
                java.util.List<BookingDetail> details = bookingDetailRepository.findByDatPhongId(booking.getId());
                return BookingDetailViewDTO.fromEntity(booking, details, new java.util.ArrayList<>());
            })
            .collect(Collectors.toList());
        
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), dtoList.size());
        java.util.List<BookingDetailViewDTO> pageContent = start > end ? java.util.Collections.emptyList() : dtoList.subList(start, end);
        return new PageImpl<>(pageContent, pageable, dtoList.size());
    }

    // Trả về chi tiết booking kèm danh sách dịch vụ đã sử dụng
    public BookingDetailViewDTO getBookingDetailViewDTOById(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) return null;
        List<BookingDetail> details = bookingDetailRepository.findByDatPhongId(booking.getId());
        // Lấy danh sách dịch vụ đã sử dụng
        List<ServiceDetail> serviceDetails = serviceDetailRepository.findByDatPhongId(booking.getId());
        List<ServiceDetailDTO> serviceUsages = serviceDetails.stream().map(sd -> {
            ServiceDetailDTO dto = new ServiceDetailDTO();
            dto.setId(sd.getId());
            dto.setBookingId(sd.getDatPhongId());
            dto.setServiceId(sd.getDichVuId());
            
            // Lấy tên dịch vụ từ ServiceRepository
            String serviceName = "Dịch vụ không xác định";
            if (sd.getDichVuId() != null) {
                var service = serviceRepository.findById(sd.getDichVuId());
                if (service.isPresent()) {
                    serviceName = service.get().getTenDichVu();
                }
            }
            dto.setServiceName(serviceName);
            
            // Lấy thông tin phòng từ BookingDetail đầu tiên (giả sử 1 booking = 1 phòng)
            if (!details.isEmpty() && details.get(0).getRoom() != null) {
                Room room = details.get(0).getRoom();
                dto.setRoomId(room.getId());
                dto.setRoomNumber(room.getSoPhong());
            }
            
            dto.setSoLuong(sd.getSoLuong() != null ? sd.getSoLuong().intValue() : null);
            dto.setDonGia(sd.getDonGiaThucTe());
            if (sd.getDonGiaThucTe() != null && sd.getSoLuong() != null) {
                dto.setThanhTien(sd.getDonGiaThucTe().multiply(new java.math.BigDecimal(sd.getSoLuong())));
            }
            dto.setTrangThai(null);
            dto.setUuidId(sd.getUuidId());
            dto.setCreatedDate(sd.getCreatedDate());
            return dto;
        }).toList();
        return BookingDetailViewDTO.fromEntity(booking, details, serviceUsages);
    }

    public boolean checkInBooking(Long bookingId, String soCmndCccd, LocalDate ngayCapCmnd, String noiCapCmnd, Byte soNguoiLonThucTe, Byte soTreEmThucTe, String ghiChuCheckIn) {
        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) return false;
        if (booking.getTrangThaiDatPhong() != Booking.TrangThaiDatPhong.DA_XAC_NHAN) return false;
        // Cập nhật trạng thái booking
        booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_NHAN_PHONG);
        booking.setCheckInTime(LocalDateTime.now());
        booking.setSoCmndCccdCheckIn(soCmndCccd);
        booking.setNgayCapCmndCheckIn(ngayCapCmnd);
        booking.setNoiCapCmndCheckIn(noiCapCmnd);
        booking.setSoNguoiLonThucTe(soNguoiLonThucTe);
        booking.setSoTreEmThucTe(soTreEmThucTe);
        booking.setGhiChuCheckIn(ghiChuCheckIn);
        bookingRepository.save(booking);
        // Cập nhật trạng thái phòng
        List<BookingDetail> details = bookingDetailRepository.findByDatPhongId(booking.getId());
        for (BookingDetail detail : details) {
            if (detail.getPhongId() != null) {
                Room room = roomRepository.findById(detail.getPhongId()).orElse(null);
                if (room != null) {
                    room.setTrangThai(Room.TrangThaiPhong.DANG_SU_DUNG);
                    roomRepository.save(room);
                }
            }
        }
        return true;
    }

    public List<Room> getAvailableRooms(Integer roomTypeId) {
        return roomRepository.findAll().stream()
            .filter(room -> room.getTrangThai() == Room.TrangThaiPhong.SAN_SANG &&
                            (roomTypeId == null || (room.getRoomType() != null && room.getRoomType().getId().equals(roomTypeId))))
            .collect(java.util.stream.Collectors.toList());
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
                
                // Giải phóng phòng - cập nhật trạng thái phòng về SAN_SANG
                List<BookingDetail> details = bookingDetailRepository.findByDatPhongId(booking.getId());
                for (BookingDetail detail : details) {
                    if (detail.getPhongId() != null) {
                        Room room = roomRepository.findById(detail.getPhongId()).orElse(null);
                        if (room != null) {
                            room.setTrangThai(Room.TrangThaiPhong.SAN_SANG); // Trả về trạng thái sẵn sàng
                            roomRepository.save(room);
                        }
                    }
                }
                
                logger.info("[AutoCancel] Đã hủy booking {} do quá hạn thanh toán!", booking.getMaDatPhong());
                // Gửi email thông báo hủy nếu có
                try {
                    Customer customer = booking.getKhachHang();
                    if (customer != null && customer.getEmail() != null) {
                        String emailBody = String.format(
                            "Xin chào %s!\n\n" +
                            "Đặt phòng của bạn (mã: %s) tại Panacea Hotel đã bị hủy do không thanh toán trong vòng 1 phút (test).\n" +
                            "Nếu có thắc mắc, vui lòng liên hệ Panacea Hotel.\n\nTrân trọng,\nPanacea Hotel",
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

    // Cập nhật lại danh sách dịch vụ đã sử dụng cho booking
    public boolean updateBookingServices(Long bookingId, java.util.List<java.util.Map<String, Object>> services) {
        try {
            var bookingOpt = bookingRepository.findById(bookingId);
            if (bookingOpt.isEmpty()) return false;
            var booking = bookingOpt.get();
            // Xóa toàn bộ ServiceDetail cũ
            var oldDetails = serviceDetailRepository.findByDatPhongId(booking.getId());
            serviceDetailRepository.deleteAll(oldDetails);
            // Thêm lại các ServiceDetail mới
            java.math.BigDecimal tongTienDichVu = java.math.BigDecimal.ZERO;
            for (var s : services) {
                Integer serviceId = (Integer) (s.get("serviceId") instanceof Integer ? s.get("serviceId") : Integer.parseInt(s.get("serviceId").toString()));
                Integer soLuong = (Integer) (s.get("soLuong") instanceof Integer ? s.get("soLuong") : Integer.parseInt(s.get("soLuong").toString()));
                if (soLuong == null || soLuong < 1) continue;
                var serviceOpt = serviceRepository.findById(serviceId);
                if (serviceOpt.isEmpty()) continue;
                var service = serviceOpt.get();
                var detail = new panacea.website_dat_lich_khach_san.entity.ServiceDetail();
                detail.setDatPhongId(booking.getId());
                detail.setDichVuId(serviceId);
                detail.setSoLuong(soLuong.shortValue());
                detail.setDonGiaThucTe(service.getDonGia());
                detail.setGhiChu(null);
                serviceDetailRepository.save(detail);
                if (service.getDonGia() != null) {
                    tongTienDichVu = tongTienDichVu.add(service.getDonGia().multiply(new java.math.BigDecimal(soLuong)));
                }
            }
            // Cập nhật tổng tiền dịch vụ và tổng thanh toán booking
            booking.setTongTienDichVu(tongTienDichVu);
            if (booking.getTongTienPhong() != null) {
                booking.setTongThanhToan(booking.getTongTienPhong().add(tongTienDichVu));
            } else {
                booking.setTongThanhToan(tongTienDichVu);
            }
            bookingRepository.save(booking);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}