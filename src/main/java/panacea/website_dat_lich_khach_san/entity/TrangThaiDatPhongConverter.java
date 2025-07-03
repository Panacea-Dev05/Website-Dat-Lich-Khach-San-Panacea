package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrangThaiDatPhongConverter implements AttributeConverter<Booking.TrangThaiDatPhong, String> {
    @Override
    public String convertToDatabaseColumn(Booking.TrangThaiDatPhong value) {
        return value != null ? value.getLabel() : null;
    }

    @Override
    public Booking.TrangThaiDatPhong convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Booking.TrangThaiDatPhong.fromString(dbData);
    }
} 