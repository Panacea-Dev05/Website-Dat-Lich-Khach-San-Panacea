package panacea.website_dat_lich_khach_san.infrastructure.Enums;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import panacea.website_dat_lich_khach_san.entity.Customer;

@Converter(autoApply = true)
public class TrangThaiCustomerConverter implements AttributeConverter<Customer.TrangThaiCustomer, String> {
    @Override
    public String convertToDatabaseColumn(Customer.TrangThaiCustomer attribute) {
        return attribute != null ? attribute.getValue() : null;
    }
    @Override
    public Customer.TrangThaiCustomer convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        for (Customer.TrangThaiCustomer ttc : Customer.TrangThaiCustomer.values()) {
            if (ttc.getValue().equals(dbData)) return ttc;
        }
        throw new IllegalArgumentException("Unknown trang_thai: " + dbData);
    }
} 