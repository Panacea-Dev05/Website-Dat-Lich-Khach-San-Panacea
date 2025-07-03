package panacea.website_dat_lich_khach_san.core.Admin.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Review;
import panacea.website_dat_lich_khach_san.repository.ReviewRepository;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.ReviewDTO;
import panacea.website_dat_lich_khach_san.repository.BookingDetailRepository;
import panacea.website_dat_lich_khach_san.entity.BookingDetail;
import panacea.website_dat_lich_khach_san.entity.Room;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

@Service
public class AdminReviewService {
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private BookingDetailRepository bookingDetailRepository;
    
    public List<ReviewDTO> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public ReviewDTO getReviewById(Long id) {
        Optional<Review> review = reviewRepository.findById(id);
        return review.map(this::convertToDTO).orElse(null);
    }
    
    public ReviewDTO createReview(ReviewDTO reviewDTO) {
        Review review = convertToEntity(reviewDTO);
        Review savedReview = reviewRepository.save(review);
        return convertToDTO(savedReview);
    }
    
    public ReviewDTO updateReview(Long id, ReviewDTO reviewDTO) {
        Optional<Review> existingReview = reviewRepository.findById(id);
        if (existingReview.isPresent()) {
            Review review = existingReview.get();
            review.setDiemTongQuan(reviewDTO.getDiemDanhGia() != null ? reviewDTO.getDiemDanhGia().byteValue() : null);
            review.setBinhLuan(reviewDTO.getNoiDung());
            review.setTrangThai(reviewDTO.getTrangThai() != null ? panacea.website_dat_lich_khach_san.entity.Review.TrangThaiReview.valueOf(reviewDTO.getTrangThai()) : null);
            Review savedReview = reviewRepository.save(review);
            return convertToDTO(savedReview);
        }
        return null;
    }
    
    public boolean deleteReview(Long id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setCustomerId(review.getKhachHangId());
        dto.setRoomId(null); // No roomId in Review entity
        dto.setDiemDanhGia(review.getDiemTongQuan() != null ? review.getDiemTongQuan().intValue() : null);
        dto.setNoiDung(review.getBinhLuan());
        dto.setComment(review.getBinhLuan());
        dto.setTrangThai(review.getTrangThai() != null ? review.getTrangThai().name() : null);
        // Map trạng thái nội bộ sang status cho template
        if (review.getTrangThai() != null) {
            switch (review.getTrangThai()) {
                case DA_DUYET:
                    dto.setStatus("Approved");
                    break;
                case CHO_DUYET:
                    dto.setStatus("Pending");
                    break;
                case DA_AN:
                    dto.setStatus("Rejected");
                    break;
                default:
                    dto.setStatus("Unknown");
            }
        } else {
            dto.setStatus("Unknown");
        }
        dto.setThoiGianDanhGia(review.getNgayDanhGia());
        dto.setUuidId(review.getUuidId());
        if (review.getCreatedDate() != null) {
            dto.setCreatedDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(review.getCreatedDate()), ZoneId.systemDefault()));
        } else {
            dto.setCreatedDate(null);
        }
        if (review.getCustomer() != null) {
            dto.setCustomerName(review.getCustomer().getHo() + " " + review.getCustomer().getTen());
        } else {
            dto.setCustomerName("Không rõ");
        }
        // Set roomNumber if possible
        if (review.getDatPhongId() != null) {
            java.util.List<BookingDetail> details = bookingDetailRepository.findByDatPhongId(review.getDatPhongId());
            if (details != null && !details.isEmpty() && details.get(0).getRoom() != null) {
                dto.setRoomNumber(details.get(0).getRoom().getSoPhong());
            } else {
                dto.setRoomNumber("Không rõ");
            }
        } else {
            dto.setRoomNumber("Không rõ");
        }
        return dto;
    }
    
    private Review convertToEntity(ReviewDTO dto) {
        Review review = new Review();
        review.setDiemTongQuan(dto.getDiemDanhGia() != null ? dto.getDiemDanhGia().byteValue() : null);
        review.setBinhLuan(dto.getNoiDung());
        review.setTrangThai(dto.getTrangThai() != null ? panacea.website_dat_lich_khach_san.entity.Review.TrangThaiReview.valueOf(dto.getTrangThai()) : null);
        review.setNgayDanhGia(dto.getThoiGianDanhGia());
        review.setUuidId(dto.getUuidId());
        if (dto.getCreatedDate() != null) {
            review.setCreatedDate(dto.getCreatedDate().toInstant(ZoneOffset.ofTotalSeconds(ZoneId.systemDefault().getRules().getOffset(dto.getCreatedDate()).getTotalSeconds())).toEpochMilli());
        } else {
            review.setCreatedDate(null);
        }
        return review;
    }
} 