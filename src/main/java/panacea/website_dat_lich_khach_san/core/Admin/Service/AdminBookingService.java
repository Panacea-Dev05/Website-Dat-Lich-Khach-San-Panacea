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
import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.BookingDTO;
import panacea.website_dat_lich_khach_san.repository.AuditLogRepository;
import panacea.website_dat_lich_khach_san.repository.BookingDetailRepository;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.repository.PaymentRepository;
import panacea.website_dat_lich_khach_san.repository.PromotionRepository;
import panacea.website_dat_lich_khach_san.repository.ServiceDetailRepository;
import panacea.website_dat_lich_khach_san.repository.StaffRepository;
import panacea.website_dat_lich_khach_san.repository.HotelRepository;

/**
 * Service class quản lý đặt phòng cho Admin
 * Chức năng chính:
 * - Quản lý toàn bộ booking trong hệ thống
 * - Xử lý thanh toán và hoàn tiền
 * - Áp dụng khuyến mãi
 * - Theo dõi lịch sử và audit log
 * - Lọc và tìm kiếm booking theo nhiều tiêu chí
 */
@Service
public class AdminBookingService {
    
    // Repository dependencies - Các repository để truy cập dữ liệu
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
    @Autowired
    private HotelRepository hotelRepository;
    
    /**
     * Lấy danh sách tất cả booking trong hệ thống
     * Bao gồm thông tin nhân viên tạo booking từ audit log
     * @return List<BookingDTO> - Danh sách booking với thông tin đầy đủ
     */
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
    
    /**
     * Lấy danh sách booking có thể thanh toán
     * Loại bỏ các booking đã hủy khỏi danh sách
     * @return List<BookingDTO> - Danh sách booking có thể thanh toán
     */
    public List<BookingDTO> getPayableBookings() {
        return bookingRepository.findAll().stream()
                .filter(booking -> booking.getTrangThaiDatPhong() != Booking.TrangThaiDatPhong.DA_HUY) // Loại bỏ booking đã hủy
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy danh sách booking đã hủy để tạo hoàn tiền
     * Chỉ lấy các booking có trạng thái đã hủy
     * @return List<BookingDTO> - Danh sách booking đã hủy
     */
    public List<BookingDTO> getCancelledBookings() {
        return bookingRepository.findByTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_HUY)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Lấy thông tin booking theo ID
     * @param id - ID của booking cần tìm
     * @return BookingDTO - Thông tin booking hoặc null nếu không tìm thấy
     */
    public BookingDTO getBookingById(Integer id) {
        Optional<Booking> booking = bookingRepository.findById(id);
        return booking.map(this::convertToDTO).orElse(null);
    }
    
    /**
     * Tạo booking mới trong hệ thống
     * @param bookingDTO - Thông tin booking cần tạo
     * @return BookingDTO - Thông tin booking đã được tạo
     */
    public BookingDTO createBooking(BookingDTO bookingDTO) {
        Booking booking = convertToEntity(bookingDTO);
        Booking savedBooking = bookingRepository.save(booking);
        return convertToDTO(savedBooking);
    }
    
    /**
     * Cập nhật thông tin booking
     * @param id - ID của booking cần cập nhật
     * @param bookingDTO - Thông tin booking mới
     * @return BookingDTO - Thông tin booking đã cập nhật hoặc null nếu không tìm thấy
     */
    public BookingDTO updateBooking(Integer id, BookingDTO bookingDTO) {
        Optional<Booking> existingBooking = bookingRepository.findById(id);
        if (existingBooking.isPresent()) {
            Booking booking = existingBooking.get();
            booking.setNgayNhanPhong(bookingDTO.getNgayNhanPhong());
            booking.setNgayTraPhong(bookingDTO.getNgayTraPhong());
            booking.setTongThanhToan(bookingDTO.getTongThanhToan());
            booking.setTrangThaiDatPhong(bookingDTO.getTrangThaiDatPhong() != null ? Booking.TrangThaiDatPhong.fromString(bookingDTO.getTrangThaiDatPhong()) : null);
            booking.setGhiChuKhachHang(bookingDTO.getGhiChuKhachHang());
            booking.setYeuCauDacBiet(bookingDTO.getYeuCauDacBiet());
            Booking savedBooking = bookingRepository.save(booking);
            return convertToDTO(savedBooking);
        }
        return null;
    }
    
    /**
     * Xóa booking khỏi hệ thống
     * @param id - ID của booking cần xóa
     * @return boolean - true nếu xóa thành công, false nếu không tìm thấy
     */
    public boolean deleteBooking(Integer id) {
        if (bookingRepository.existsById(id)) {
            bookingRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    /**
     * Chuyển đổi entity Booking thành DTO
     * Bao gồm thông tin phòng, loại phòng và khách hàng
     * @param booking - Entity booking cần chuyển đổi
     * @return BookingDTO - DTO chứa thông tin booking
     */
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
        dto.setYeuCauDacBiet(booking.getYeuCauDacBiet());
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
    
    /**
     * Chuyển đổi DTO thành entity Booking
     * @param dto - DTO chứa thông tin booking
     * @return Booking - Entity booking
     */
    private Booking convertToEntity(BookingDTO dto) {
        Booking booking = new Booking();
        booking.setNgayNhanPhong(dto.getNgayNhanPhong());
        booking.setNgayTraPhong(dto.getNgayTraPhong());
        booking.setTongThanhToan(dto.getTongThanhToan());
        booking.setTrangThaiDatPhong(dto.getTrangThaiDatPhong() != null ? Booking.TrangThaiDatPhong.fromString(dto.getTrangThaiDatPhong()) : null);
        booking.setGhiChuKhachHang(dto.getGhiChuKhachHang());
        
        // Set hotel (assuming single hotel model)
        Hotel hotel = hotelRepository.findAll().stream().findFirst().orElse(null);
        booking.setHotel(hotel);
        
        return booking;
    }
    
    /**
     * Xác nhận booking - chuyển trạng thái thành đã xác nhận
     * @param bookingId - ID của booking cần xác nhận
     * @return BookingDTO - Thông tin booking đã xác nhận hoặc null nếu không tìm thấy
     */
    public BookingDTO confirmBooking(Integer bookingId) {
        Optional<Booking> opt = bookingRepository.findById(bookingId);
        if (opt.isEmpty()) return null;
        Booking booking = opt.get();
        booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_XAC_NHAN);
        Booking saved = bookingRepository.save(booking);
        return convertToDTO(saved);
    }
    
    /**
     * Check-in khách hàng - chuyển trạng thái thành đã nhận phòng
     * @param bookingId - ID của booking cần check-in
     * @return BookingDTO - Thông tin booking đã check-in hoặc null nếu không tìm thấy
     */
    public BookingDTO checkIn(Integer bookingId) {
        Optional<Booking> opt = bookingRepository.findById(bookingId);
        if (opt.isEmpty()) return null;
        Booking booking = opt.get();
        booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_NHAN_PHONG);
        Booking saved = bookingRepository.save(booking);
        return convertToDTO(saved);
    }
    
    /**
     * Check-out khách hàng - chuyển trạng thái thành đã hoàn thành
     * @param bookingId - ID của booking cần check-out
     * @return BookingDTO - Thông tin booking đã check-out hoặc null nếu không tìm thấy
     */
    public BookingDTO checkOut(Integer bookingId) {
        Optional<Booking> opt = bookingRepository.findById(bookingId);
        if (opt.isEmpty()) return null;
        Booking booking = opt.get();
        booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.DA_HOAN_THANH);
        Booking saved = bookingRepository.save(booking);
        return convertToDTO(saved);
    }
    
