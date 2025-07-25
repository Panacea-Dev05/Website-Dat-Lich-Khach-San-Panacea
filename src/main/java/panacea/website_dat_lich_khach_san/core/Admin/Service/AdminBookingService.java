package panacea.website_dat_lich_khach_san.core.Admin.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import panacea.website_dat_lich_khach_san.entity.AuditLog;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.entity.BookingDetail;
import panacea.website_dat_lich_khach_san.entity.Payment;
import panacea.website_dat_lich_khach_san.entity.Promotion;
import panacea.website_dat_lich_khach_san.entity.ServiceDetail;
import panacea.website_dat_lich_khach_san.entity.Staff;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.BookingDTO;
import panacea.website_dat_lich_khach_san.repository.AuditLogRepository;
import panacea.website_dat_lich_khach_san.repository.BookingDetailRepository;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.repository.PaymentRepository;
import panacea.website_dat_lich_khach_san.repository.PromotionRepository;
import panacea.website_dat_lich_khach_san.repository.ServiceDetailRepository;
import panacea.website_dat_lich_khach_san.repository.StaffRepository;

@Service
public class AdminBookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    @Autowired
    private BookingDetailRepository bookingDetailRepository;
    
    @Autowired
    private PromotionRepository promotionRepository;
    
    @Autowired
    private ServiceDetailRepository serviceDetailRepository;
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    @Autowired
    private StaffRepository staffRepository;
    
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(booking -> {
                    BookingDTO dto = convertToDTO(booking);
                    // Lấy tên nhân viên tạo booking từ AuditLog
                    AuditLog log = auditLogRepository.findAll().stream()
                        .filter(a -> "Booking".equals(a.getBangTacDong()) &&
                                     a.getIdBanGhi() == booking.getId() &&
                                     a.getHanhDong() == AuditLog.HanhDong.INSERT &&
                                     "Staff".equals(a.getUserType()))
                        .findFirst().orElse(null);
                    if (log != null && log.getUserId() != null) {
                        Staff staff = staffRepository.findById(log.getUserId()).orElse(null);
                        if (staff != null) {
                            dto.setTenNhanVienTao(staff.getHoTen());
                        }
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    public BookingDTO getBookingById(Long id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.map(this::convertToDTO).orElse(null);
    }
    
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        Booking booking = convertToEntity(bookingDTO);
        Booking savedBooking = bookingRepository.save(booking);
        return convertToDTO(savedBooking);
    }
    
    public BookingDTO updateBooking(Long id, BookingDTO bookingDTO) {
        Optional<Booking> existingBooking = bookingRepository.findById(id);
        if (existingBooking.isPresent()) {
            Booking booking = existingBooking.get();
            booking.setNgayNhanPhong(bookingDTO.getNgayNhanPhong());
            booking.setNgayTraPhong(bookingDTO.getNgayTraPhong());
            booking.setTongThanhToan(bookingDTO.getTongThanhToan());
            booking.setTrangThaiDatPhong(bookingDTO.getTrangThaiDatPhong() != null ? Booking.TrangThaiDatPhong.fromString(bookingDTO.getTrangThaiDatPhong()) : null);
            booking.setGhiChuKhachHang(bookingDTO.getGhiChuKhachHang());
            Booking savedBooking = bookingRepository.save(booking);
            return convertToDTO(savedBooking);
        }
        return null;
    }
    
    public boolean deleteBooking(Long id) {
        if (bookingRepository.existsById(id)) {
            bookingRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setMaDatPhong(booking.getMaDatPhong());
        dto.setKhachHangId(booking.getKhachHang().getId());
        dto.setNgayDat(booking.getNgayDat());
        dto.setNgayNhanPhong(booking.getNgayNhanPhong());
        dto.setNgayTraPhong(booking.getNgayTraPhong());
        dto.setTongThanhToan(booking.getTongThanhToan());
        dto.setTrangThaiDatPhong(booking.getTrangThaiDatPhong() != null ? booking.getTrangThaiDatPhong().name() : null);
        dto.setGhiChuKhachHang(booking.getGhiChuKhachHang());
        dto.setCreatedDate(booking.getCreatedDate());
        // Set roomNumber and roomTypeName from BookingDetail if available
        java.util.List<BookingDetail> details = bookingDetailRepository.findByDatPhongId(booking.getId());
        if (details != null && !details.isEmpty() && details.get(0).getRoom() != null) {
            dto.setRoomNumber(details.get(0).getRoom().getSoPhong());
            if (details.get(0).getRoom().getRoomType() != null) {
                dto.setRoomTypeName(details.get(0).getRoom().getRoomType().getTenLoaiPhong());
            }
        }
        // Set customerName
        if (booking.getKhachHang() != null) {
            dto.setCustomerName(booking.getKhachHang().getHo() + " " + booking.getKhachHang().getTen());
        } else {
            dto.setCustomerName("Không rõ");
        }
        return dto;
    }
    
    private Booking convertToEntity(BookingDTO dto) {
        Booking booking = new Booking();
        booking.setNgayNhanPhong(dto.getNgayNhanPhong());
        booking.setNgayTraPhong(dto.getNgayTraPhong());
        booking.setTongThanhToan(dto.getTongThanhToan());
        booking.setTrangThaiDatPhong(dto.getTrangThaiDatPhong() != null ? Booking.TrangThaiDatPhong.fromString(dto.getTrangThaiDatPhong()) : null);
        booking.setGhiChuKhachHang(dto.getGhiChuKhachHang());
        return booking;
    }
    
    // Xác nhận booking
    public BookingDTO confirmBooking(Long bookingId) {
        Optional<Booking> opt = bookingRepository.findById(bookingId);
        if (opt.isEmpty()) return null;
        Booking booking = opt.get();
        booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_XAC_NHAN);
        Booking saved = bookingRepository.save(booking);
        return convertToDTO(saved);
    }
    
    // Check-in
    public BookingDTO checkIn(Long bookingId) {
        Optional<Booking> opt = bookingRepository.findById(bookingId);
        if (opt.isEmpty()) return null;
        Booking booking = opt.get();
        booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_NHAN_PHONG);
        Booking saved = bookingRepository.save(booking);
        return convertToDTO(saved);
    }
    
    // Check-out
    public BookingDTO checkOut(Long bookingId) {
        Optional<Booking> opt = bookingRepository.findById(bookingId);
        if (opt.isEmpty()) return null;
        Booking booking = opt.get();
        booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_HOAN_THANH);
        Booking saved = bookingRepository.save(booking);
        return convertToDTO(saved);
    }
    
    // Áp dụng promotion
    public BookingDTO applyPromotion(Long bookingId, String promoCode) {
        Optional<Booking> opt = bookingRepository.findById(bookingId);
        if (opt.isEmpty()) return null;
        Booking booking = opt.get();
        List<Promotion> promos = promotionRepository.findAll().stream()
            .filter(p -> p.getMaKhuyenMai().equalsIgnoreCase(promoCode))
            .toList();
        if (promos.isEmpty()) return convertToDTO(booking);
        Promotion promo = promos.get(0);
        // Giả lập giảm giá 10% nếu hợp lệ
        if (promo.getTrangThai() == Promotion.TrangThaiPromotion.HOAT_DONG) {
            booking.setTongThanhToan(booking.getTongThanhToan().multiply(java.math.BigDecimal.valueOf(0.9)));
        }
        Booking saved = bookingRepository.save(booking);
        return convertToDTO(saved);
    }
    
    // Lọc danh sách booking theo customer/hotel/date
    public List<BookingDTO> filterBookings(Integer customerId, Integer hotelId, LocalDate from, LocalDate to) {
        return bookingRepository.findAll().stream()
            .filter(b -> (customerId == null || (b.getKhachHang() != null && b.getKhachHang().getId().equals(customerId))))
            // Đã bỏ filter theo hotelId vì không còn quan hệ hotel
            .filter(b -> (from == null || !b.getNgayNhanPhong().isBefore(from)))
            .filter(b -> (to == null || !b.getNgayTraPhong().isAfter(to)))
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    // Xem chi tiết booking đầy đủ (phòng, dịch vụ, thanh toán)
    public BookingFullDetail getBookingFullDetail(Long id) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isEmpty()) return null;
        Booking booking = bookingOpt.get();
        BookingDTO dto = convertToDTO(booking);
        // Lấy dịch vụ
        List<ServiceDetail> services = serviceDetailRepository.findAll().stream()
            .filter(s -> s.getBooking() != null && s.getBooking().getId().equals(id))
            .toList();
        // Lấy thanh toán
        List<Payment> payments = paymentRepository.findAll().stream()
            .filter(p -> p.getBooking() != null && p.getBooking().getId().equals(id))
            .toList();
        return new BookingFullDetail(dto, services, payments);
    }
    
    // DTO trả về chi tiết booking đầy đủ
    public static class BookingFullDetail {
        public BookingDTO booking;
        public List<ServiceDetail> services;
        public List<Payment> payments;
        public BookingFullDetail(BookingDTO booking, List<ServiceDetail> services, List<Payment> payments) {
            this.booking = booking;
            this.services = services;
            this.payments = payments;
        }
    }
} 