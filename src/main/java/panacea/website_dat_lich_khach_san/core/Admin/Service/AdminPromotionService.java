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
} 