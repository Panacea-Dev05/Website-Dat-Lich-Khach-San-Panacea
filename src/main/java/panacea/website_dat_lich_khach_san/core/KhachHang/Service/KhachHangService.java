package panacea.website_dat_lich_khach_san.core.KhachHang.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import panacea.website_dat_lich_khach_san.entity.Booking;
import panacea.website_dat_lich_khach_san.entity.Customer;
import panacea.website_dat_lich_khach_san.entity.Hotel;
import panacea.website_dat_lich_khach_san.entity.RoomType;
import panacea.website_dat_lich_khach_san.entity.RoomPricing;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.BookingRequestDTO;
import panacea.website_dat_lich_khach_san.repository.BookingRepository;
import panacea.website_dat_lich_khach_san.repository.CustomerRepository;
import panacea.website_dat_lich_khach_san.repository.HotelRepository;
import panacea.website_dat_lich_khach_san.repository.RoomRepository;
import panacea.website_dat_lich_khach_san.repository.RoomPricingRepositoty;
import panacea.website_dat_lich_khach_san.repository.RoomTypeRepository;
import panacea.website_dat_lich_khach_san.repository.RoomImagesRepositoty;
import panacea.website_dat_lich_khach_san.infrastructure.DTO.RoomTypeDTO;
import panacea.website_dat_lich_khach_san.service.VNPayService;
import panacea.website_dat_lich_khach_san.service.SePayService;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Service xử lý các chức năng dành cho khách hàng
 * Chức năng chính:
 * - Đặt phòng đơn lẻ và combo nhiều loại phòng
 * - Quản lý thông tin khách hàng
 * - Gửi email xác nhận và QR code thanh toán
 * - Lấy thông tin phòng và giá cả
 */
