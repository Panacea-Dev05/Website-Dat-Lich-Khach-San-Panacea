package panacea.website_dat_lich_khach_san.infrastructure.Config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@JsonComponent
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter DATETIME_FORMATTER_WITH_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        String dateString = node.asText();
        
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        
        try {
            // Thử parse với format đầy đủ LocalDateTime trước
            if (dateString.contains("T")) {
                if (dateString.contains(".")) {
                    return LocalDateTime.parse(dateString, DATETIME_FORMATTER_WITH_MS);
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
                                ". Format hỗ trợ: yyyy-MM-dd, yyyy-MM-ddTHH:mm:ss, yyyy-MM-ddTHH:mm:ss.SSS", e);
        }
    }
}
