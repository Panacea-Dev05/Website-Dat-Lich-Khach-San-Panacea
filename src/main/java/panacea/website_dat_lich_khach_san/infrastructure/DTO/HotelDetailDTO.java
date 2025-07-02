package panacea.website_dat_lich_khach_san.infrastructure.DTO;

import lombok.Data;
import java.util.List;

@Data
public class HotelDetailDTO {
    private HotelDTO hotel;
    private List<HotelAmenitiesDTO> amenities;
    private List<RoomDTO> rooms;
} 