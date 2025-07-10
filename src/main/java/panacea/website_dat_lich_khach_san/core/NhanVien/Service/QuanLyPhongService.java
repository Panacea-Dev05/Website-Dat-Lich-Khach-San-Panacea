package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.entity.RoomType;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomCreateDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomTypeCreateDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomTypeDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomUpdateDTO;
import panacea.website_dat_lich_khach_san.infrastructure.Exception.BadRequestException;
import panacea.website_dat_lich_khach_san.infrastructure.Exception.ResourceNotFoundException;
import panacea.website_dat_lich_khach_san.repository.HotelRepository;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;
import panacea.website_dat_lich_khach_san.repository.RoomTypeRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuanLyPhongService {
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    
    @Autowired
    private HotelRepository hotelRepository;

    public String getStaffName() {
        return "Nguyễn Văn A";
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
    
    // CRUD Operations for Rooms
    public List<RoomDTO> getAllRoomsDTO() {
        return roomRepository.findAll().stream()
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public Page<RoomDTO> getAllRoomsPaged(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return roomRepository.findAll(pageable)
                .map(RoomDTO::fromEntity);
    }
    
    public RoomDTO getRoomById(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với ID: " + id));
        return RoomDTO.fromEntity(room);
    }
    
    public RoomDTO createRoom(RoomCreateDTO roomCreateDTO) {
        // Validate hotel exists
        Hotel hotel = hotelRepository.findById(roomCreateDTO.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn với ID: " + roomCreateDTO.getHotelId()));
        
        // Validate room type exists
        RoomType roomType = roomTypeRepository.findById(roomCreateDTO.getRoomTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại phòng với ID: " + roomCreateDTO.getRoomTypeId()));
        
        // Check if room number already exists in the hotel
        if (roomRepository.findByHotelIdAndSoPhong(hotel.getId(), roomCreateDTO.getSoPhong()).isPresent()) {
            throw new BadRequestException("Số phòng " + roomCreateDTO.getSoPhong() + " đã tồn tại trong khách sạn này");
        }
        
        Room room = new Room();
        room.setSoPhong(roomCreateDTO.getSoPhong());
        room.setTang(roomCreateDTO.getTang());
        room.setViewPhong(roomCreateDTO.getViewPhong());
        room.setGiaCoBan(roomCreateDTO.getGiaCoBan());
        room.setGhiChu(roomCreateDTO.getGhiChu());
        room.setHotel(hotel);
        room.setRoomType(roomType);
        
        // Set trang thai
        if (roomCreateDTO.getTrangThai() != null) {
            room.setTrangThai(Room.TrangThaiPhong.valueOf(roomCreateDTO.getTrangThai()));
        }
        
        Room savedRoom = roomRepository.save(room);
        return RoomDTO.fromEntity(savedRoom);
    }
    
    public RoomDTO updateRoom(Integer id, RoomUpdateDTO roomUpdateDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với ID: " + id));
        
        // Check if new room number conflicts with existing rooms in the same hotel
        if (roomUpdateDTO.getSoPhong() != null && !roomUpdateDTO.getSoPhong().equals(room.getSoPhong())) {
            if (roomRepository.findByHotelIdAndSoPhong(room.getHotel().getId(), roomUpdateDTO.getSoPhong()).isPresent()) {
                throw new BadRequestException("Số phòng " + roomUpdateDTO.getSoPhong() + " đã tồn tại trong khách sạn này");
            }
        }
        
        // Update room type if provided
        if (roomUpdateDTO.getRoomTypeId() != null) {
            RoomType roomType = roomTypeRepository.findById(roomUpdateDTO.getRoomTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại phòng với ID: " + roomUpdateDTO.getRoomTypeId()));
            room.setRoomType(roomType);
        }
        
        if (roomUpdateDTO.getSoPhong() != null) {
            room.setSoPhong(roomUpdateDTO.getSoPhong());
        }
        if (roomUpdateDTO.getTang() != null) {
            room.setTang(roomUpdateDTO.getTang());
        }
        if (roomUpdateDTO.getViewPhong() != null) {
            room.setViewPhong(roomUpdateDTO.getViewPhong());
        }
        if (roomUpdateDTO.getGiaCoBan() != null) {
            room.setGiaCoBan(roomUpdateDTO.getGiaCoBan());
        }
        if (roomUpdateDTO.getGhiChu() != null) {
            room.setGhiChu(roomUpdateDTO.getGhiChu());
        }
        if (roomUpdateDTO.getTrangThai() != null) {
            room.setTrangThai(Room.TrangThaiPhong.valueOf(roomUpdateDTO.getTrangThai()));
        }
        
        Room updatedRoom = roomRepository.save(room);
        return RoomDTO.fromEntity(updatedRoom);
    }
    
    public void deleteRoom(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với ID: " + id));
        
        // Check if room is currently in use
        if (room.getTrangThai() == Room.TrangThaiPhong.DANG_SU_DUNG) {
            throw new BadRequestException("Không thể xóa phòng đang được sử dụng");
        }
        
        roomRepository.delete(room);
    }
    
    // Search and Filter Operations
    public List<RoomDTO> searchRoomsBySoPhong(String soPhong) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getSoPhong() != null && room.getSoPhong().contains(soPhong))
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<RoomDTO> getRoomsByHotel(Integer hotelId) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getHotel() != null && room.getHotel().getId().equals(hotelId))
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<RoomDTO> getRoomsByRoomType(Integer roomTypeId) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getRoomType() != null && room.getRoomType().getId().equals(roomTypeId))
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<RoomDTO> getRoomsByTrangThai(String trangThai) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getTrangThai() != null && room.getTrangThai().name().equals(trangThai))
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<RoomDTO> getRoomsByHotelAndTrangThai(Integer hotelId, String trangThai) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getHotel() != null && room.getHotel().getId().equals(hotelId))
                .filter(room -> room.getTrangThai() != null && room.getTrangThai().name().equals(trangThai))
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<RoomDTO> getRoomsByTang(Byte tang) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getTang() != null && room.getTang().equals(tang))
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<RoomDTO> getRoomsByHotelAndTang(Integer hotelId, Byte tang) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getHotel() != null && room.getHotel().getId().equals(hotelId))
                .filter(room -> room.getTang() != null && room.getTang().equals(tang))
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<RoomDTO> getRoomsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getGiaCoBan() != null &&
                        room.getGiaCoBan().compareTo(minPrice) >= 0 &&
                        room.getGiaCoBan().compareTo(maxPrice) <= 0)
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<RoomDTO> getRoomsByHotelAndPriceRange(Integer hotelId, BigDecimal minPrice, BigDecimal maxPrice) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getHotel() != null && room.getHotel().getId().equals(hotelId))
                .filter(room -> room.getGiaCoBan() != null &&
                        room.getGiaCoBan().compareTo(minPrice) >= 0 &&
                        room.getGiaCoBan().compareTo(maxPrice) <= 0)
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<RoomDTO> getRoomsByHotelPaged(Integer hotelId, int page, int size) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getHotel() != null && room.getHotel().getId().equals(hotelId))
                .skip((long) page * size)
                .limit(size)
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<RoomDTO> getRoomsByHotelAndTrangThaiPaged(Integer hotelId, String trangThai, int page, int size) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getHotel() != null && room.getHotel().getId().equals(hotelId))
                .filter(room -> room.getTrangThai() != null && room.getTrangThai().name().equals(trangThai))
                .skip((long) page * size)
                .limit(size)
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // Room Type Operations
    public List<RoomTypeDTO> getAllRoomTypes() {
        return roomTypeRepository.findAll().stream()
                .map(RoomTypeDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public RoomTypeDTO getRoomTypeById(Integer id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại phòng với ID: " + id));
        return RoomTypeDTO.fromEntity(roomType);
    }
    
    public List<RoomTypeDTO> searchRoomTypesByTenLoaiPhong(String tenLoaiPhong) {
        return roomTypeRepository.findByTenLoaiPhongContaining(tenLoaiPhong).stream()
                .map(RoomTypeDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<RoomTypeDTO> getRoomTypesBySoGiuong(Byte soGiuong) {
        return roomTypeRepository.findBySoGiuong(soGiuong).stream()
                .map(RoomTypeDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<RoomTypeDTO> getRoomTypesBySucChua(Byte sucChua) {
        return roomTypeRepository.findBySucChuaToiDaGreaterThanEqual(sucChua).stream()
                .map(RoomTypeDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<RoomTypeDTO> getRoomTypesByDienTich(BigDecimal minDienTich, BigDecimal maxDienTich) {
        return roomTypeRepository.findByDienTichBetween(minDienTich, maxDienTich).stream()
                .map(RoomTypeDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // Create room type
    public RoomTypeDTO createRoomType(RoomTypeCreateDTO roomTypeCreateDTO) {
        if (roomTypeCreateDTO.getMaLoaiPhong() == null || roomTypeCreateDTO.getMaLoaiPhong().trim().isEmpty()) {
            throw new BadRequestException("Vui lòng nhập mã loại phòng.");
        }
        if (roomTypeCreateDTO.getTenLoaiPhong() == null || roomTypeCreateDTO.getTenLoaiPhong().trim().isEmpty()) {
            throw new BadRequestException("Vui lòng nhập tên loại phòng.");
        }
        if (roomTypeRepository.findByMaLoaiPhong(roomTypeCreateDTO.getMaLoaiPhong()).isPresent()) {
            throw new BadRequestException("Mã loại phòng đã tồn tại.");
        }
        RoomType roomType = new RoomType();
        roomType.setMaLoaiPhong(roomTypeCreateDTO.getMaLoaiPhong());
        roomType.setTenLoaiPhong(roomTypeCreateDTO.getTenLoaiPhong());
        roomType.setDienTich(roomTypeCreateDTO.getDienTich() != null ? roomTypeCreateDTO.getDienTich() : java.math.BigDecimal.ZERO);
        roomType.setSoGiuong(roomTypeCreateDTO.getSoGiuong() != null ? roomTypeCreateDTO.getSoGiuong() : (byte) 1);
        roomType.setLoaiGiuong(roomTypeCreateDTO.getLoaiGiuong());
        roomType.setSucChuaToiDa(roomTypeCreateDTO.getSucChuaToiDa() != null ? roomTypeCreateDTO.getSucChuaToiDa() : (byte) 1);
        roomType.setMoTa(roomTypeCreateDTO.getMoTa());
        roomType.setTienNghi(roomTypeCreateDTO.getTienNghi());
        RoomType savedRoomType = roomTypeRepository.save(roomType);
        return RoomTypeDTO.fromEntity(savedRoomType);
    }
    
    // Statistics
    public long getTotalRooms() {
        return roomRepository.count();
    }
    
    public long getTotalRoomsByHotel(Integer hotelId) {
        return roomRepository.findByHotelId(hotelId).size();
    }
    
    public long getTotalRoomsByTrangThai(String trangThai) {
        Room.TrangThaiPhong status = Room.TrangThaiPhong.valueOf(trangThai);
        return roomRepository.findByTrangThai(status).size();
    }
    
    public long getTotalRoomsByHotelAndTrangThai(Integer hotelId, String trangThai) {
        Room.TrangThaiPhong status = Room.TrangThaiPhong.valueOf(trangThai);
        return roomRepository.findByHotelIdAndTrangThai(hotelId, status).size();
    }
} 