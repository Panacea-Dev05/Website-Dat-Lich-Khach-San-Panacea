package panacea.website_dat_lich_khach_san;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.entity.Room;
import panacea.website_dat_lich_khach_san.entity.RoomType;
import panacea.website_dat_lich_khach_san.repository.HotelRepository;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;
import panacea.website_dat_lich_khach_san.repository.RoomTypeRepository;

import java.math.BigDecimal;

@SpringBootApplication
@EnableAsync
public class WebsiteDatLichKhachSanApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsiteDatLichKhachSanApplication.class, args);
    }

    @Bean
    CommandLineRunner init(HotelRepository hotelRepository, RoomTypeRepository roomTypeRepository, RoomRepository roomRepository) {
        return args -> {
            // Kiểm tra xem đã có dữ liệu Hotel chưa
            if (hotelRepository.count() == 0) {
                // Tạo dữ liệu Hotel mẫu
                Hotel hotel = new Hotel();
                hotel.setMaKhachSan("HOTEL001");
                hotel.setTenKhachSan("Panacea Grand Hotel");
                hotel.setDiaChi("123 Đường Nguyễn Huệ");
                hotel.setThanhPho("Hồ Chí Minh");
                hotel.setQuocGia("Việt Nam");
                hotel.setSoDienThoai("028-1234-5678");
                hotel.setEmail("info@panaceahotel.com");
                hotel.setWebsite("https://panaceahotel.com");
                hotel.setSoSao((byte) 5);
                hotel.setMoTa("Khách sạn 5 sao sang trọng tại trung tâm thành phố");
                hotel.setChinhSachHuy("Hủy miễn phí trước 24 giờ");
                hotel.setTrangThai(Hotel.TrangThaiHotel.HOAT_DONG);
                
                hotelRepository.save(hotel);
                System.out.println("✓ Đã tạo dữ liệu Hotel mẫu: " + hotel.getTenKhachSan());
            } else {
                System.out.println("✓ Dữ liệu Hotel đã tồn tại trong database");
            }
        };
    }
}
