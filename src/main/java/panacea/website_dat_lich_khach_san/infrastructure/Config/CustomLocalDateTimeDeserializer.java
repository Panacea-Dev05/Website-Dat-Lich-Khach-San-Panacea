package panacea.website_dat_lich_khach_san.infrastructure.Config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER_MINUTE = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FORMATTER_WITH_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final DateTimeFormatter DATETIME_FORMATTER_WITH_TZ = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateString = p.getValueAsString();
        
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Thử parse với format đầy đủ LocalDateTime trước
            if (dateString.contains("T")) {
                if (dateString.contains(".") && dateString.contains("Z")) {
                    return LocalDateTime.parse(dateString, DATETIME_FORMATTER_WITH_TZ);
                } else if (dateString.contains(".")) {
                    return LocalDateTime.parse(dateString, DATETIME_FORMATTER_WITH_MS);
                } else if (dateString.length() == 16) { // yyyy-MM-ddTHH:mm format
                    return LocalDateTime.parse(dateString, DATETIME_FORMATTER_MINUTE);
                } else {
                    return LocalDateTime.parse(dateString, DATETIME_FORMATTER);
                }
            } else {
                // Nếu chỉ có ngày (yyyy-MM-dd), chuyển thành LocalDateTime với thời gian 00:00:00
                LocalDate localDate = LocalDate.parse(dateString, DATE_FORMATTER);
                return localDate.atStartOfDay();
            }
        } catch (DateTimeParseException e) {
            // Nếu tất cả các format đều fail, throw exception với thông báo rõ ràng
            throw new IOException("Không thể parse ngày tháng: " + dateString + 
                                ". Format hỗ trợ: yyyy-MM-dd, yyyy-MM-ddTHH:mm, yyyy-MM-ddTHH:mm:ss, yyyy-MM-ddTHH:mm:ss.SSS", e);
        }
    }
}
