package panacea.website_dat_lich_khach_san.infrastructure.Enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LoaiGiaoDichJpaConverter implements AttributeConverter<LoaiGiaoDich, String> {
    
    @Override
    public String convertToDatabaseColumn(LoaiGiaoDich attribute) {
        return attribute != null ? attribute.name() : null;
    }
    
    @Override
    public LoaiGiaoDich convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.trim().isEmpty()) {
            return null;
        }
        
        // First try to match by enum name (XUAT_KHO, NHAP_KHO, etc.)
        try {
            return LoaiGiaoDich.valueOf(dbData.toUpperCase());
        } catch (IllegalArgumentException e) {
            // If that fails, try to match by display value ("Xuất kho", "Nhập kho", etc.)
            for (LoaiGiaoDich loai : LoaiGiaoDich.values()) {
                if (loai.getValue().equals(dbData) || loai.value().equals(dbData)) {
                    return loai;
                }
            }
            
            // Handle specific cases that might cause issues
            switch (dbData.trim()) {
                case "Xuất":
                case "Xuất kho":
                    return LoaiGiaoDich.XUAT_KHO;
                case "Nhập":
                case "Nhập kho":
                    return LoaiGiaoDich.NHAP_KHO;
                case "Điều chỉnh":
                    return LoaiGiaoDich.DIEU_CHINH;
                case "Kiểm kê":
                    return LoaiGiaoDich.KIEM_KE;
                default:
                    throw new IllegalArgumentException("Unknown LoaiGiaoDich value: " + dbData);
            }
        }
    }
}