@Service
public class KhachHangService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired(required = false)
    private JavaMailSender mailSender;
    
    @Autowired
    private VNPayService vnPayService;
    @Autowired
    private SePayService sePayService;
    @Autowired
    private RoomPricingRepositoty roomPricingRepositoty;
    @Autowired
    private RoomTypeRepository roomTypeRepository;
    @Autowired
    private RoomImagesRepositoty roomImagesRepositoty;

    /**
     * ĐẶT PHÒNG CHO KHÁCH HÀNG: Xử lý đặt phòng đơn lẻ hoặc combo nhiều loại phòng
     * @param dto - Thông tin đặt phòng từ khách hàng
     * @return boolean - true nếu đặt phòng thành công, false nếu thất bại
     */
    public boolean datPhongChoKhachHang(BookingRequestDTO dto) {
        try {
            // Kiểm tra loại booking: single room hoặc multiple room types
            if ("multiple".equals(dto.getRoomSelection()) && dto.getMultiRoomTypes() != null && !dto.getMultiRoomTypes().isEmpty()) {
                return datPhongComboChoKhachHang(dto);
            }
            
            // Logic cũ cho single room type
            var roomType = roomTypeRepository.findById(dto.getRoomTypeId()).orElse(null);
            if (roomType == null) return false;

            // Tạo khách hàng nếu chưa có (giả sử theo email)
            Customer customer = customerRepository.findByEmail(dto.getEmailKhach()).orElseGet(() -> {
                Customer c = new Customer();
                c.setHo(dto.getHoKhach());
                c.setTen(dto.getTenKhach());
                c.setEmail(dto.getEmailKhach());
                c.setSoDienThoai(dto.getSoDienThoai());
                c.setSoCmndCccd(dto.getSoCmndCccd());
                c.setDiaChi(dto.getDiaChi());
                c.setNgaySinh(dto.getNgaySinh());
                if (dto.getGioiTinh() != null && !dto.getGioiTinh().isEmpty()) {
                    c.setGioiTinh("NAM".equals(dto.getGioiTinh()) ? Customer.GioiTinh.NAM : Customer.GioiTinh.NU);
                }
                c.setQuocTich(dto.getQuocTich());
                c.setMaKhachHang("KH" + System.currentTimeMillis());
                c.setMatKhauHash("default");
                return customerRepository.save(c);
            });

            // Get hotel (assuming single hotel model)
            Hotel hotel = hotelRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách sạn"));

            Booking booking = new Booking();
            booking.setKhachHang(customer);
            booking.setHotel(hotel);
            booking.setNgayNhanPhong(dto.getNgayNhanPhong());
            booking.setNgayTraPhong(dto.getNgayTraPhong());
            booking.setSoNguoiLon(dto.getSoNguoiLon());
            booking.setSoTreEm(dto.getSoTreEm());
            booking.setGhiChuKhachHang(dto.getGhiChuKhachHang());
            booking.setYeuCauDacBiet(dto.getYeuCauDacBiet());
            booking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.CHO_XAC_NHAN);
            booking.setNgayDat(LocalDateTime.now());
            // Lưu loại phòng khách chọn
            booking.setRoomType(roomType);
            
            // Kiểm tra logic số giường vs số người lớn và số phòng
            int soNguoiLon = dto.getSoNguoiLon() != null ? dto.getSoNguoiLon() : 0;
            int soTreEm = dto.getSoTreEm() != null ? dto.getSoTreEm() : 0;
            int soGiuong = dto.getSoGiuong() != null ? dto.getSoGiuong() : 1;
            int soPhong = dto.getSoPhong() != null ? dto.getSoPhong() : 1;
            
            String ghiChuHeThong = "";
            
            // Cảnh báo nếu 2+ người lớn nhưng chọn 1 giường
            if (soNguoiLon >= 2 && soGiuong == 1) {
                ghiChuHeThong += "LƯU Ý: Khách đặt " + soNguoiLon + " người lớn nhưng chọn 1 giường - có thể cần tư vấn thêm. ";
            }
            
            // Kiểm tra sức chứa và số phòng
            int tongSoKhach = soNguoiLon + soTreEm;
            int sucChuaToiDa = 2; // Sức chứa tối đa mỗi phòng
            int soPhongCanThiet = (int) Math.ceil((double) tongSoKhach / sucChuaToiDa);
            
            if (soPhong < soPhongCanThiet) {
                ghiChuHeThong += String.format("CẢNH BÁO: %d khách cần ít nhất %d phòng nhưng chỉ đặt %d phòng. ", 
                    tongSoKhach, soPhongCanThiet, soPhong);
            }
            
            if (soPhong > 1) {
                ghiChuHeThong += String.format("Khách đặt %d phòng cùng loại. ", soPhong);
            }
            
            // Logic kiểm tra sức chứa đã được xử lý ở trên
            
            // Gán ghi chú hệ thống vào booking
            if (!ghiChuHeThong.isEmpty()) {
                if (dto.getGhiChuKhachHang() != null && !dto.getGhiChuKhachHang().trim().isEmpty()) {
                    booking.setGhiChuKhachHang(ghiChuHeThong + dto.getGhiChuKhachHang());
                } else {
                    booking.setGhiChuKhachHang(ghiChuHeThong + "Nhân viên sẽ liên hệ để xác nhận.");
                }
            }
            
            // Sinh mã đặt phòng tự động
            String maDatPhong = "BOOK" + System.currentTimeMillis();
            booking.setMaDatPhong(maDatPhong);
            // --- TÍNH GIÁ THEO LOẠI THUÊ ---
            String bookingType = dto.getBookingType();
            String loaiGia = dto.getLoaiGia(); // Loại giá từ frontend: giaNgay, giaGio, giaQuaDem
            Integer bookingQuantity = dto.getBookingQuantity() != null && dto.getBookingQuantity() > 0 ? dto.getBookingQuantity() : 1;
            BigDecimal unitPrice = BigDecimal.ZERO;
            
            // Lấy thông tin giá từ RoomPricing
            RoomPricing pricing = roomPricingRepositoty.findFirstByRoomType_IdAndLoaiGia(roomType.getId(), RoomPricing.LoaiGia.BASE);
            if (pricing != null) {
                // Ưu tiên sử dụng loaiGia từ frontend nếu có
                if (loaiGia != null && !loaiGia.isEmpty()) {
                    if ("giaNgay".equals(loaiGia)) unitPrice = pricing.getGiaNgay();
                    else if ("giaGio".equals(loaiGia)) unitPrice = pricing.getGiaGio();
                    else if ("giaQuaDem".equals(loaiGia)) unitPrice = pricing.getGiaQuaDem();
                } else {
                    // Fallback về logic cũ nếu không có loaiGia
                    if ("ngay".equals(bookingType)) unitPrice = pricing.getGiaNgay();
                    else if ("gio".equals(bookingType)) unitPrice = pricing.getGiaGio();
                    else if ("dem".equals(bookingType)) unitPrice = pricing.getGiaQuaDem();
                }
            }
            // Tính tổng tiền cho nhiều phòng
            BigDecimal tongTienPhong = unitPrice.multiply(BigDecimal.valueOf(bookingQuantity)).multiply(BigDecimal.valueOf(soPhong));
            
            BigDecimal tienDatCoc = tongTienPhong.divide(BigDecimal.valueOf(2), 0, java.math.RoundingMode.HALF_UP);
            booking.setTongTienPhong(tongTienPhong);
            booking.setTongThanhToan(tongTienPhong);
            booking.setTienDatCoc(tienDatCoc);
            
            // TODO: set thêm các trường khác nếu cần

            bookingRepository.save(booking);

            // --- Tạo URL QR code SePay để tạo nút click ---
            String qrImageUrl = sePayService.getSePayQRImageUrl(
                booking.getTienDatCoc(), 
                maDatPhong, 
                "DatPhong_" + maDatPhong
            );
            
            // Fallback: nếu URL SePay không hoạt động, tạo QR code bằng ZXing
            if (qrImageUrl == null || qrImageUrl.isEmpty()) {
                System.out.println("SePay URL failed, generating QR with ZXing fallback");
                byte[] qrCodeBytes = sePayService.generateSePayQRCodeFallback(
                    booking.getTienDatCoc(), 
                    maDatPhong, 
                    "DatPhong_" + maDatPhong
                );
                if (qrCodeBytes != null) {
                    // Tạo data URL cho QR code ZXing
                    String base64QR = java.util.Base64.getEncoder().encodeToString(qrCodeBytes);
                    qrImageUrl = "data:image/png;base64," + base64QR;
                } else {
                    // Fallback cuối cùng: sử dụng placeholder
                    qrImageUrl = "https://via.placeholder.com/250x250?text=QR+Code+Error";
                }
            }
            
            // Không cần attachment nữa vì sử dụng nút click
            InputStreamSource qrImage = null;

            // Gửi mail cho khách hàng
            if (mailSender != null) {
                String subject = "[Panacea Hotel] Yêu cầu đặt phòng đã được gửi";
                StringBuilder dichVuHtml = new StringBuilder();
                if (dto.getDichVu() != null && !dto.getDichVu().isEmpty()) {
                    dichVuHtml.append("<li><b>Dịch vụ đăng ký:</b> <ul>");
                    for (String dv : dto.getDichVu()) {
                        dichVuHtml.append("<li>").append(dv).append("</li>");
                    }
                    dichVuHtml.append("</ul></li>");
                }
                String bookingTypeLabel = "Theo ngày";
                if ("gio".equals(dto.getBookingType())) bookingTypeLabel = "Theo giờ";
                else if ("dem".equals(dto.getBookingType())) bookingTypeLabel = "Theo đêm";

                // Sửa lỗi ở đây
                String text = String.format(
                        "<h2>Cảm ơn %s đã đặt phòng tại Panacea Hotel!</h2>" +
                                "<p>Thông tin đặt phòng của bạn:</p>" +
                                "<ul>" +
                                "<li>Khách sạn: Panacea Hotel</li>" +
                                "<li>Loại phòng: %s</li>" +
                                "<li>Loại thuê: %s</li>" +
                                "<li>Số lượng: %d</li>" +
                                "<li>Ngày nhận phòng: %s</li>" +
                                "<li>Ngày trả phòng: %s</li>" +
                                "<li>Số người lớn: %d</li>" +
                                "<li>Số trẻ em: %d</li>" +
                                "<li>Ghi chú: %s</li>" +
                                "%s" + // Dịch vụ HTML
                                "</ul>" +
                                "<div style='background: #f8f9fa; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #10b981;'>" +
                                "<h3 style='color: #10b981; margin-top: 0;'>Tổng kết thanh toán</h3>" +
                                "<p><b>Tổng cộng:</b> %,.0f VNĐ</p>" +
                                "<p><b>Tiền cọc (50%%):</b> %,.0f VNĐ</p>" +
                                "<p><b>Số tiền còn lại:</b> %,.0f VNĐ (thanh toán khi nhận phòng)</p>" +
                                "</div>" +
                    "<p>Vui lòng thanh toán <b>tiền cọc</b> qua SePay bằng cách nhấn nút QR dưới đây:</p>" +
                    "<div style='text-align: center; margin: 20px 0;'>" +
                    "<a href='%s' target='_blank' " +
                    "style='display: inline-block; background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%); " +
                    "color: white; padding: 20px 40px; text-decoration: none; border-radius: 50px; " +
                    "font-weight: bold; font-size: 18px; box-shadow: 0 8px 25px rgba(102, 126, 234, 0.3); " +
                    "transition: all 0.3s ease; border: none; cursor: pointer;'>" +
                    "📱 Quét QR Code SePay</a>" +
                    "</div>" +
                    "<p><b>Tài khoản ngân hàng:</b> %s - %s</p>" +
                    "<p><b>Số tiền cọc cần thanh toán:</b> %,.0f VNĐ</p>" +
                    "<p><b>Nội dung chuyển khoản: </b>DatPhong_%s</p>" +
                    "<p><b>Lưu ý:</b> Đây là tiền cọc (50%% tổng tiền phòng). Số tiền còn lại sẽ thanh toán khi nhận phòng.</p>" +
                    "<p><b>Hướng dẫn:</b> Nhấn nút trên để mở QR code SePay, sau đó quét bằng app ngân hàng để thanh toán.</p>" +
                                "<p>Yêu cầu của bạn đang chờ xác nhận từ nhân viên. Chúng tôi sẽ gửi email xác nhận khi đặt phòng được duyệt.</p>" +
                                "<br><b>Panacea Hotel</b>",
                        dto.getTenKhach(),
                        roomType.getTenLoaiPhong(),
                        bookingTypeLabel,
                        dto.getBookingQuantity() != null ? dto.getBookingQuantity() : 1,
                        dto.getNgayNhanPhong(),
                        dto.getNgayTraPhong(),
                        dto.getSoNguoiLon(),
                        dto.getSoTreEm(),
                        dto.getGhiChuKhachHang() != null ? dto.getGhiChuKhachHang() : "Không có",
                        dichVuHtml.toString(),
                        tongTienPhong.doubleValue(),
                        booking.getTienDatCoc().doubleValue(),
                        tongTienPhong.subtract(booking.getTienDatCoc()).doubleValue(),
                        qrImageUrl != null ? qrImageUrl : "https://via.placeholder.com/250x250?text=QR+Code+Error",
                        sePayService.getBankAccountInfo().get("account"),
                        sePayService.getBankAccountInfo().get("bank"),
                        booking.getTienDatCoc().doubleValue(),
                        maDatPhong
                );
                // Thêm phần VNPAY vào text (cũng sử dụng tiền cọc)
                text += "<p>Hoặc thanh toán <b>tiền cọc</b> bằng VNPAY:</p>" +
                    "<div style='text-align: center; margin: 20px 0;'>" +
                    "<a href='" + (vnPayService != null ? vnPayService.createPaymentUrl(booking.getTienDatCoc().longValue(), maDatPhong) : "#") + "' " +
                    "style='display: inline-block; background-color: #ff6b35; color: white; padding: 15px 40px; text-decoration: none; border-radius: 50px; font-weight: bold; font-size: 18px; box-shadow: 0 4px 15px rgba(255, 107, 53, 0.3);'>" +
                    "🚀 Thanh toán cọc với VNPAY</a>" +
                    "</div>" +
                    "<p><b>Số tiền cọc cần thanh toán: </b>" + String.format("%,.0f", booking.getTienDatCoc().doubleValue()) + " VNĐ</p>" +
                    "<p><b>Mã đặt phòng: </b>" + maDatPhong + "</p>" +
                    "<p><b>Lưu ý:</b> Đây là tiền cọc (50%% tổng tiền phòng). Click vào nút trên để thanh toán an toàn và nhanh chóng.</p>" +
                    "<p>Yêu cầu của bạn đang chờ xác nhận từ nhân viên. Chúng tôi sẽ gửi email xác nhận khi đặt phòng được duyệt.</p>" +
                    "<br><b>Panacea Hotel</b>";
                
            // Gửi email với nút QR code (không cần attachment)
            sendMail(dto.getEmailKhach(), subject, text);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * LẤY TẤT CẢ LOẠI PHÒNG CHO KHÁCH HÀNG: Lấy danh sách loại phòng với giá và hình ảnh
     * @return List<RoomTypeDTO> - Danh sách loại phòng với thông tin đầy đủ
     */
    public List<RoomTypeDTO> getAllRoomTypesForCustomer() {
        List<panacea.website_dat_lich_khach_san.entity.RoomType> roomTypes = roomTypeRepository.findAll();
        List<RoomTypeDTO> result = new ArrayList<>();
        for (var rt : roomTypes) {
            // Debug log để kiểm tra dienTich
            System.out.println("[DEBUG] RoomType ID: " + rt.getId() + ", Ten: " + rt.getTenLoaiPhong() + ", DienTich: " + rt.getDienTich());
            
            // Lấy giá BASE
            RoomPricing pricing = roomPricingRepositoty.findFirstByRoomType_IdAndLoaiGia(rt.getId(), RoomPricing.LoaiGia.BASE);
            List<RoomPricing> pricings = pricing != null ? List.of(pricing) : new ArrayList<>();
            
            RoomTypeDTO dto = RoomTypeDTO.fromEntityWithPricing(rt, pricings);
            
            // Debug log sau khi convert
            System.out.println("[DEBUG] DTO DienTich: " + dto.getDienTich());
            
            // Lấy ảnh từ bảng RoomImages
            var images = roomImagesRepositoty.findByLoaiPhong_Id(rt.getId());
            List<String> urls = new ArrayList<>();
            for (var img : images) {
                urls.add(img.getUrlHinhAnh());
            }
            dto.setImageUrls(urls);
            result.add(dto);
        }
        return result;
    }

    /**
     * LẤY LOẠI PHÒNG THEO ID: Lấy thông tin chi tiết loại phòng theo ID
     * @param id - ID loại phòng
     * @return RoomTypeDTO - Thông tin loại phòng hoặc null nếu không tìm thấy
     */
    public RoomTypeDTO getRoomTypeDTOById(Integer id) {
        System.out.println("[DEBUG] ===== getRoomTypeDTOById called with ID: " + id + " =====");
        System.out.println("[DEBUG] Service method is being called!");
        var rtOpt = roomTypeRepository.findById(id);
        if (rtOpt.isEmpty()) {
            System.out.println("[ERROR] RoomType not found for ID: " + id);
            return null;
        }
        var rt = rtOpt.get();
        System.out.println("[DEBUG] RoomType found: " + rt.getTenLoaiPhong());
        
        // Debug log để kiểm tra dienTich
        System.out.println("[DEBUG] Single RoomType ID: " + rt.getId() + ", Ten: " + rt.getTenLoaiPhong() + ", DienTich: " + rt.getDienTich());
        
        // Lấy tất cả các loại giá (NGAY, GIO, QUA_DEM)
        System.out.println("[DEBUG] Querying pricing for room type ID: " + rt.getId());
        List<RoomPricing> pricings = roomPricingRepositoty.findByRoomTypeId(rt.getId());
        System.out.println("[DEBUG] Found " + pricings.size() + " pricing records for room type " + rt.getId());
        
        // Debug thêm về repository
        System.out.println("[DEBUG] Repository class: " + roomPricingRepositoty.getClass().getName());
        
        // Debug từng loại giá
        for (RoomPricing pricing : pricings) {
            System.out.println("[DEBUG] Pricing ID: " + pricing.getId() + 
                             ", LoaiGia: " + pricing.getLoaiGia() + 
                             ", giaGio=" + pricing.getGiaGio() + 
                             ", giaNgay=" + pricing.getGiaNgay() + 
                             ", giaQuaDem=" + pricing.getGiaQuaDem() +
                             ", giaTri=" + pricing.getGiaTri());
        }
        
        // Nếu không có dữ liệu, tạo dữ liệu fallback
        if (pricings.isEmpty()) {
            System.out.println("[ERROR] No pricing data found for room type " + rt.getId() + " in database!");
            System.out.println("[ERROR] Creating fallback pricing data...");
            
            // Tạo RoomPricing fallback
            RoomPricing fallbackPricing = new RoomPricing();
            fallbackPricing.setRoomType(rt);
            fallbackPricing.setLoaiGia(RoomPricing.LoaiGia.BASE);
            fallbackPricing.setGiaGio(new BigDecimal("1200"));
            fallbackPricing.setGiaNgay(new BigDecimal("14000"));
            fallbackPricing.setGiaQuaDem(new BigDecimal("15000"));
            fallbackPricing.setGiaTri(new BigDecimal("14000"));
            fallbackPricing.setNgayBatDau(java.time.LocalDate.now());
            fallbackPricing.setNgayKetThuc(java.time.LocalDate.now().plusYears(1));
            
            pricings = List.of(fallbackPricing);
            System.out.println("[DEBUG] Created fallback pricing: giaGio=1200, giaNgay=14000, giaQuaDem=15000");
        }
        
        // Lấy pricing đầu tiên từ danh sách
        RoomPricing pricing = pricings.isEmpty() ? null : pricings.get(0);
        
        // Debug log pricing
        System.out.println("[DEBUG] RoomPricing found: " + (pricing != null ? "YES" : "NO"));
        if (pricing != null) {
            System.out.println("[DEBUG] Pricing details: giaNgay=" + pricing.getGiaNgay() + ", giaGio=" + pricing.getGiaGio() + ", giaQuaDem=" + pricing.getGiaQuaDem());
        }
        
        // Nếu không tìm thấy pricing, thử lấy pricing đầu tiên từ tất cả
        if (pricing == null) {
            List<RoomPricing> allPricings = roomPricingRepositoty.findAll().stream()
                .filter(p -> p.getRoomType() != null && p.getRoomType().getId().equals(rt.getId()))
                .collect(java.util.stream.Collectors.toList());
            System.out.println("[DEBUG] All pricings for room type " + rt.getId() + ": " + allPricings.size());
            if (!allPricings.isEmpty()) {
                pricing = allPricings.get(0);
                pricings = List.of(pricing);
                System.out.println("[DEBUG] Using first available pricing: giaNgay=" + pricing.getGiaNgay() + ", giaGio=" + pricing.getGiaGio() + ", giaQuaDem=" + pricing.getGiaQuaDem());
            }
        }
        
        RoomTypeDTO dto = RoomTypeDTO.fromEntityWithPricing(rt, pricings);
        
        // Nếu không có giá, sử dụng giá mặc định
        if (dto.getGiaNgay() == null || dto.getGiaNgay().compareTo(java.math.BigDecimal.ZERO) == 0) {
            dto.setGiaNgay(new java.math.BigDecimal("1500000")); // 1.5 triệu VNĐ
            System.out.println("[DEBUG] Sử dụng giá mặc định cho giaNgay: 1,500,000");
        }
        if (dto.getGiaGio() == null || dto.getGiaGio().compareTo(java.math.BigDecimal.ZERO) == 0) {
            dto.setGiaGio(new java.math.BigDecimal("200000")); // 200k VNĐ
            System.out.println("[DEBUG] Sử dụng giá mặc định cho giaGio: 200,000");
        }
        if (dto.getGiaQuaDem() == null || dto.getGiaQuaDem().compareTo(java.math.BigDecimal.ZERO) == 0) {
            dto.setGiaQuaDem(new java.math.BigDecimal("800000")); // 800k VNĐ
            System.out.println("[DEBUG] Sử dụng giá mặc định cho giaQuaDem: 800,000");
        }
        
        // Debug log cuối cùng
        System.out.println("[DEBUG] Giá cuối cùng sau khi xử lý: giaNgay=" + dto.getGiaNgay() + ", giaGio=" + dto.getGiaGio() + ", giaQuaDem=" + dto.getGiaQuaDem());
        
        // Debug log sau khi convert
        System.out.println("[DEBUG] Single DTO DienTich: " + dto.getDienTich());
        
        // Log debug giá
        System.out.println("[DEBUG] Giá phòng DTO: id=" + id + ", giaNgay=" + dto.getGiaNgay() + ", giaGio=" + dto.getGiaGio() + ", giaQuaDem=" + dto.getGiaQuaDem());
        // Lấy ảnh từ bảng RoomImages
        var images = roomImagesRepositoty.findByLoaiPhong_Id(rt.getId());
        List<String> urls = new ArrayList<>();
        for (var img : images) {
            urls.add(img.getUrlHinhAnh());
        }
        dto.setImageUrls(urls);
        return dto;
    }

    /**
     * GỬI EMAIL: Gửi email HTML đến khách hàng
     * @param to - Email người nhận
     * @param subject - Tiêu đề email
     * @param text - Nội dung HTML
     * @throws MessagingException - Lỗi gửi email
     */
    private void sendMail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true);
        mailSender.send(message);
    }

    /**
     * SINH QR CODE: Tạo mã QR từ chuỗi text (dùng ZXing)
     * @param text - Nội dung cần tạo QR
     * @param width - Chiều rộng QR code
     * @param height - Chiều cao QR code
     * @return byte[] - Dữ liệu ảnh PNG hoặc null nếu lỗi
     */
    private byte[] generateQRCodeImage(String text, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);
            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * GỬI EMAIL KÈM QR CODE: Gửi email HTML kèm ảnh QR code inline
     * @param to - Email người nhận
     * @param subject - Tiêu đề email
     * @param html - Nội dung HTML
     * @param qrImage - Ảnh QR code để đính kèm
     * @throws MessagingException - Lỗi gửi email
     */
    private void sendMailWithQRFile(String to, String subject, String html, InputStreamSource qrImage) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);
        if (qrImage != null) {
            helper.addInline("qr_sepay", qrImage, "image/png");
        }
        mailSender.send(message);
    }

    /**
     * Lấy danh sách 4 loại phòng chính cho khách hàng (Standard, Superior, Deluxe, Suite)
     */
    public List<RoomType> getMainRoomTypes() {
        // Giả sử mã loại phòng là: STANDARD, SUPERIOR, DELUXE, SUITE
        List<String> mainCodes = Arrays.asList("STANDARD", "SUPERIOR", "DELUXE", "SUITE");
        return roomTypeRepository.findAll().stream()
                .filter(rt -> mainCodes.contains(rt.getMaLoaiPhong()))
                .toList();
    }

    /**
     * Lấy chi tiết loại phòng theo id
     */
    public RoomType getRoomTypeById(Integer id) {
        Optional<RoomType> opt = roomTypeRepository.findById(id);
        return opt.orElse(null);
    }

    /**
     * Lấy giá cơ bản (BASE) của loại phòng
     */
    public java.math.BigDecimal getRoomBasePriceByRoomTypeId(Integer roomTypeId) {
        return roomPricingRepositoty.findFirstByRoomTypeIdAndLoaiGiaOrderByNgayBatDauDesc(roomTypeId, RoomPricing.LoaiGia.BASE)
                .map(RoomPricing::getGiaTri)
                .orElse(null);
    }

    /**
     * LẤY BOOKING THEO EMAIL: Lấy danh sách đặt phòng của khách hàng theo email
     * @param email - Email khách hàng
     * @return List<Booking> - Danh sách booking của khách hàng
     */
    public List<Booking> getBookingsByEmail(String email) {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        if (customer.isPresent()) {
            return bookingRepository.findByKhachHang(customer.get());
        }
        return new ArrayList<>();
    }
    
    /**
     * Xử lý đặt phòng combo nhiều loại phòng khác nhau
     */
    public boolean datPhongComboChoKhachHang(BookingRequestDTO dto) {
        try {
            // Tạo khách hàng nếu chưa có
            Customer customer = customerRepository.findByEmail(dto.getEmailKhach()).orElseGet(() -> {
                Customer c = new Customer();
                c.setHo(dto.getHoKhach());
                c.setTen(dto.getTenKhach());
                c.setEmail(dto.getEmailKhach());
                c.setSoDienThoai(dto.getSoDienThoai());
                c.setSoCmndCccd(dto.getSoCmndCccd());
                c.setDiaChi(dto.getDiaChi());
                c.setNgaySinh(dto.getNgaySinh());
                if (dto.getGioiTinh() != null && !dto.getGioiTinh().isEmpty()) {
                    c.setGioiTinh("NAM".equals(dto.getGioiTinh()) ? Customer.GioiTinh.NAM : Customer.GioiTinh.NU);
                }
                c.setQuocTich(dto.getQuocTich());
                c.setMaKhachHang("KH" + System.currentTimeMillis());
                c.setMatKhauHash("default");
                return customerRepository.save(c);
            });
            
            // Get hotel (assuming single hotel model)
            Hotel hotel = hotelRepository.findAll().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách sạn"));

            // Tạo booking chính cho combo
            Booking mainBooking = new Booking();
            mainBooking.setKhachHang(customer);
            mainBooking.setHotel(hotel);
            mainBooking.setNgayNhanPhong(dto.getNgayNhanPhong());
            mainBooking.setNgayTraPhong(dto.getNgayTraPhong());
            mainBooking.setSoNguoiLon(dto.getSoNguoiLon());
            mainBooking.setSoTreEm(dto.getSoTreEm());
            mainBooking.setGhiChuKhachHang(dto.getGhiChuKhachHang());
            mainBooking.setYeuCauDacBiet(dto.getYeuCauDacBiet());
            mainBooking.setTrangThaiDatPhong(Booking.TrangThaiDatPhong.CHO_XAC_NHAN);
            mainBooking.setNgayDat(LocalDateTime.now());
            
            // Sinh mã đặt phòng cho combo
            String maDatPhong = "COMBO" + System.currentTimeMillis();
            mainBooking.setMaDatPhong(maDatPhong);
            
            // Tính tổng tiền cho tất cả các loại phòng
            BigDecimal tongTienCombo = BigDecimal.ZERO;
            StringBuilder ghiChuCombo = new StringBuilder("COMBO PHÒNG: ");
            
            for (BookingRequestDTO.MultiRoomTypeRequest roomTypeReq : dto.getMultiRoomTypes()) {
                // Tìm room type theo tên
                RoomType roomType = findRoomTypeByName(roomTypeReq.getRoomType());
                if (roomType == null) {
                    continue; // Bỏ qua nếu không tìm thấy loại phòng
                }
                
                // Lấy giá của loại phòng này
                BigDecimal unitPrice = BigDecimal.valueOf(roomTypeReq.getPrice() != null ? roomTypeReq.getPrice() : 0);
                Integer quantity = roomTypeReq.getQuantity() != null ? roomTypeReq.getQuantity() : 1;
                
                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
                tongTienCombo = tongTienCombo.add(subtotal);
                
                // Thêm vào ghi chú
                ghiChuCombo.append(String.format("%d x %s (%.0f VNĐ), ", 
                    quantity, roomType.getTenLoaiPhong(), unitPrice));
            }
            
            // Set thông tin tổng cho booking chính
            mainBooking.setTongTienPhong(tongTienCombo);
            
            // Đảm bảo tong_thanh_toan không null và có giá trị hợp lệ
            BigDecimal tongThanhToan = tongTienCombo;
            if (tongThanhToan == null || tongThanhToan.compareTo(BigDecimal.ZERO) < 0) {
                tongThanhToan = BigDecimal.ZERO;
            }
            mainBooking.setTongThanhToan(tongThanhToan);
            
            BigDecimal tienDatCoc = tongTienCombo.divide(BigDecimal.valueOf(2), 0, java.math.RoundingMode.HALF_UP);
            mainBooking.setTienDatCoc(tienDatCoc);
            
            // Cập nhật ghi chú
            String finalNote = ghiChuCombo.toString();
            if (finalNote.endsWith(", ")) {
                finalNote = finalNote.substring(0, finalNote.length() - 2);
            }
            if (dto.getGhiChuKhachHang() != null && !dto.getGhiChuKhachHang().trim().isEmpty()) {
                mainBooking.setGhiChuKhachHang(finalNote + ". " + dto.getGhiChuKhachHang());
            } else {
                mainBooking.setGhiChuKhachHang(finalNote + ". Nhân viên sẽ liên hệ để xác nhận chi tiết.");
            }
            
            // Lưu booking
            bookingRepository.save(mainBooking);
            
            // Gửi email thông báo (có thể tùy chỉnh template cho combo)
            sendComboBookingEmail(dto, customer, tongTienCombo, finalNote);
            
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Tìm room type theo tên (single, double, suite)
     */
    private RoomType findRoomTypeByName(String roomTypeName) {
        switch (roomTypeName.toLowerCase()) {
            case "single":
                return roomTypeRepository.findByMaLoaiPhong("STANDARD").orElse(null);
            case "double":
                return roomTypeRepository.findByMaLoaiPhong("SUPERIOR").orElse(null);
            case "suite":
                return roomTypeRepository.findByMaLoaiPhong("SUITE").orElse(null);
            default:
                return null;
        }
    }
    
    /**
     * Gửi email cho booking combo
     */
    private void sendComboBookingEmail(BookingRequestDTO dto, Customer customer, BigDecimal tongTien, String comboDetails) {
        if (mailSender != null) {
            try {
                String subject = "[Panacea Hotel] Yêu cầu đặt combo phòng đã được gửi";
                String text = String.format(
                    "<h2>Cảm ơn %s đã đặt combo phòng tại Panacea Hotel!</h2>" +
                    "<p>Thông tin đặt phòng combo của bạn:</p>" +
                    "<ul>" +
                    "<li>Khách sạn: Panacea Hotel</li>" +
                    "<li>Combo phòng: %s</li>" +
                    "<li>Ngày nhận phòng: %s</li>" +
                    "<li>Ngày trả phòng: %s</li>" +
                    "<li>Số người lớn: %d</li>" +
                    "<li>Số trẻ em: %d</li>" +
                    "<li>Tổng tiền: %s VNĐ</li>" +
                    "<li>Ghi chú: %s</li>" +
                    "</ul>" +
                    "<p>Nhân viên sẽ liên hệ để xác nhận chi tiết và hướng dẫn thanh toán.</p>" +
                    "<p>Cảm ơn bạn đã tin tưởng Panacea Hotel!</p>",
                    dto.getTenKhach(),
                    comboDetails,
                    dto.getNgayNhanPhong(),
                    dto.getNgayTraPhong(),
                    dto.getSoNguoiLon(),
                    dto.getSoTreEm(),
                    new java.text.DecimalFormat("#,###").format(tongTien),
                    dto.getGhiChuKhachHang() != null ? dto.getGhiChuKhachHang() : "Không có"
                );
                
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
                helper.setTo(dto.getEmailKhach());
                helper.setSubject(subject);
                helper.setText(text, true);
                helper.setFrom("noreply@panaceahotel.com");
                
                mailSender.send(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Thêm dữ liệu giá phòng mẫu để test
     */
    public boolean addSamplePricingData() {
        try {
            // Kiểm tra xem đã có dữ liệu chưa
            List<RoomPricing> existingPricings = roomPricingRepositoty.findAll();
            if (!existingPricings.isEmpty()) {
                System.out.println("[DEBUG] Đã có " + existingPricings.size() + " bản ghi pricing trong database");
                return true;
            }

            // Lấy danh sách room types
            List<RoomType> roomTypes = roomTypeRepository.findAll();
            if (roomTypes.isEmpty()) {
                System.out.println("[DEBUG] Không có room types nào trong database");
                return false;
            }

            System.out.println("[DEBUG] Tìm thấy " + roomTypes.size() + " room types");

            // Thêm pricing cho mỗi room type
            for (RoomType roomType : roomTypes) {
                RoomPricing pricing = new RoomPricing();
                pricing.setRoomType(roomType);
                pricing.setLoaiGia(RoomPricing.LoaiGia.BASE);
                pricing.setGiaTri(new BigDecimal("1500000")); // 1.5 triệu
                pricing.setGiaNgay(new BigDecimal("1500000")); // 1.5 triệu
                pricing.setGiaGio(new BigDecimal("200000")); // 200k
                pricing.setGiaQuaDem(new BigDecimal("800000")); // 800k
                pricing.setNgayBatDau(java.time.LocalDate.of(2024, 1, 1));
                pricing.setNgayKetThuc(java.time.LocalDate.of(2025, 12, 31));
                pricing.setApDungCho("All");
                pricing.setHeSoDieuChinh(BigDecimal.ONE);
                pricing.setTrangThai("Hoạt động");
                
                roomPricingRepositoty.save(pricing);
                System.out.println("[DEBUG] Đã thêm pricing cho room type: " + roomType.getTenLoaiPhong());
            }

            return true;
        } catch (Exception e) {
            System.out.println("[DEBUG] Lỗi khi thêm dữ liệu pricing: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}