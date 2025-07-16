package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrangThaiPaymentConverter implements AttributeConverter<Payment.TrangThaiPayment, String> {
    @Override
    public String convertToDatabaseColumn(Payment.TrangThaiPayment value) {
        return value != null ? value.getValue() : null;
    }

    @Override
    public Payment.TrangThaiPayment convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Payment.TrangThaiPayment.fromLabel(dbData);
    }
} 