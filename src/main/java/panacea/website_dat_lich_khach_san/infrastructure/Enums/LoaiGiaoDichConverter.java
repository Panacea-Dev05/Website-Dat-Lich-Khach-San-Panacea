package panacea.website_dat_lich_khach_san.infrastructure.Enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import panacea.website_dat_lich_khach_san.entity.InventoryTransaction;

@Converter(autoApply = true)
public class LoaiGiaoDichConverter implements AttributeConverter<InventoryTransaction.LoaiGiaoDich, String> {
    @Override
    public String convertToDatabaseColumn(InventoryTransaction.LoaiGiaoDich attribute) {
        return attribute != null ? attribute.name() : null;
    }

    @Override
    public InventoryTransaction.LoaiGiaoDich convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        for (InventoryTransaction.LoaiGiaoDich lgd : InventoryTransaction.LoaiGiaoDich.values()) {
            if (lgd.name().equalsIgnoreCase(dbData) || lgd.getLabel().equalsIgnoreCase(dbData)) {
                return lgd;
            }
        }
        throw new IllegalArgumentException("Unknown loai_giao_dich: " + dbData);
    }
} 