    /**
     * Áp dụng mã khuyến mãi cho booking
     * Giảm 10% tổng thanh toán nếu mã khuyến mãi hợp lệ
     * @param bookingId - ID của booking
     * @param promoCode - Mã khuyến mãi
     * @return BookingDTO - Thông tin booking sau khi áp dụng khuyến mãi
     */
    public BookingDTO applyPromotion(Integer bookingId, String promoCode) {
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
    
    /**
     * Lọc danh sách booking theo nhiều tiêu chí
     * @param customerId - ID khách hàng (có thể null)
     * @param hotelId - ID khách sạn (có thể null)
     * @param from - Ngày bắt đầu (có thể null)
     * @param to - Ngày kết thúc (có thể null)
     * @return List<BookingDTO> - Danh sách booking phù hợp với tiêu chí
     */
    public List<BookingDTO> filterBookings(Integer customerId, Integer hotelId, LocalDate from, LocalDate to) {
        return bookingRepository.findAll().stream()
            .filter(b -> (customerId == null || (b.getKhachHang() != null && b.getKhachHang().getId().equals(customerId))))
            // Đã bỏ filter theo hotelId vì không còn quan hệ hotel
            .filter(b -> (from == null || !b.getNgayNhanPhong().isBefore(from)))
            .filter(b -> (to == null || !b.getNgayTraPhong().isAfter(to)))
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    // Lấy chi tiết booking đầy đủ bao gồm dịch vụ và thanh toán
    public BookingFullDetail getBookingFullDetail(Integer id) {
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