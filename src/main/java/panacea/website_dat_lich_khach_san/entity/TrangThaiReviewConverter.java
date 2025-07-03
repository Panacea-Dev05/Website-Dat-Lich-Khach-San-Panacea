package panacea.website_dat_lich_khach_san.entity;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TrangThaiReviewConverter implements AttributeConverter<Review.TrangThaiReview, String> {
    @Override
    public String convertToDatabaseColumn(Review.TrangThaiReview value) {
        return value != null ? value.getValue() : null;
    }

    @Override
    public Review.TrangThaiReview convertToEntityAttribute(String dbData) {
        if (dbData == null) return null;
        return Review.TrangThaiReview.fromLabel(dbData);
    }
} 