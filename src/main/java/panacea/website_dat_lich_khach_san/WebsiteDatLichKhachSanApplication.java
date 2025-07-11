package panacea.website_dat_lich_khach_san;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.entity.RoomType;
import panacea.website_dat_lich_khach_san.repository.HotelRepository;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;
import panacea.website_dat_lich_khach_san.repository.RoomTypeRepository;

import java.math.BigDecimal;

@SpringBootApplication
public class WebsiteDatLichKhachSanApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsiteDatLichKhachSanApplication.class, args);
    }

    @Bean
    public CommandLineRunner initData(HotelRepository hotelRepository, 
                                    RoomTypeRepository roomTypeRepository, 
                                    RoomRepository roomRepository) {
        return args -> {
            // Tạo dữ liệu mẫu nếu chưa có
            if (hotelRepository.count() == 0) {
                createSampleData(hotelRepository, roomTypeRepository, roomRepository);
            }
        };
    }

    private void createSampleData(HotelRepository hotelRepository, 
                                RoomTypeRepository roomTypeRepository, 
                                RoomRepository roomRepository) {
        
        // Tạo khách sạn mẫu
        Hotel hotel = new Hotel();
        hotel.setMaKhachSan("KS001");
        hotel.setTenKhachSan("Panacea Hotel Central");
        hotel.setDiaChi("123 Đường ABC, Quận 1");
        hotel.setThanhPho("TP.HCM");
        hotel.setQuocGia("Việt Nam");
        hotel.setSoDienThoai("028 1234 5678");
        hotel.setEmail("contact@panacea.com");
        hotel.setSoSao((byte) 5);
        hotel.setMoTa("Khách sạn 5 sao tại trung tâm thành phố");
        hotel = hotelRepository.save(hotel);

        // Tạo loại phòng mẫu
        RoomType standardRoom = new RoomType();
        standardRoom.setMaLoaiPhong("STD001");
        standardRoom.setTenLoaiPhong("Phòng Tiêu Chuẩn");
        standardRoom.setDienTich(new BigDecimal("25.00"));
        standardRoom.setSoGiuong((byte) 1);
        standardRoom.setLoaiGiuong("Giường đơn");
        standardRoom.setSucChuaToiDa((byte) 2);
        standardRoom.setMoTa("Phòng tiêu chuẩn với đầy đủ tiện nghi hiện đại");
        standardRoom.setTienNghi("WiFi, TV, Điều hòa, Tủ lạnh mini");
        standardRoom = roomTypeRepository.save(standardRoom);

        RoomType deluxeRoom = new RoomType();
        deluxeRoom.setMaLoaiPhong("DLX001");
        deluxeRoom.setTenLoaiPhong("Phòng Deluxe");
        deluxeRoom.setDienTich(new BigDecimal("35.00"));
        deluxeRoom.setSoGiuong((byte) 1);
        deluxeRoom.setLoaiGiuong("Giường đôi");
        deluxeRoom.setSucChuaToiDa((byte) 3);
        deluxeRoom.setMoTa("Phòng deluxe rộng rãi với view đẹp");
        deluxeRoom.setTienNghi("WiFi, TV, Điều hòa, Tủ lạnh mini, Bồn tắm");
        deluxeRoom = roomTypeRepository.save(deluxeRoom);

        RoomType suiteRoom = new RoomType();
        suiteRoom.setMaLoaiPhong("SUITE001");
        suiteRoom.setTenLoaiPhong("Phòng Suite");
        suiteRoom.setDienTich(new BigDecimal("50.00"));
        suiteRoom.setSoGiuong((byte) 2);
        suiteRoom.setLoaiGiuong("Giường đôi + Sofa");
        suiteRoom.setSucChuaToiDa((byte) 4);
        suiteRoom.setMoTa("Phòng suite cao cấp với không gian riêng biệt");
        suiteRoom.setTienNghi("WiFi, TV, Điều hòa, Tủ lạnh mini, Bồn tắm, Phòng khách");
        suiteRoom = roomTypeRepository.save(suiteRoom);

        // Tạo phòng mẫu
        createRoom(roomRepository, hotel, standardRoom, "101", (byte) 1, "City", new BigDecimal("1200000"));
        createRoom(roomRepository, hotel, standardRoom, "102", (byte) 1, "City", new BigDecimal("1200000"));
        createRoom(roomRepository, hotel, deluxeRoom, "201", (byte) 2, "Pool", new BigDecimal("1800000"));
        createRoom(roomRepository, hotel, deluxeRoom, "202", (byte) 2, "Pool", new BigDecimal("1800000"));
        createRoom(roomRepository, hotel, suiteRoom, "301", (byte) 3, "Sea", new BigDecimal("2500000"));
        createRoom(roomRepository, hotel, suiteRoom, "302", (byte) 3, "Sea", new BigDecimal("2500000"));
    }

    private void createRoom(RoomRepository roomRepository, Hotel hotel, RoomType roomType, 
                           String soPhong, Byte tang, String viewPhong, BigDecimal giaCoBan) {
        Room room = new Room();
        room.setSoPhong(soPhong);
        room.setTang(tang);
        room.setViewPhong(viewPhong);
        room.setGiaCoBan(giaCoBan);
        room.setHotel(hotel);
        room.setRoomType(roomType);
        room.setTrangThai(Room.TrangThaiPhong.SAN_SANG);
        roomRepository.save(room);
    }
}
