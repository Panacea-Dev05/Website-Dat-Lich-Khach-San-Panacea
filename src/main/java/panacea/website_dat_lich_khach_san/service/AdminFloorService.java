package panacea.website_dat_lich_khach_san.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import panacea.website_dat_lich_khach_san.dto.FloorDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomDTO;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.entity.Room.TrangThaiPhong;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;

@Service
public class AdminFloorService {

    @Autowired
    private RoomRepository roomRepository;

    /**
     * Lấy danh sách tất cả tầng với thống kê
     */
    public List<FloorDTO> getAllFloors() {
        List<Byte> distinctFloors = roomRepository.findDistinctFloors();
        List<FloorDTO> floorDTOs = new ArrayList<>();
        
        for (Byte floorNumber : distinctFloors) {
            List<Room> roomsOnFloor = roomRepository.findByTang(floorNumber);
            
            int totalRooms = roomsOnFloor.size();
            int availableRooms = 0;
            int occupiedRooms = 0;
            int maintenanceRooms = 0;
            
            for (Room room : roomsOnFloor) {
                switch (room.getTrangThai()) {
                    case SAN_SANG:
                        availableRooms++;
                        break;
                    case DANG_SU_DUNG:
                        occupiedRooms++;
                        break;
                    case BAO_TRI:
                        maintenanceRooms++;
                        break;
                    default:
                        break;
                }
            }
            
            FloorDTO floorDTO = new FloorDTO(floorNumber, totalRooms, availableRooms, occupiedRooms, maintenanceRooms);
            floorDTOs.add(floorDTO);
        }
        
        return floorDTOs;
    }

    /**
     * Lấy danh sách phòng theo tầng
     */
    public List<RoomDTO> getRoomsByFloor(Byte floorNumber) {
        List<Room> rooms = roomRepository.findByTang(floorNumber);
        return rooms.stream()
                .map(this::convertToRoomDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cập nhật tầng cho một phòng
     */
    @Transactional
    public void updateRoomFloor(Integer roomId, Byte newFloor) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng với ID: " + roomId));
        
        room.setTang(newFloor);
        roomRepository.save(room);
    }

    /**
     * Di chuyển tất cả phòng từ tầng này sang tầng khác
     */
    @Transactional
    public void moveAllRoomsToFloor(Byte fromFloor, Byte toFloor) {
        List<Room> roomsToMove = roomRepository.findByTang(fromFloor);
        
        for (Room room : roomsToMove) {
            room.setTang(toFloor);
        }
        
        roomRepository.saveAll(roomsToMove);
    }

    /**
     * Lấy thống kê của một tầng cụ thể
     */
    public FloorDTO getFloorStatistics(Byte floorNumber) {
        List<Room> roomsOnFloor = roomRepository.findByTang(floorNumber);
        
        if (roomsOnFloor.isEmpty()) {
            return null;
        }
        
        int totalRooms = roomsOnFloor.size();
        int availableRooms = 0;
        int occupiedRooms = 0;
        int maintenanceRooms = 0;
        
        for (Room room : roomsOnFloor) {
            switch (room.getTrangThai()) {
                case SAN_SANG:
                    availableRooms++;
                    break;
                case DANG_SU_DUNG:
                    occupiedRooms++;
                    break;
                case BAO_TRI:
                    maintenanceRooms++;
                    break;
                default:
                    break;
            }
        }
        
        return new FloorDTO(floorNumber, totalRooms, availableRooms, occupiedRooms, maintenanceRooms);
    }

    /**
     * Chuyển đổi Room entity thành RoomDTO
     */
    private RoomDTO convertToRoomDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setSoPhong(room.getSoPhong());
        dto.setTang(room.getTang());
        dto.setTrangThai(room.getTrangThai() != null ? room.getTrangThai().name() : null);
        dto.setGiaCoBan(room.getGiaCoBan());
        dto.setViewPhong(room.getViewPhong());
        dto.setGhiChu(room.getGhiChu());
        dto.setUuidId(room.getUuidId());
        dto.setCreatedDate(room.getCreatedDate());
        dto.setLastModifiedDate(room.getLastModifiedDate());
        if (room.getRoomType() != null) {
            dto.setRoomTypeId(room.getRoomType().getId());
            dto.setRoomTypeName(room.getRoomType().getTenLoaiPhong());
        }
        return dto;
    }
}