package panacea.website_dat_lich_khach_san.infrastructure.Enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrangThaiYeuCauJpaConverter implements AttributeConverter<TrangThaiYeuCau, String> {
    
    @Override
    public String convertToDatabaseColumn(TrangThaiYeuCau attribute) {
        if (attribute == null) {
            System.out.println("[DEBUG] TrangThaiYeuCauJpaConverter: attribute is null, returning CHO_DUYET");
            return "CHO_DUYET"; // Default value thay vì null
        }
        String result = attribute.name();
        System.out.println("[DEBUG] TrangThaiYeuCauJpaConverter: converting " + attribute + " to " + result);
        return result;
    }
    
    @Override
    public TrangThaiYeuCau convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        
        // Ưu tiên enum name trước (CHO_DUYET, DA_DUYET, etc.)
        try {
            return TrangThaiYeuCau.valueOf(dbData);
        } catch (IllegalArgumentException e) {
            // Fallback cho dữ liệu cũ có thể sử dụng getValue()
            for (TrangThaiYeuCau status : TrangThaiYeuCau.values()) {
                if (status.getValue().equals(dbData)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Unknown trang_thai_yeu_cau: " + dbData);
        }
    }
}