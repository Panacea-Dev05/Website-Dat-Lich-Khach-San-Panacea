package panacea.website_dat_lich_khach_san.core.Admin.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.BookingDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminBookingService {
    
    @Autowired
    private BookingRepository bookingRepository;
    
    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(this::convertToDTO)
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
            booking.setTrangThaiDatPhong(bookingDTO.getTrangThaiDatPhong() != null ? Booking.TrangThaiDatPhong.valueOf(bookingDTO.getTrangThaiDatPhong()) : null);
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
        dto.setKhachHangId(booking.getKhachHang().getId());
        dto.setNgayNhanPhong(booking.getNgayNhanPhong());
        dto.setNgayTraPhong(booking.getNgayTraPhong());
        dto.setTongThanhToan(booking.getTongThanhToan());
        dto.setTrangThaiDatPhong(booking.getTrangThaiDatPhong() != null ? booking.getTrangThaiDatPhong().name() : null);
        dto.setGhiChuKhachHang(booking.getGhiChuKhachHang());
        dto.setCreatedDate(booking.getCreatedDate());
        return dto;
    }
    
    private Booking convertToEntity(BookingDTO dto) {
        Booking booking = new Booking();
        booking.setNgayNhanPhong(dto.getNgayNhanPhong());
        booking.setNgayTraPhong(dto.getNgayTraPhong());
        booking.setTongThanhToan(dto.getTongThanhToan());
        booking.setTrangThaiDatPhong(dto.getTrangThaiDatPhong() != null ? Booking.TrangThaiDatPhong.valueOf(dto.getTrangThaiDatPhong()) : null);
        booking.setGhiChuKhachHang(dto.getGhiChuKhachHang());
        return booking;
    }
} 