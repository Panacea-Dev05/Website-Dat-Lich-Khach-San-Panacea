package panacea.website_dat_lich_khach_san.infrastructure.Enums;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class TrangThaiYeuCauConverter {

    public static class Serializer extends JsonSerializer<TrangThaiYeuCau> {
        @Override
        public void serialize(TrangThaiYeuCau value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value != null) {
                gen.writeString(value.name());
            } else {
                gen.writeNull();
            }
        }
    }

    public static class Deserializer extends JsonDeserializer<TrangThaiYeuCau> {
        @Override
        public TrangThaiYeuCau deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getValueAsString();
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            
            try {
                // Try to parse as enum name first
                return TrangThaiYeuCau.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                // If that fails, try to find by value
                for (TrangThaiYeuCau status : TrangThaiYeuCau.values()) {
                    if (status.getValue().equals(value)) {
                        return status;
                    }
                }
                throw new IllegalArgumentException("Unknown TrangThaiYeuCau: " + value);
            }
        }
    }
}