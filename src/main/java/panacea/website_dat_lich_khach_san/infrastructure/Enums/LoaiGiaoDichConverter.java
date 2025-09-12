package panacea.website_dat_lich_khach_san.infrastructure.Enums;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

@JsonComponent
public class LoaiGiaoDichConverter {
    
    public static class Deserializer extends JsonDeserializer<LoaiGiaoDich> {
        @Override
        public LoaiGiaoDich deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getValueAsString();
            
            if (value == null || value.trim().isEmpty()) {
                return null;
            }
            
            // First try to match by enum name (XUAT_KHO, NHAP_KHO, etc.)
            try {
                return LoaiGiaoDich.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                // If that fails, try to match by display value ("Xuất kho", "Nhập kho", etc.)
                for (LoaiGiaoDich loai : LoaiGiaoDich.values()) {
                    if (loai.getValue().equals(value) || loai.value().equals(value)) {
                        return loai;
                    }
                }
                
                // Handle specific cases that might cause issues
                switch (value.trim()) {
                    case "Xuất":
                    case "Xuất kho":
                        return LoaiGiaoDich.XUAT_KHO;
                    case "Nhập":
                    case "Nhập kho":
                        return LoaiGiaoDich.NHAP_KHO;
                    case "Điều chỉnh":
                        return LoaiGiaoDich.DIEU_CHINH;
                    case "Kiểm kê":
                        return LoaiGiaoDich.KIEM_KE;
                    default:
                        throw new IllegalArgumentException("Unknown LoaiGiaoDich value: " + value);
                }
            }
        }
    }
}