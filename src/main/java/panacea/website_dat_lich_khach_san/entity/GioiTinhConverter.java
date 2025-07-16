package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GioiTinhConverter implements AttributeConverter<Customer.GioiTinh, String> {
    @Override
    public String convertToDatabaseColumn(Customer.GioiTinh gioiTinh) {
        return gioiTinh != null ? gioiTinh.getLabel() : null;
    }

    @Override
    public Customer.GioiTinh convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Customer.GioiTinh.fromString(dbData);
    }
} 