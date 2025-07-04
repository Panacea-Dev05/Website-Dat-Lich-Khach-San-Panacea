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
            Room room = roomRepository.findById(Long.valueOf(dto.getRoomId())).orElse(null);
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
            // TODO: set thêm các trường khác nếu cần

            bookingRepository.save(booking);

            // Gửi mail cho khách hàng
            if (mailSender != null) {
                String subject = "[Panacea Hotel] Yêu cầu đặt phòng đã được gửi";
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
                    "</ul>" +
                    "<p>Yêu cầu của bạn đang chờ xác nhận từ nhân viên. Chúng tôi sẽ gửi email xác nhận khi đặt phòng được duyệt.</p>" +
                    "<br><b>Panacea Hotel</b>",
                    dto.getTenKhach(),
                    dto.getNgayNhanPhong(),
                    dto.getNgayTraPhong(),
                    dto.getSoNguoiLon(),
                    dto.getSoTreEm(),
                    dto.getGhiChuKhachHang() != null ? dto.getGhiChuKhachHang() : "Không có"
                );
                sendMail(dto.getEmailKhach(), subject, text);
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
} 