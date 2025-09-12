package panacea.website_dat_lich_khach_san.infrastructure.Enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrangThaiYeuCauJpaConverter implements AttributeConverter<TrangThaiYeuCau, String> {
    
    @Override
    public String convertToDatabaseColumn(TrangThaiYeuCau attribute) {
        return attribute != null ? attribute.getValue() : null;
    }
    
    @Override
    public TrangThaiYeuCau convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        for (TrangThaiYeuCau status : TrangThaiYeuCau.values()) {
            if (status.getValue().equals(dbData)) {
                return status;
            }
        }
        
        // Fallback for existing data that might use enum names
        try {
            return TrangThaiYeuCau.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unknown trang_thai_yeu_cau: " + dbData);
        }
    }
}