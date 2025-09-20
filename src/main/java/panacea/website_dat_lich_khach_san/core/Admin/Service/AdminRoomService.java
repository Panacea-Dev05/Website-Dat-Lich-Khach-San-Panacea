package panacea.website_dat_lich_khach_san.core.Admin.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.entity.RoomImages;
import panacea.website_dat_lich_khach_san.entity.RoomPricing;
import panacea.website_dat_lich_khach_san.entity.RoomType;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomDTO;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomTypeDTO;
import panacea.website_dat_lich_khach_san.repository.HotelRepository;
import panacea.website_dat_lich_khach_san.repository.RoomImagesRepositoty;
import panacea.website_dat_lich_khach_san.repository.RoomPricingRepositoty;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;
import panacea.website_dat_lich_khach_san.repository.RoomTypeRepository;

// Service quản lý phòng và loại phòng cho Admin
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
    
    @Autowired
    private HotelRepository hotelRepository;

    // Lấy tất cả phòng
    public List<RoomDTO> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lấy phòng theo ID
    public RoomDTO getRoomById(Integer id) {
        Optional<Room> room = roomRepository.findById(id);
        return room.map(this::convertToDTO).orElse(null);
    }

    // Tạo phòng mới
    public RoomDTO createRoom(RoomDTO roomDTO) {
        Room room = convertToEntity(roomDTO);
        
        // Thiết lập RoomType nếu có roomTypeId
        if (roomDTO.getRoomTypeId() != null) {
            RoomType roomType = roomTypeRepository.findById(roomDTO.getRoomTypeId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hạng phòng với ID: " + roomDTO.getRoomTypeId()));
            room.setRoomType(roomType);
        }
        
        Room savedRoom = roomRepository.save(room);
        return convertToDTO(savedRoom);
    }

    // Cập nhật phòng
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
            // Thêm cập nhật hạng phòng
            if (roomDTO.getRoomTypeId() != null) {
                RoomType roomType = roomTypeRepository.findById(roomDTO.getRoomTypeId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy hạng phòng"));
                room.setRoomType(roomType);
            }
            Room savedRoom = roomRepository.save(room);
            return convertToDTO(savedRoom);
        }
        return null;
    }

    // Xóa phòng
    public boolean deleteRoom(Integer id) {
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Lấy tất cả loại phòng
    public List<RoomTypeDTO> getAllRoomTypes() {
        return roomTypeRepository.findAll().stream()
                .map(this::convertRoomTypeToDTO)
                .collect(Collectors.toList());
    }

    // Lấy loại phòng theo ID
    public RoomTypeDTO getRoomTypeById(Integer id) {
        Optional<RoomType> roomType = roomTypeRepository.findById(id);
        return roomType.map(this::convertRoomTypeToDTO).orElse(null);
    }

    // Chuyển đổi RoomType sang DTO
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

        // Lấy danh sách hình ảnh từ bảng RoomImages
        List<String> imageUrls = roomImagesRepositoty.findByLoaiPhong_Id(roomType.getId())
                .stream()
                .filter(img -> img.getTrangThai() != null && img.getTrangThai().equals("Hoạt động"))
                .sorted((img1, img2) -> {
                    int orderCompare = Integer.compare(
                        img1.getThuTuHienThi() != null ? img1.getThuTuHienThi().intValue() : 0,
                        img2.getThuTuHienThi() != null ? img2.getThuTuHienThi().intValue() : 0
                    );
                    if (orderCompare != 0) return orderCompare;
                    return Integer.compare(img1.getId(), img2.getId());
                })
                .map(RoomImages::getUrlHinhAnh)
                .collect(Collectors.toList());
        dto.setImageUrls(imageUrls);

        return dto;
    }

    // Chuyển đổi Room sang DTO
    private RoomDTO convertToDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setId(room.getId());
        dto.setSoPhong(room.getSoPhong());
        dto.setTang(room.getTang());
        dto.setViewPhong(room.getViewPhong());
        dto.setTrangThai(room.getTrangThai() != null ? room.getTrangThai().name() : null);
        dto.setGiaCoBan(room.getGiaCoBan());
        dto.setGhiChu(room.getGhiChu());
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

    // Chuyển đổi DTO sang Room
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
        
        // Set hotel (assuming single hotel model)
        Hotel hotel = hotelRepository.findAll().stream().findFirst().orElse(null);
        room.setHotel(hotel);
        
        return room;
    }

    // Tạo loại phòng mới
    public RoomTypeDTO createRoomType(RoomTypeDTO dto) {
        // Lấy hotel đầu tiên (single hotel model)
        Hotel hotel = hotelRepository.findAll().stream().findFirst().orElse(null);
        
        RoomType roomType = new RoomType();
        roomType.setMaLoaiPhong(dto.getMaLoaiPhong());
        roomType.setTenLoaiPhong(dto.getTenLoaiPhong());
        roomType.setDienTich(dto.getDienTich());
        roomType.setSoGiuong(dto.getSoGiuong());
        roomType.setLoaiGiuong(dto.getLoaiGiuong());
        roomType.setSucChuaToiDa(dto.getSucChuaToiDa());
        roomType.setMoTa(dto.getMoTa());
        roomType.setTienNghi(dto.getTienNghi());
        roomType.setHotel(hotel); // Set hotel relationship
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

    // Cập nhật loại phòng
    public RoomTypeDTO updateRoomType(Integer id, RoomTypeDTO dto) {
        Optional<RoomType> opt = roomTypeRepository.findById(id);
        if (opt.isEmpty()) return null;
        RoomType roomType = opt.get();
        // Cho phép cập nhật mã hạng phòng
        roomType.setMaLoaiPhong(dto.getMaLoaiPhong());
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

    // Xóa loại phòng
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

    // Tìm phòng trống
    public List<RoomDTO> findAvailableRooms(LocalDate ngay, int soNguoi) {
        return roomRepository.findAll().stream()
                .filter(room -> room.getTrangThai() == Room.TrangThaiPhong.SAN_SANG)
                .filter(room -> room.getRoomType() != null && room.getRoomType().getSucChuaToiDa() >= soNguoi)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Thay đổi trạng thái phòng
    public RoomDTO changeRoomStatus(Integer roomId, Room.TrangThaiPhong newStatus) {
        Optional<Room> opt = roomRepository.findById(roomId);
        if (opt.isEmpty()) return null;
        Room room = opt.get();
        room.setTrangThai(newStatus);
        Room saved = roomRepository.save(room);
        return convertToDTO(saved);
    }

    // Thêm hình ảnh phòng
    public RoomImages addRoomImage(RoomImages img) {
        return roomImagesRepositoty.save(img);
    }

    // Xóa hình ảnh phòng
    public boolean deleteRoomImage(Integer imgId) {
        if (roomImagesRepositoty.existsById(imgId)) {
            roomImagesRepositoty.deleteById(imgId);
            return true;
        }
        return false;
    }




    // Thiết lập giá phòng
    public RoomPricing setRoomPricing(RoomPricing pricing) {
        return roomPricingRepositoty.save(pricing);
    }

    // Cập nhật giá phòng
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

    // Lấy danh sách giá theo loại phòng
    public List<RoomPricing> listRoomPricingByRoomType(Integer roomTypeId) {
        return roomPricingRepositoty.findAll().stream()
                .filter(p -> p.getRoomType() != null && p.getRoomType().getId().equals(roomTypeId))
                .collect(Collectors.toList());
    }

    // Lọc phòng theo điều kiện
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
                    // Đã bỏ filter theo hotel vì không còn quan hệ hotel
                    return match;
                })
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Lọc phòng theo điều kiện có phân trang
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

    // Lọc loại phòng theo từ khóa
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

    // Lấy danh sách ảnh theo hạng phòng
    public List<RoomImages> getRoomTypeImages(Integer roomTypeId) {
        try {
            System.out.println("=== GET ROOM TYPE IMAGES START ===");
            System.out.println("Room Type ID: " + roomTypeId);
            
            List<RoomImages> images = roomImagesRepositoty.findByLoaiPhong_Id(roomTypeId);
            System.out.println("Found " + images.size() + " images for room type " + roomTypeId);
            
            // Sắp xếp theo thứ tự hiển thị và ID
            images.sort((a, b) -> {
                int orderCompare = Byte.compare(
                    a.getThuTuHienThi() != null ? a.getThuTuHienThi() : 1,
                    b.getThuTuHienThi() != null ? b.getThuTuHienThi() : 1
                );
                if (orderCompare != 0) return orderCompare;
                return Integer.compare(a.getId(), b.getId());
            });
            
            System.out.println("=== GET ROOM TYPE IMAGES SUCCESS ===");
            return images;
        } catch (Exception e) {
            System.err.println("=== GET ROOM TYPE IMAGES ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Thêm hình ảnh cho hạng phòng
    public RoomImages addRoomTypeImage(Integer roomTypeId, String imageUrl, String imageName, String description) {
        try {
            System.out.println("=== ADD ROOM TYPE IMAGE START ===");
            System.out.println("Room Type ID: " + roomTypeId);
            System.out.println("Image URL: " + imageUrl);
            System.out.println("Image Name: " + imageName);
            
            // Tìm hạng phòng
            RoomType roomType = roomTypeRepository.findById(roomTypeId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hạng phòng với ID: " + roomTypeId));
            
            System.out.println("Found room type: " + roomType.getTenLoaiPhong());
            
            // Tạo entity hình ảnh
            RoomImages image = new RoomImages();
            image.setLoaiPhong(roomType);
            image.setUrlHinhAnh(imageUrl);
            image.setTenHinhAnh(imageName);
            image.setMoTa(description);
            image.setThuTuHienThi((byte) 1);
            image.setLaHinhChinh(false);
            image.setTrangThai("Hoạt động");
            image.setUuidId(UUID.randomUUID());
            image.setCreatedDate(System.currentTimeMillis());
            
            System.out.println("Saving image to database...");
            RoomImages savedImage = roomImagesRepositoty.save(image);
            System.out.println("Successfully saved image with ID: " + savedImage.getId());
            System.out.println("=== ADD ROOM TYPE IMAGE SUCCESS ===");
            
            return savedImage;
        } catch (Exception e) {
            System.err.println("=== ADD ROOM TYPE IMAGE ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    // Xóa hình ảnh hạng phòng
    public boolean deleteRoomTypeImage(Integer imageId) {
        try {
            System.out.println("=== DELETE ROOM TYPE IMAGE START ===");
            System.out.println("Image ID: " + imageId);
            
            Optional<RoomImages> imageOpt = roomImagesRepositoty.findById(imageId);
            if (imageOpt.isPresent()) {
                RoomImages image = imageOpt.get();
                // Chỉ xóa nếu là hình ảnh của hạng phòng (không phải phòng cụ thể)
                if (image.getLoaiPhong() != null && image.getPhong() == null) {
                    roomImagesRepositoty.deleteById(imageId);
                    System.out.println("Successfully deleted image with ID: " + imageId);
                    System.out.println("=== DELETE ROOM TYPE IMAGE SUCCESS ===");
                    return true;
                } else {
                    System.out.println("Image is not a room type image, cannot delete");
                    return false;
                }
            } else {
                System.out.println("Image not found with ID: " + imageId);
                return false;
            }
        } catch (Exception e) {
            System.err.println("=== DELETE ROOM TYPE IMAGE ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}