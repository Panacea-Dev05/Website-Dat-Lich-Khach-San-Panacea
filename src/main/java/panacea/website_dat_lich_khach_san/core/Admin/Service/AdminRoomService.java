package panacea.website_dat_lich_khach_san.core.Admin.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.entity.RoomImages;
import panacea.website_dat_lich_khach_san.entity.RoomPricing;
import panacea.website_dat_lich_khach_san.entity.RoomType;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomTypeDTO;
import panacea.website_dat_lich_khach_san.repository.RoomImagesRepositoty;
import panacea.website_dat_lich_khach_san.repository.RoomPricingRepositoty;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;
import panacea.website_dat_lich_khach_san.repository.RoomTypeRepository;

@Service
public class AdminRoomService {
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    
    @Autowired
    private RoomImagesRepositoty roomImagesRepositoty;
    
    @Autowired
    private RoomPricingRepositoty roomPricingRepositoty;
    
    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    public List<RoomDTO> getAllRoomsDTO() {
        return roomRepository.findAll().stream()
                .map(RoomDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    public RoomDTO getRoomById(Integer id) {
        Optional<Room> room = roomRepository.findById(id);
        return room.map(this::convertToDTO).orElse(null);
    }
    
    public RoomDTO createRoom(RoomDTO roomDTO) {
        Room room = convertToEntity(roomDTO);
        Room savedRoom = roomRepository.save(room);
        return convertToDTO(savedRoom);
    }
    
    public RoomDTO updateRoom(Integer id, RoomDTO roomDTO) {
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
    
    public boolean deleteRoom(Integer id) {
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    public List<RoomTypeDTO> getAllRoomTypes() {
        return roomTypeRepository.findAll().stream()
            .map(this::convertRoomTypeToDTO)
            .collect(Collectors.toList());
    }
    
    private RoomTypeDTO convertRoomTypeToDTO(RoomType roomType) {
        RoomTypeDTO dto = new RoomTypeDTO();
        dto.setId(roomType.getId());
        dto.setMaLoaiPhong(roomType.getMaLoaiPhong());
        dto.setTenLoaiPhong(roomType.getTenLoaiPhong());
        dto.setDienTich(roomType.getDienTich());
        dto.setSoGiuong(roomType.getSoGiuong());
        dto.setLoaiGiuong(roomType.getLoaiGiuong());
        dto.setSucChuaToiDa(roomType.getSucChuaToiDa());
        dto.setMoTa(roomType.getMoTa());
        dto.setTienNghi(roomType.getTienNghi());
        dto.setUuidId(roomType.getUuidId());
        dto.setCreatedDate(roomType.getCreatedDate());
        dto.setLastModifiedDate(roomType.getLastModifiedDate());
        return dto;
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
        dto.setUuidId(room.getUuidId());
        dto.setCreatedDate(room.getCreatedDate());
        dto.setLastModifiedDate(room.getLastModifiedDate());
        
        if (room.getHotel() != null) {
            dto.setHotelId(room.getHotel().getId());
            dto.setHotelName(room.getHotel().getTenKhachSan());
        }
        
        if (room.getRoomType() != null) {
            dto.setRoomTypeId(room.getRoomType().getId());
            dto.setRoomTypeName(room.getRoomType().getTenLoaiPhong());
            dto.setMaLoaiPhong(room.getRoomType().getMaLoaiPhong());
            
            // Thêm thông tin từ RoomType
            dto.setDienTich(room.getRoomType().getDienTich());
            dto.setSoGiuong(room.getRoomType().getSoGiuong());
            dto.setLoaiGiuong(room.getRoomType().getLoaiGiuong());
            dto.setSucChuaToiDa(room.getRoomType().getSucChuaToiDa());
            dto.setMoTa(room.getRoomType().getMoTa());
            dto.setTienNghi(room.getRoomType().getTienNghi());
        }
        
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
    
    public RoomTypeDTO createRoomType(RoomTypeDTO dto) {
        RoomType roomType = new RoomType();
        roomType.setMaLoaiPhong(dto.getMaLoaiPhong());
        roomType.setTenLoaiPhong(dto.getTenLoaiPhong());
        roomType.setDienTich(dto.getDienTich());
        roomType.setSoGiuong(dto.getSoGiuong());
        roomType.setLoaiGiuong(dto.getLoaiGiuong());
        roomType.setSucChuaToiDa(dto.getSucChuaToiDa());
        roomType.setMoTa(dto.getMoTa());
        roomType.setTienNghi(dto.getTienNghi());
        RoomType saved = roomTypeRepository.save(roomType);
        return convertRoomTypeToDTO(saved);
    }
    
    public RoomTypeDTO updateRoomType(Long id, RoomTypeDTO dto) {
        Optional<RoomType> opt = roomTypeRepository.findById(Math.toIntExact(id));
        if (opt.isEmpty()) return null;
        RoomType roomType = opt.get();
        roomType.setTenLoaiPhong(dto.getTenLoaiPhong());
        roomType.setDienTich(dto.getDienTich());
        roomType.setSoGiuong(dto.getSoGiuong());
        roomType.setLoaiGiuong(dto.getLoaiGiuong());
        roomType.setSucChuaToiDa(dto.getSucChuaToiDa());
        roomType.setMoTa(dto.getMoTa());
        roomType.setTienNghi(dto.getTienNghi());
        RoomType saved = roomTypeRepository.save(roomType);
        return convertRoomTypeToDTO(saved);
    }
    
    public List<RoomDTO> findAvailableRooms(LocalDate ngay, int soNguoi) {
        return roomRepository.findAll().stream()
            .filter(room -> room.getTrangThai() == Room.TrangThaiPhong.SAN_SANG)
            .filter(room -> room.getRoomType() != null && room.getRoomType().getSucChuaToiDa() >= soNguoi)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    public RoomDTO changeRoomStatus(Long roomId, Room.TrangThaiPhong newStatus) {
        Optional<Room> opt = roomRepository.findById(Math.toIntExact(roomId));
        if (opt.isEmpty()) return null;
        Room room = opt.get();
        room.setTrangThai(newStatus);
        Room saved = roomRepository.save(room);
        return convertToDTO(saved);
    }
    
    public RoomImages addRoomImage(RoomImages img) {
        return roomImagesRepositoty.save(img);
    }
    
    public boolean deleteRoomImage(Integer imgId) {
        if (roomImagesRepositoty.existsById(imgId)) {
            roomImagesRepositoty.deleteById(imgId);
            return true;
        }
        return false;
    }
    
    public List<RoomImages> listRoomImagesByRoom(Integer roomId) {
        return roomImagesRepositoty.findAll().stream()
            .filter(img -> img.getPhong() != null && img.getPhong().getId().equals(roomId))
            .collect(Collectors.toList());
    }
    
    public RoomPricing setRoomPricing(RoomPricing pricing) {
        return roomPricingRepositoty.save(pricing);
    }
    
    public RoomPricing updateRoomPricing(Integer id, RoomPricing updated) {
        Optional<RoomPricing> opt = roomPricingRepositoty.findById(id);
        if (opt.isEmpty()) return null;
        RoomPricing pricing = opt.get();
        pricing.setLoaiGia(updated.getLoaiGia());
        pricing.setGiaTri(updated.getGiaTri());
        pricing.setNgayBatDau(updated.getNgayBatDau());
        pricing.setNgayKetThuc(updated.getNgayKetThuc());
        pricing.setApDungCho(updated.getApDungCho());
        pricing.setHeSoDieuChinh(updated.getHeSoDieuChinh());
        pricing.setTrangThai(updated.getTrangThai());
        return roomPricingRepositoty.save(pricing);
    }
    
    public List<RoomPricing> listRoomPricingByRoomType(Integer roomTypeId) {
        return roomPricingRepositoty.findAll().stream()
            .filter(p -> p.getRoomType() != null && p.getRoomType().getId().equals(roomTypeId))
            .collect(Collectors.toList());
    }
} 