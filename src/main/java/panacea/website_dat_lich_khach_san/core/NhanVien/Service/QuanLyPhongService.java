package panacea.website_dat_lich_khach_san.core.NhanVien.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.entity.RoomType;
import panacea.website_dat_lich_khach_san.entity.RoomPricing;
import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomCreateDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomTypeCreateDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomTypeDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomUpdateDTO;
import panacea.website_dat_lich_khach_san.infrastructure.Exception.BadRequestException;
import panacea.website_dat_lich_khach_san.infrastructure.Exception.ResourceNotFoundException;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;
import panacea.website_dat_lich_khach_san.repository.RoomTypeRepository;
import panacea.website_dat_lich_khach_san.repository.RoomPricingRepositoty;
import panacea.website_dat_lich_khach_san.repository.HotelRepository;
import panacea.website_dat_lich_khach_san.enums.FloorType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class QuanLyPhongService {
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    
    @Autowired
    private RoomPricingRepositoty roomPricingRepository;
    
    @Autowired
    private HotelRepository hotelRepository;
    
    // Lấy tên nhân viên
    public String getStaffName() {
        return "Nhân viên quản lý phòng";
    }
    
    // Thay đổi trạng thái phòng - CHỈ CHỨC NĂNG NÀY ĐƯỢC PHÉP CHO NHÂN VIÊN
    public boolean changeRoomStatus(Integer roomId, String newStatus, String ghiChu) {
        try {
            Optional<Room> roomOpt = roomRepository.findById(roomId);
            if (roomOpt.isEmpty()) {
                return false;
            }
            
            Room room = roomOpt.get();
            Room.TrangThaiPhong oldStatus = room.getTrangThai();
            
            // Chuyển đổi String thành enum
            Room.TrangThaiPhong newStatusEnum;
            try {
                newStatusEnum = Room.TrangThaiPhong.valueOf(newStatus);
            } catch (IllegalArgumentException e) {
                System.err.println("Trạng thái không hợp lệ: " + newStatus);
                return false;
            }
            
            room.setTrangThai(newStatusEnum);
            
            // Có thể thêm ghi chú vào một field khác nếu cần
            // room.setGhiChu(ghiChu);
            
            roomRepository.save(room);
            
            // Log thay đổi trạng thái
            System.out.println("Phòng " + room.getSoPhong() + " đã thay đổi trạng thái từ " + oldStatus + " sang " + newStatusEnum);
            if (ghiChu != null && !ghiChu.trim().isEmpty()) {
                System.out.println("Ghi chú: " + ghiChu);
            }
            
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi khi thay đổi trạng thái phòng: " + e.getMessage());
            return false;
        }
    }

    // Lấy danh sách tất cả phòng
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
    
    // Lấy danh sách tất cả phòng dưới dạng DTO
    public List<RoomDTO> getAllRoomsDTO() {
        return roomRepository.findAll().stream()
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // Lấy danh sách phòng có phân trang
    public Page<RoomDTO> getAllRoomsPaged(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        return roomRepository.findAll(pageable)
                .map(RoomDTO::fromEntity);
    }
    
    // Lấy thông tin phòng theo ID
    public RoomDTO getRoomById(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với ID: " + id));
        return RoomDTO.fromEntity(room);
    }
    
    // Tạo phòng mới
    public RoomDTO createRoom(RoomCreateDTO roomCreateDTO) {
        // Validate room type exists
        RoomType roomType = roomTypeRepository.findById(roomCreateDTO.getRoomTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại phòng với ID: " + roomCreateDTO.getRoomTypeId()));

        // Get hotel (assuming single hotel model)
        Hotel hotel = hotelRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách sạn"));
        // Validate floor number
        validateFloor(roomCreateDTO.getTang());
        
        // Check if room number already exists in the hotel
        Optional<Room> existingRoom = roomRepository.findAll().stream().filter(r -> r.getSoPhong().equals(roomCreateDTO.getSoPhong())).findFirst();
        if (existingRoom.isPresent()) {
            throw new BadRequestException("Số phòng " + roomCreateDTO.getSoPhong() + " đã tồn tại trong khách sạn này");
        }
        
        Room room = new Room();
        room.setSoPhong(roomCreateDTO.getSoPhong());
        room.setTang(roomCreateDTO.getTang());
        room.setViewPhong(roomCreateDTO.getViewPhong());
        room.setGiaCoBan(roomCreateDTO.getGiaCoBan());
        room.setGhiChu(roomCreateDTO.getGhiChu());
        room.setRoomType(roomType);
        room.setHotel(hotel);
        
        // Set trang thai
        if (roomCreateDTO.getTrangThai() != null) {
            room.setTrangThai(Room.TrangThaiPhong.valueOf(roomCreateDTO.getTrangThai()));
        }
        
        Room savedRoom = roomRepository.save(room);
        return RoomDTO.fromEntity(savedRoom);
    }
    
    // Cập nhật thông tin phòng
    public RoomDTO updateRoom(Integer id, RoomUpdateDTO roomUpdateDTO) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với ID: " + id));
        
        // Check if new room number conflicts with existing rooms in the same hotel
        if (roomUpdateDTO.getSoPhong() != null && !roomUpdateDTO.getSoPhong().equals(room.getSoPhong())) {
            Optional<Room> existingRoom = roomRepository.findAll().stream().filter(r -> r.getSoPhong().equals(roomUpdateDTO.getSoPhong())).findFirst();
            if (existingRoom.isPresent()) {
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
            validateFloor(roomUpdateDTO.getTang());
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
    
    // Xóa phòng
    public void deleteRoom(Integer id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phòng với ID: " + id));
        
        // Check if room is currently in use
        if (room.getTrangThai() == Room.TrangThaiPhong.DANG_SU_DUNG) {
            throw new BadRequestException("Không thể xóa phòng đang được sử dụng");
        }
        
        roomRepository.delete(room);
    }
    
    // Tìm kiếm phòng theo số phòng
    public List<RoomDTO> searchRoomsBySoPhong(String soPhong) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getSoPhong() != null && room.getSoPhong().contains(soPhong))
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // Lấy phòng theo trạng thái
    public List<RoomDTO> getRoomsByTrangThai(String trangThai) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getTrangThai() != null && room.getTrangThai().name().equals(trangThai))
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // Lấy phòng theo trạng thái có phân trang
    public List<RoomDTO> getRoomsByTrangThaiPaged(String trangThai, int page, int size) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getTrangThai() != null && room.getTrangThai().name().equals(trangThai))
                .skip((long) page * size)
                .limit(size)
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // Lấy phòng theo tầng
    public List<RoomDTO> getRoomsByTang(Byte tang) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getTang() != null && room.getTang().equals(tang))
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // Lấy phòng theo khoảng giá
    public List<RoomDTO> getRoomsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getGiaCoBan() != null &&
                        room.getGiaCoBan().compareTo(minPrice) >= 0 &&
                        room.getGiaCoBan().compareTo(maxPrice) <= 0)
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // Lấy phòng có phân trang
    public List<RoomDTO> getRoomsPaged(int page, int size) {
        return roomRepository.findAll().stream()
            .skip((long) page * size)
            .limit(size)
            .map(RoomDTO::fromEntity)
            .collect(Collectors.toList());
    }
    
    // Lấy danh sách tất cả loại phòng
    public List<RoomTypeDTO> getAllRoomTypes() {
        return roomTypeRepository.findAll().stream()
                .map(RoomTypeDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // Lấy thông tin loại phòng theo ID
    public RoomTypeDTO getRoomTypeById(Integer id) {
        RoomType roomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại phòng với ID: " + id));
        // Lấy pricing information
        List<RoomPricing> pricings = roomPricingRepository.findAll().stream()
                .filter(p -> p.getRoomType() != null && p.getRoomType().getId().equals(id))
                .collect(Collectors.toList());
        return RoomTypeDTO.fromEntityWithPricing(roomType, pricings);
    }
    
    // Tìm kiếm loại phòng theo tên
    public List<RoomTypeDTO> searchRoomTypesByTenLoaiPhong(String tenLoaiPhong) {
        return roomTypeRepository.findByTenLoaiPhongContaining(tenLoaiPhong).stream()
                .map(roomType -> {
                    List<RoomPricing> pricings = roomPricingRepository.findAll().stream()
                            .filter(p -> p.getRoomType() != null && p.getRoomType().getId().equals(roomType.getId()))
                            .collect(Collectors.toList());
                    return RoomTypeDTO.fromEntityWithPricing(roomType, pricings);
                })
                .collect(Collectors.toList());
    }
    
    // Lấy loại phòng theo số giường
    public List<RoomTypeDTO> getRoomTypesBySoGiuong(Byte soGiuong) {
        return roomTypeRepository.findBySoGiuong(soGiuong).stream()
                .map(roomType -> {
                    List<RoomPricing> pricings = roomPricingRepository.findAll().stream()
                            .filter(p -> p.getRoomType() != null && p.getRoomType().getId().equals(roomType.getId()))
                            .collect(Collectors.toList());
                    return RoomTypeDTO.fromEntityWithPricing(roomType, pricings);
                })
                .collect(Collectors.toList());
    }
    
    // Lấy loại phòng theo sức chứa
    public List<RoomTypeDTO> getRoomTypesBySucChua(Byte sucChua) {
        return roomTypeRepository.findBySucChuaToiDaGreaterThanEqual(sucChua).stream()
                .map(roomType -> {
                    List<RoomPricing> pricings = roomPricingRepository.findAll().stream()
                            .filter(p -> p.getRoomType() != null && p.getRoomType().getId().equals(roomType.getId()))
                            .collect(Collectors.toList());
                    return RoomTypeDTO.fromEntityWithPricing(roomType, pricings);
                })
                .collect(Collectors.toList());
    }
    
    // Lấy loại phòng theo diện tích
    public List<RoomTypeDTO> getRoomTypesByDienTich(BigDecimal minDienTich, BigDecimal maxDienTich) {
        return roomTypeRepository.findByDienTichBetween(minDienTich, maxDienTich).stream()
                .map(RoomTypeDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    // Tạo loại phòng mới
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
    
    // Lấy tổng số phòng
    public long getTotalRooms() {
        return roomRepository.count();
    }
    
    // Lấy tổng số phòng theo trạng thái
    public long getTotalRoomsByTrangThai(String trangThai) {
        Room.TrangThaiPhong status = Room.TrangThaiPhong.valueOf(trangThai);
        return roomRepository.findByTrangThai(status).size();
    }
    
    // Lấy tổng số phòng theo trạng thái có phân trang
    public long getTotalRoomsByTrangThaiPaged(String trangThai, int page, int size) {
        Room.TrangThaiPhong status = Room.TrangThaiPhong.valueOf(trangThai);
        return roomRepository.findByTrangThai(status).size();
    }
    
    /**
     * Validate floor number using FloorType enum
     * @param tang Floor number to validate
     * @throws BadRequestException if floor number is invalid
     */
    private void validateFloor(Byte tang) {
        if (tang == null) {
            throw new BadRequestException("Tầng không được để trống");
        }
        
        if (!FloorType.isValidFloor(tang)) {
            throw new BadRequestException("Tầng không hợp lệ. Chỉ cho phép: tầng hầm (-1), tầng trệt (0), tầng thường (1-50), tầng áp mái (99), tầng mái (100)");
        }
        
        // Get floor type for additional validation
        FloorType floorType = FloorType.getFloorType(tang);
        
        // Log floor type for debugging (optional)
        // System.out.println("Floor " + tang + " is of type: " + floorType.getDisplayName());
    }
}