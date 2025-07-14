package panacea.website_dat_lich_khach_san.core.Admin.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

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

    public RoomTypeDTO getRoomTypeById(Integer id) {
        Optional<RoomType> roomType = roomTypeRepository.findById(id);
        return roomType.map(this::convertRoomTypeToDTO).orElse(null);
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
        dto.setSoLuongPhong((int) roomRepository.countByRoomTypeId(roomType.getId()));

        // Lấy thông tin giá từ RoomPricing
        List<RoomPricing> pricings = roomPricingRepositoty.findAll().stream()
                .filter(p -> p.getRoomType() != null && p.getRoomType().getId().equals(roomType.getId()))
                .collect(Collectors.toList());

        if (!pricings.isEmpty()) {
            // Lấy giá đầu tiên (có thể cải thiện logic này để lấy giá hiện tại)
            RoomPricing pricing = pricings.get(0);
            dto.setGiaGio(pricing.getGiaGio());
            dto.setGiaNgay(pricing.getGiaNgay());
            dto.setGiaQuaDem(pricing.getGiaQuaDem());
        }

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
        dto.setHotelId(room.getHotel() != null ? room.getHotel().getId() : null);
        dto.setRoomTypeId(room.getRoomType() != null ? room.getRoomType().getId() : null);
        dto.setUuidId(room.getUuidId());
        dto.setCreatedDate(room.getCreatedDate());
        dto.setLastModifiedDate(room.getLastModifiedDate());

        // Bổ sung setRoomTypeName
        if (room.getRoomType() != null) {
            dto.setRoomTypeName(room.getRoomType().getTenLoaiPhong());
            dto.setMaLoaiPhong(room.getRoomType().getMaLoaiPhong());

            // Lấy thông tin giá từ RoomPricing
            List<RoomPricing> pricings = roomPricingRepositoty.findAll().stream()
                    .filter(p -> p.getRoomType() != null && p.getRoomType().getId().equals(room.getRoomType().getId()))
                    .collect(Collectors.toList());

            if (!pricings.isEmpty()) {
                // Lấy giá đầu tiên (có thể cải thiện logic này để lấy giá hiện tại)
                RoomPricing pricing = pricings.get(0);
                dto.setGiaGio(pricing.getGiaGio());
                dto.setGiaNgay(pricing.getGiaNgay());
                dto.setGiaQuaDem(pricing.getGiaQuaDem());
            }
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

        // Tạo RoomPricing cho hạng phòng mới
        if (dto.getGiaGio() != null || dto.getGiaNgay() != null || dto.getGiaQuaDem() != null) {
            RoomPricing pricing = new RoomPricing();
            pricing.setRoomType(saved);
            pricing.setLoaiGia(RoomPricing.LoaiGia.BASE);
            pricing.setGiaGio(dto.getGiaGio() != null ? dto.getGiaGio() : BigDecimal.ZERO);
            pricing.setGiaNgay(dto.getGiaNgay() != null ? dto.getGiaNgay() : BigDecimal.ZERO);
            pricing.setGiaQuaDem(dto.getGiaQuaDem() != null ? dto.getGiaQuaDem() : BigDecimal.ZERO);
            pricing.setNgayBatDau(LocalDate.now());
            pricing.setNgayKetThuc(LocalDate.now().plusYears(10)); // Giá có hiệu lực trong 10 năm
            pricing.setApDungCho("All");
            pricing.setHeSoDieuChinh(BigDecimal.ONE);
            pricing.setTrangThai("Hoạt động");
            roomPricingRepositoty.save(pricing);
        }

        return convertRoomTypeToDTO(saved);
    }

    public RoomTypeDTO updateRoomType(Integer id, RoomTypeDTO dto) {
        Optional<RoomType> opt = roomTypeRepository.findById(id);
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

        // Cập nhật hoặc tạo mới RoomPricing
        List<RoomPricing> existingPricings = roomPricingRepositoty.findAll().stream()
                .filter(p -> p.getRoomType() != null && p.getRoomType().getId().equals(id))
                .collect(Collectors.toList());

        if (!existingPricings.isEmpty()) {
            // Cập nhật giá hiện tại
            RoomPricing pricing = existingPricings.get(0);
            pricing.setGiaGio(dto.getGiaGio() != null ? dto.getGiaGio() : BigDecimal.ZERO);
            pricing.setGiaNgay(dto.getGiaNgay() != null ? dto.getGiaNgay() : BigDecimal.ZERO);
            pricing.setGiaQuaDem(dto.getGiaQuaDem() != null ? dto.getGiaQuaDem() : BigDecimal.ZERO);
            roomPricingRepositoty.save(pricing);
        } else if (dto.getGiaGio() != null || dto.getGiaNgay() != null || dto.getGiaQuaDem() != null) {
            // Tạo mới RoomPricing nếu chưa có
            RoomPricing pricing = new RoomPricing();
            pricing.setRoomType(saved);
            pricing.setLoaiGia(RoomPricing.LoaiGia.BASE);
            pricing.setGiaGio(dto.getGiaGio() != null ? dto.getGiaGio() : BigDecimal.ZERO);
            pricing.setGiaNgay(dto.getGiaNgay() != null ? dto.getGiaNgay() : BigDecimal.ZERO);
            pricing.setGiaQuaDem(dto.getGiaQuaDem() != null ? dto.getGiaQuaDem() : BigDecimal.ZERO);
            pricing.setNgayBatDau(LocalDate.now());
            pricing.setNgayKetThuc(LocalDate.now().plusYears(10));
            pricing.setApDungCho("All");
            pricing.setHeSoDieuChinh(BigDecimal.ONE);
            pricing.setTrangThai("Hoạt động");
            roomPricingRepositoty.save(pricing);
        }

        return convertRoomTypeToDTO(saved);
    }

    public boolean deleteRoomType(Integer id) {
        Optional<RoomType> opt = roomTypeRepository.findById(id);
        if (opt.isPresent()) {
            // Xóa tất cả RoomPricing liên quan
            List<RoomPricing> pricings = roomPricingRepositoty.findAll().stream()
                    .filter(p -> p.getRoomType() != null && p.getRoomType().getId().equals(id))
                    .collect(Collectors.toList());
            roomPricingRepositoty.deleteAll(pricings);

            // Xóa RoomType
            roomTypeRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<RoomDTO> findAvailableRooms(LocalDate ngay, int soNguoi) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getTrangThai() == Room.TrangThaiPhong.SAN_SANG)
                .filter(room -> room.getRoomType() != null && room.getRoomType().getSucChuaToiDa() >= soNguoi)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RoomDTO changeRoomStatus(Integer roomId, Room.TrangThaiPhong newStatus) {
        Optional<Room> opt = roomRepository.findById(roomId);
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

    public List<RoomDTO> filterRooms(String keyword, Integer roomTypeId, String status, String area, String branch) {
        return roomRepository.findAll().stream()
                .filter(room -> {
                    boolean match = true;
                    if (keyword != null && !keyword.isEmpty()) {
                        match &= room.getSoPhong() != null && room.getSoPhong().toLowerCase().contains(keyword.toLowerCase());
                    }
                    if (roomTypeId != null) {
                        match &= room.getRoomType() != null && room.getRoomType().getId().equals(roomTypeId);
                    }
                    if (status != null && !status.isEmpty()) {
                        match &= room.getTrangThai() != null && room.getTrangThai().name().equalsIgnoreCase(status);
                    }
                    if (area != null && !area.isEmpty()) {
                        match &= room.getTang() != null && room.getTang().toString().equals(area);
                    }
                    if (branch != null && !branch.isEmpty()) {
                        match &= room.getHotel() != null && room.getHotel().getTenKhachSan().toLowerCase().contains(branch.toLowerCase());
                    }
                    return match;
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<RoomDTO> filterRoomsPaged(String keyword, Integer roomTypeId, String status, String area, String branch, Pageable pageable) {
        List<RoomDTO> allRooms = filterRooms(keyword, roomTypeId, status, area, branch);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allRooms.size());

        if (start > allRooms.size()) {
            return new PageImpl<>(List.of(), pageable, allRooms.size());
        }

        List<RoomDTO> pageContent = allRooms.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allRooms.size());
    }

    public List<RoomTypeDTO> filterRoomTypes(String keyword, String branch, String status) {
        return roomTypeRepository.findAll().stream()
                .filter(type -> {
                    if (keyword != null && !keyword.isEmpty()) {
                        return type.getTenLoaiPhong() != null && type.getTenLoaiPhong().toLowerCase().contains(keyword.toLowerCase());
                    }
                    return true;
                })
                .map(this::convertRoomTypeToDTO)
                .collect(Collectors.toList());
    }
} 