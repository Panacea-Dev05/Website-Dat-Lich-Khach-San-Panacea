package panacea.website_dat_lich_khach_san.core.Admin.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AdminRoomService {
    
    @Autowired
    private RoomRepository roomRepository;
    
    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public RoomDTO getRoomById(Long id) {
        Optional<Room> room = roomRepository.findById(id);
        return room.map(this::convertToDTO).orElse(null);
    }
    
    public RoomDTO createRoom(RoomDTO roomDTO) {
        Room room = convertToEntity(roomDTO);
        Room savedRoom = roomRepository.save(room);
        return convertToDTO(savedRoom);
    }
    
    public RoomDTO updateRoom(Long id, RoomDTO roomDTO) {
        Optional<Room> existingRoom = roomRepository.findById(id);
        if (existingRoom.isPresent()) {
            Room room = existingRoom.get();
            room.setSoPhong(roomDTO.getSoPhong());
            room.setTang(roomDTO.getTang());
            room.setViewPhong(roomDTO.getViewPhong());
            room.setGiaCoBan(roomDTO.getGiaCoBan());
            room.setTrangThai(roomDTO.getTrangThai() != null ? panacea.website_dat_lich_khach_san.entity.Room.TrangThaiPhong.valueOf(roomDTO.getTrangThai()) : null);
            room.setGhiChu(roomDTO.getGhiChu());
            Room savedRoom = roomRepository.save(room);
            return convertToDTO(savedRoom);
        }
        return null;
    }
    
    public boolean deleteRoom(Long id) {
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    private RoomDTO convertToDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setSoPhong(room.getSoPhong());
        dto.setTang(room.getTang());
        dto.setViewPhong(room.getViewPhong());
        dto.setTrangThai(room.getTrangThai() != null ? room.getTrangThai().name() : null);
        dto.setGiaCoBan(room.getGiaCoBan());
        dto.setGhiChu(room.getGhiChu());
        dto.setHotelId(room.getHotel() != null ? room.getHotel().getId() : null);
        dto.setRoomTypeId(room.getRoomType() != null ? room.getRoomType().getId() : null);
        dto.setUuidId(room.getUuidId());
        dto.setCreatedDate(room.getCreatedDate());
        dto.setLastModifiedDate(room.getLastModifiedDate());
        return dto;
    }
    
    private Room convertToEntity(RoomDTO dto) {
        Room room = new Room();
        room.setSoPhong(dto.getSoPhong());
        room.setTang(dto.getTang());
        room.setViewPhong(dto.getViewPhong());
        room.setTrangThai(dto.getTrangThai() != null ? panacea.website_dat_lich_khach_san.entity.Room.TrangThaiPhong.valueOf(dto.getTrangThai()) : null);
        room.setGiaCoBan(dto.getGiaCoBan());
        room.setGhiChu(dto.getGhiChu());
        room.setUuidId(dto.getUuidId());
        room.setCreatedDate(dto.getCreatedDate());
        room.setLastModifiedDate(dto.getLastModifiedDate());
        return room;
    }
} 