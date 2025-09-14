package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

@Data
public class RoomCreateDTO {
    private String soPhong;
    
    @NotNull(message = "Tầng không được để trống")
    @Min(value = 1, message = "Tầng phải từ 1 trở lên")
    @Max(value = 50, message = "Tầng không được vượt quá 50")
    private Byte tang;
    
    private String viewPhong;
    private String trangThai;
    private BigDecimal giaCoBan;
    private String ghiChu;
    private Integer hotelId;
    private Integer roomTypeId;
}