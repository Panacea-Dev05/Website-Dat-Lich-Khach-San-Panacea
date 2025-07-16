package panacea.website_dat_lich_khach_san.infrastructure.Enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LoaiKhachHangConverter implements AttributeConverter<LoaiKhachHang, String> {
    @Override
    public String convertToDatabaseColumn(LoaiKhachHang attribute) {
        return attribute != null ? attribute.getDbValue() : null;
    }
    @Override
    public LoaiKhachHang convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        for (LoaiKhachHang lkh : LoaiKhachHang.values()) {
            if (lkh.getDbValue().equals(dbData)) return lkh;
        }
        throw new IllegalArgumentException("Unknown loai_khach_hang: " + dbData);
    }
} 