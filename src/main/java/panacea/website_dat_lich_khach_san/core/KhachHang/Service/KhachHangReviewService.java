package panacea.website_dat_lich_khach_san.core.KhachHang.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Review;
import panacea.website_dat_lich_khach_san.entity.Customer;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.repository.ReviewRepository;
import panacea.website_dat_lich_khach_san.repository.CustomerRepository;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.ReviewDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service xử lý đánh giá cho khách hàng
 */
@Service
public class KhachHangReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    /**
     * Tạo đánh giá mới cho phòng
     */
    public boolean createReview(ReviewDTO reviewDTO) {
        try {
            System.out.println("[DEBUG] ========== BẮT ĐẦU TẠO ĐÁNH GIÁ ==========");
            System.out.println("[DEBUG] Dữ liệu nhận được:");
            System.out.println("[DEBUG] - Customer Email: " + reviewDTO.getCustomerEmail());
            System.out.println("[DEBUG] - Booking ID: " + reviewDTO.getBookingId());
            System.out.println("[DEBUG] - Room Type ID: " + reviewDTO.getRoomTypeId());
            System.out.println("[DEBUG] - Diem Tong Quan: " + reviewDTO.getDiemTongQuan());
            System.out.println("[DEBUG] - Diem Sach Se: " + reviewDTO.getDiemSachSe());
            System.out.println("[DEBUG] - Diem Dich Vu: " + reviewDTO.getDiemDichVu());
            System.out.println("[DEBUG] - Diem Vi Tri: " + reviewDTO.getDiemViTri());
            System.out.println("[DEBUG] - Diem Gia Ca: " + reviewDTO.getDiemGiaCa());
            System.out.println("[DEBUG] - Binh Luan: " + reviewDTO.getBinhLuan());
            
            // Tìm khách hàng theo email
            Optional<Customer> customerOpt = customerRepository.findByEmail(reviewDTO.getCustomerEmail());
            if (customerOpt.isEmpty()) {
                System.out.println("[ERROR] Không tìm thấy khách hàng với email: " + reviewDTO.getCustomerEmail());
                return false;
            }
            System.out.println("[DEBUG] Tìm thấy khách hàng: " + customerOpt.get().getHo() + " " + customerOpt.get().getTen() + " (ID: " + customerOpt.get().getId() + ")");
            
            // Tìm booking theo ID hoặc tạo booking mặc định
            Integer bookingId = reviewDTO.getBookingId();
            Optional<Booking> bookingOpt;
            
            System.out.println("[DEBUG] Tìm kiếm booking...");
            System.out.println("[DEBUG] - Booking ID từ DTO: " + bookingId);
            
            if (bookingId != null) {
                System.out.println("[DEBUG] Tìm booking theo ID: " + bookingId);
                bookingOpt = bookingRepository.findById(bookingId);
                System.out.println("[DEBUG] - Kết quả tìm booking theo ID: " + (bookingOpt.isPresent() ? "Tìm thấy" : "Không tìm thấy"));
            } else {
                System.out.println("[DEBUG] Booking ID null, tìm booking chưa được đánh giá của khách hàng...");
                // Tìm booking chưa được đánh giá của khách hàng này
                List<Booking> customerBookings = bookingRepository.findByKhachHangId(customerOpt.get().getId());
                System.out.println("[DEBUG] - Tổng số booking của khách hàng: " + customerBookings.size());
                
                // Tìm booking chưa được đánh giá
                Booking unratedBooking = null;
                for (Booking booking : customerBookings) {
                    List<Review> existingReviews = reviewRepository.findByDatPhongIdAndKhachHangId(
                        booking.getId(), 
                        customerOpt.get().getId()
                    );
                    if (existingReviews.isEmpty()) {
                        unratedBooking = booking;
                        break;
                    }
                }
                
                if (unratedBooking != null) {
                    bookingOpt = Optional.of(unratedBooking);
                    bookingId = unratedBooking.getId();
                    System.out.println("[DEBUG] - Tìm thấy booking chưa đánh giá ID: " + bookingId);
                } else {
                    // Nếu tất cả booking đã được đánh giá, không cho phép đánh giá thêm
                    System.out.println("[DEBUG] Tất cả booking đã được đánh giá, không thể tạo đánh giá mới");
                    throw new RuntimeException("Khách hàng đã đánh giá tất cả booking của mình");
                }
            }
            
            if (bookingOpt.isEmpty()) {
                System.out.println("[ERROR] Không tìm thấy booking với ID: " + bookingId);
                return false;
            }
            
            System.out.println("[DEBUG] Sử dụng booking ID: " + bookingId);
            
            // Logic mới: Cho phép đánh giá nhiều lần, mỗi booking 1 lần
            System.out.println("[DEBUG] Logic mới: Cho phép đánh giá nhiều lần");
            
            // Tạo đánh giá mới
            System.out.println("[DEBUG] Tạo đánh giá mới...");
            Review review = new Review();
            review.setDatPhongId(bookingId);
            review.setKhachHangId(customerOpt.get().getId());
            review.setKhachSanId(1); // Set khach_san_id = 1 (default hotel ID)
            review.setDiemTongQuan(reviewDTO.getDiemTongQuan());
            review.setDiemSachSe(reviewDTO.getDiemSachSe());
            review.setDiemDichVu(reviewDTO.getDiemDichVu());
            review.setDiemViTri(reviewDTO.getDiemViTri());
            review.setDiemGiaCa(reviewDTO.getDiemGiaCa());
            review.setBinhLuan(reviewDTO.getBinhLuan());
            review.setNgayDanhGia(LocalDateTime.now());
            review.setTrangThai(Review.TrangThaiReview.CHO_DUYET);
            review.setUuidId(UUID.randomUUID());
            review.setCreatedDate(System.currentTimeMillis());
            
            System.out.println("[DEBUG] Lưu đánh giá vào database...");
            reviewRepository.save(review);
            System.out.println("[SUCCESS] Đã tạo đánh giá thành công cho booking ID: " + bookingId);
            System.out.println("[DEBUG] ========== KẾT THÚC TẠO ĐÁNH GIÁ ==========");
            return true;
            
        } catch (Exception e) {
            System.out.println("[ERROR] Lỗi khi tạo đánh giá: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy danh sách đánh giá của phòng
     */
    public List<ReviewDTO> getRoomReviews(Integer roomTypeId) {
        try {
            // Tìm tất cả booking của room type này
            List<Booking> bookings = bookingRepository.findByRoomTypeId(roomTypeId);
            if (bookings.isEmpty()) {
                return List.of();
            }
            
            List<Integer> bookingIds = bookings.stream()
                .map(Booking::getId)
                .collect(Collectors.toList());
            
            // Lấy đánh giá đã được duyệt
            List<Review> reviews = reviewRepository.findByDatPhongIdInAndTrangThai(
                bookingIds, 
                Review.TrangThaiReview.DA_DUYET
            );
            
            return reviews.stream().map(this::convertToDTO).collect(Collectors.toList());
            
        } catch (Exception e) {
            System.out.println("[ERROR] Lỗi khi lấy đánh giá phòng: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Lấy đánh giá của khách hàng theo email
     */
    public List<ReviewDTO> getCustomerReviews(String email) {
        try {
            Optional<Customer> customerOpt = customerRepository.findByEmail(email);
            if (customerOpt.isEmpty()) {
                return List.of();
            }
            
            List<Review> reviews = reviewRepository.findByKhachHangId(customerOpt.get().getId());
            return reviews.stream().map(this::convertToDTO).collect(Collectors.toList());
            
        } catch (Exception e) {
            System.out.println("[ERROR] Lỗi khi lấy đánh giá khách hàng: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }
    
    /**
     * Tính điểm trung bình của phòng
     */
    public Double getAverageRating(Integer roomTypeId) {
        try {
            List<ReviewDTO> reviews = getRoomReviews(roomTypeId);
            if (reviews.isEmpty()) {
                return 0.0;
            }
            
            double totalRating = reviews.stream()
                .mapToDouble(r -> r.getDiemTongQuan() != null ? r.getDiemTongQuan().doubleValue() : 0.0)
                .average()
                .orElse(0.0);
            
            return Math.round(totalRating * 10.0) / 10.0; // Làm tròn 1 chữ số thập phân
            
        } catch (Exception e) {
            System.out.println("[ERROR] Lỗi khi tính điểm trung bình: " + e.getMessage());
            e.printStackTrace();
            return 0.0;
        }
    }
    
    /**
     * Chuyển đổi Review entity sang ReviewDTO
     */
    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setBookingId(review.getDatPhongId());
        dto.setCustomerId(review.getKhachHangId());
        dto.setDiemTongQuan(review.getDiemTongQuan());
        dto.setDiemSachSe(review.getDiemSachSe());
        dto.setDiemDichVu(review.getDiemDichVu());
        dto.setDiemViTri(review.getDiemViTri());
        dto.setDiemGiaCa(review.getDiemGiaCa());
        dto.setBinhLuan(review.getBinhLuan());
        dto.setNgayDanhGia(review.getNgayDanhGia());
        dto.setTrangThai(review.getTrangThai().name());
        dto.setUuidId(review.getUuidId());
        // Xử lý createdDate có thể null
        if (review.getCreatedDate() != null) {
            dto.setCreatedDate(LocalDateTime.ofInstant(
                java.time.Instant.ofEpochMilli(review.getCreatedDate()),
                java.time.ZoneId.systemDefault()
            ));
        } else {
            dto.setCreatedDate(LocalDateTime.now()); // Fallback nếu null
        }
        
        // Lấy tên khách hàng
        if (review.getCustomer() != null) {
            dto.setCustomerName(review.getCustomer().getHo() + " " + review.getCustomer().getTen());
        }
        
        // Lấy tên loại phòng
        if (review.getBooking() != null && review.getBooking().getRoomType() != null) {
            dto.setRoomNumber(review.getBooking().getRoomType().getTenLoaiPhong());
        }
        
        return dto;
    }
}
