package panacea.website_dat_lich_khach_san.infrastructure.Enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrangThaiDuyetJpaConverter implements AttributeConverter<TrangThaiDuyet, String> {

    @Override
    public String convertToDatabaseColumn(TrangThaiDuyet attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getValue();
    }

    @Override
    public TrangThaiDuyet convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        return TrangThaiDuyet.fromValue(dbData);
    }
}