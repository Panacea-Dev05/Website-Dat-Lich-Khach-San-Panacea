package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrangThaiThanhToanConverter implements AttributeConverter<Booking.TrangThaiThanhToan, String> {
    @Override
    public String convertToDatabaseColumn(Booking.TrangThaiThanhToan value) {
        return value != null ? value.getLabel() : null;
    }

    @Override
    public Booking.TrangThaiThanhToan convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Booking.TrangThaiThanhToan.fromString(dbData);
    }
} 