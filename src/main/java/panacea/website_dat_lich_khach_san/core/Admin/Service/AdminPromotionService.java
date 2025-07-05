package panacea.website_dat_lich_khach_san.core.Admin.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Promotion;
import panacea.website_dat_lich_khach_san.repository.PromotionRepository;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.PromotionDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminPromotionService {
    
    @Autowired
    private PromotionRepository promotionRepository;
    
    public List<PromotionDTO> getAllPromotions() {
        return promotionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public PromotionDTO getPromotionById(Integer id) {
        Optional<Promotion> promotion = promotionRepository.findById(id);
        return promotion.map(this::convertToDTO).orElse(null);
    }
    
    public PromotionDTO createPromotion(PromotionDTO promotionDTO) {
        Promotion promotion = convertToEntity(promotionDTO);
        Promotion savedPromotion = promotionRepository.save(promotion);
        return convertToDTO(savedPromotion);
    }
    
    public PromotionDTO updatePromotion(Integer id, PromotionDTO promotionDTO) {
        Optional<Promotion> existingPromotion = promotionRepository.findById(id);
        if (existingPromotion.isPresent()) {
            Promotion promotion = existingPromotion.get();
            promotion.setMaKhuyenMai(promotionDTO.getMaKhuyenMai());
            promotion.setTenKhuyenMai(promotionDTO.getTenKhuyenMai());
            promotion.setMoTa(promotionDTO.getMoTa());
            promotion.setLoaiGiamGia(Promotion.LoaiGiamGia.valueOf(promotionDTO.getLoaiGiamGia()));
            promotion.setGiaTriGiam(promotionDTO.getGiaTriGiam());
            promotion.setGiamToiDa(promotionDTO.getGiamToiDa());
            promotion.setNgayBatDau(promotionDTO.getNgayBatDau());
            promotion.setNgayKetThuc(promotionDTO.getNgayKetThuc());
            promotion.setSoLuongToiDa(promotionDTO.getSoLuongToiDa());
            promotion.setDieuKienApDung(promotionDTO.getDieuKienApDung());
            promotion.setTrangThai(Promotion.TrangThaiPromotion.valueOf(promotionDTO.getTrangThai()));
            Promotion savedPromotion = promotionRepository.save(promotion);
            return convertToDTO(savedPromotion);
        }
        return null;
    }
    
    public boolean deletePromotion(Integer id) {
        if (promotionRepository.existsById(id)) {
            promotionRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private PromotionDTO convertToDTO(Promotion promotion) {
        PromotionDTO dto = new PromotionDTO();
        dto.setId(promotion.getId());
        dto.setMaKhuyenMai(promotion.getMaKhuyenMai());
        dto.setTenKhuyenMai(promotion.getTenKhuyenMai());
        dto.setMoTa(promotion.getMoTa());
        dto.setLoaiGiamGia(promotion.getLoaiGiamGia().name());
        dto.setGiaTriGiam(promotion.getGiaTriGiam());
        dto.setGiamToiDa(promotion.getGiamToiDa());
        dto.setNgayBatDau(promotion.getNgayBatDau());
        dto.setNgayKetThuc(promotion.getNgayKetThuc());
        dto.setSoLuongToiDa(promotion.getSoLuongToiDa());
        dto.setDaSuDung(promotion.getDaSuDung());
        dto.setDieuKienApDung(promotion.getDieuKienApDung());
        dto.setTrangThai(promotion.getTrangThai().name());
        dto.setUuidId(promotion.getUuidId());
        dto.setCreatedDate(promotion.getCreatedDate());
        dto.setLastModifiedDate(promotion.getLastModifiedDate());
        return dto;
    }
    
    private Promotion convertToEntity(PromotionDTO dto) {
        Promotion promotion = new Promotion();
        promotion.setMaKhuyenMai(dto.getMaKhuyenMai());
        promotion.setTenKhuyenMai(dto.getTenKhuyenMai());
        promotion.setMoTa(dto.getMoTa());
        promotion.setLoaiGiamGia(Promotion.LoaiGiamGia.valueOf(dto.getLoaiGiamGia()));
        promotion.setGiaTriGiam(dto.getGiaTriGiam());
        promotion.setGiamToiDa(dto.getGiamToiDa());
        promotion.setNgayBatDau(dto.getNgayBatDau());
        promotion.setNgayKetThuc(dto.getNgayKetThuc());
        promotion.setSoLuongToiDa(dto.getSoLuongToiDa());
        promotion.setDieuKienApDung(dto.getDieuKienApDung());
        promotion.setTrangThai(Promotion.TrangThaiPromotion.valueOf(dto.getTrangThai()));
        return promotion;
    }
    
    // Kiểm tra promotion hợp lệ
    public boolean validatePromotion(String promoCode) {
        List<Promotion> promos = promotionRepository.findAll().stream()
            .filter(p -> p.getMaKhuyenMai().equalsIgnoreCase(promoCode))
            .toList();
        if (promos.isEmpty()) return false;
        Promotion promo = promos.get(0);
        return promo.getTrangThai() == Promotion.TrangThaiPromotion.HOAT_DONG
            && promo.getNgayBatDau().isBefore(java.time.LocalDate.now().plusDays(1))
            && promo.getNgayKetThuc().isAfter(java.time.LocalDate.now().minusDays(1))
            && (promo.getSoLuongToiDa() == null || promo.getDaSuDung() < promo.getSoLuongToiDa());
    }
    
    // Filter danh sách promotion
    public List<PromotionDTO> filterPromotions(String status, String keyword) {
        return promotionRepository.findAll().stream()
            .filter(p -> status == null || p.getTrangThai().name().equalsIgnoreCase(status))
            .filter(p -> keyword == null || p.getTenKhuyenMai().toLowerCase().contains(keyword.toLowerCase()) || p.getMaKhuyenMai().toLowerCase().contains(keyword.toLowerCase()))
            .map(this::convertToDTO)
            .toList();
    }
    
    // Thống kê hiệu quả promotion (số lần sử dụng, doanh thu tạo ra)
    public PromotionStats getPromotionStats(Integer promoId) {
        Optional<Promotion> promoOpt = promotionRepository.findById(promoId);
        if (promoOpt.isEmpty()) return null;
        Promotion promo = promoOpt.get();
        int soLanSuDung = promo.getDaSuDung() != null ? promo.getDaSuDung() : 0;
        java.math.BigDecimal doanhThu = java.math.BigDecimal.ZERO;
        if (promo.getBookings() != null) {
            for (var b : promo.getBookings()) {
                if (b.getTongThanhToan() != null) doanhThu = doanhThu.add(b.getTongThanhToan());
            }
        }
        return new PromotionStats(soLanSuDung, doanhThu);
    }
    
    public static class PromotionStats {
        public int soLanSuDung;
        public java.math.BigDecimal doanhThu;
        public PromotionStats(int soLanSuDung, java.math.BigDecimal doanhThu) {
            this.soLanSuDung = soLanSuDung;
            this.doanhThu = doanhThu;
        }
    }
    
    // Vô hiệu hóa promotion
    public PromotionDTO disablePromotion(Integer promoId) {
        Optional<Promotion> promoOpt = promotionRepository.findById(promoId);
        if (promoOpt.isEmpty()) return null;
        Promotion promo = promoOpt.get();
        promo.setTrangThai(Promotion.TrangThaiPromotion.TAM_NGUNG);
        Promotion saved = promotionRepository.save(promo);
        return convertToDTO(saved);
    }
} 