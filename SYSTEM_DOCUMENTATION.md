# HỆ THỐNG QUẢN LÝ KHÁCH SẠN PANACEA
## Tài liệu tổng hợp các chức năng hệ thống

---

## 🏨 TỔNG QUAN HỆ THỐNG

**Panacea Hotel Management System** là hệ thống quản lý khách sạn toàn diện được phát triển bằng Spring Boot, hỗ trợ 3 vai trò chính:
- **Admin**: Quản trị viên hệ thống
- **Nhân viên**: Nhân viên lễ tân/quản lý
- **Khách hàng**: Khách hàng đặt phòng

---

## 🔧 KIẾN TRÚC HỆ THỐNG

### Backend (Spring Boot)
- **Framework**: Spring Boot 3.x với Java 17
- **Database**: SQL Server với JPA/Hibernate
- **Security**: Spring Security + OAuth2 (Google, Facebook)
- **Email**: Spring Mail với Gmail SMTP
- **Connection Pool**: HikariCP

### Frontend
- **Template Engine**: Thymeleaf
- **CSS Framework**: Bootstrap 5
- **Icons**: Material Icons
- **JavaScript**: Vanilla JS + jQuery
- **Responsive**: Mobile-first design

---

## 📊 CẤU TRÚC DATABASE

### Entities chính:
1. **KhachSan** - Thông tin khách sạn
2. **Tang** - Quản lý tầng
3. **HangPhong** - Loại phòng (Standard, Deluxe, Suite)
4. **Phong** - Thông tin phòng cụ thể
5. **KhachHang** - Thông tin khách hàng
6. **NhanVien** - Thông tin nhân viên
7. **DatPhong** - Đặt phòng và booking
8. **ThanhToan** - Thanh toán
9. **DichVu** - Dịch vụ khách sạn
10. **KhuyenMai** - Chương trình khuyến mãi
11. **DanhGia** - Đánh giá khách hàng
12. **Kho** - Quản lý kho
13. **YeuCauBoSung** - Yêu cầu bổ sung vật tư

---

## 🎯 CHỨC NĂNG THEO VAI TRÒ

### 👑 ADMIN (Quản trị viên)
**Quản lý toàn diện hệ thống:**

#### 🏨 Quản lý Khách sạn
- Cập nhật thông tin khách sạn (tên, địa chỉ, liên hệ)
- Quản lý cấu hình hệ thống
- Thiết lập chính sách khách sạn

#### 🏢 Quản lý Tầng & Phòng
- **Quản lý tầng**: Thêm/sửa/xóa tầng
- **Quản lý hạng phòng**: Standard, Deluxe, Suite với giá khác nhau
- **Quản lý phòng**: Trạng thái phòng, bảo trì, dọn dẹp

#### 📋 Quản lý Đặt phòng
- Xem tất cả đặt phòng trong hệ thống
- Theo dõi trạng thái: Chờ xác nhận → Đã xác nhận → Đã nhận phòng → Hoàn thành/Hủy
- Chi tiết booking với thông tin khách hàng và nhân viên tạo

#### 👥 Quản lý Khách hàng
- Danh sách khách hàng đăng ký
- Lịch sử đặt phòng của khách hàng
- Thông tin liên hệ và preferences

#### 👨‍💼 Quản lý Nhân viên
- Thêm/sửa/xóa nhân viên
- Phân quyền và vai trò
- Theo dõi hiệu suất làm việc

#### 🛎️ Quản lý Dịch vụ
- Dịch vụ spa, massage, ăn uống
- Giá dịch vụ và availability
- Booking dịch vụ kèm phòng

#### 🎁 Quản lý Khuyến mãi
- Tạo mã giảm giá
- Thiết lập điều kiện áp dụng
- Theo dõi hiệu quả khuyến mãi

#### ⭐ Quản lý Đánh giá
- Xem đánh giá của khách hàng
- Phản hồi đánh giá
- Phân tích sentiment

#### 📦 Quản lý Kho
- Inventory management
- Theo dõi vật tư tiêu hao
- Cảnh báo hết hàng

#### 📊 Báo cáo & Thống kê
- Doanh thu theo ngày/tháng/năm
- Tỷ lệ lấp đầy phòng
- Top khách hàng VIP
- Hiệu suất nhân viên

#### ⚙️ Cài đặt Hệ thống
- Cấu hình email
- Backup/restore data
- Maintenance mode
- System logs

### 👨‍💼 NHÂN VIÊN (Staff)
**Quản lý vận hành hàng ngày:**

#### 📋 Quản lý Đặt phòng
- **Dashboard**: Tổng quan đặt phòng trong ngày
- **Xác nhận booking**: Xử lý đặt phòng chờ xác nhận
- **Check-in/Check-out**: Quản lý khách vào/ra
- **Walk-in booking**: Tạo đặt phòng cho khách đến trực tiếp
- **Room assignment**: Phân phòng cho khách

#### 🏠 Quản lý Phòng
- Cập nhật trạng thái phòng (Available, Occupied, Cleaning, Maintenance)
- Báo cáo sự cố phòng
- Lịch dọn phòng

#### 👥 Thông tin Khách hàng
- Tra cứu thông tin khách hàng
- Cập nhật thông tin liên hệ
- Ghi chú đặc biệt

#### 💳 Thanh toán
- Xử lý thanh toán check-out
- In hóa đơn
- Xử lý refund

#### 🛎️ Dịch vụ
- Booking dịch vụ cho khách
- Cập nhật trạng thái dịch vụ
- Tính phí dịch vụ

#### 📦 Quản lý Kho (Cơ bản)
- Xem inventory
- Tạo yêu cầu bổ sung vật tư
- Báo cáo tiêu hao

#### 📊 Báo cáo Ca làm việc
- Doanh thu ca làm việc
- Số lượng check-in/check-out
- Sự cố trong ca

### 🧑‍🤝‍🧑 KHÁCH HÀNG (Customer)
**Trải nghiệm đặt phòng trực tuyến:**

#### 🏠 Trang chủ & Khám phá
- **Giao diện hiện đại**: Responsive design với carousel hình ảnh
- **Thông tin khách sạn**: Giới thiệu, tiện ích, địa điểm
- **Gallery**: Hình ảnh phòng và dịch vụ
- **Virtual tour**: Tham quan ảo khách sạn

#### 🔍 Tìm kiếm & Đặt phòng
- **Search form**: Chọn ngày, số khách, loại phòng
- **Real-time availability**: Kiểm tra phòng trống
- **Price comparison**: So sánh giá các loại phòng
- **Booking flow**: Quy trình đặt phòng 3 bước đơn giản

#### 🔐 Đăng nhập & Đăng ký
- **OAuth2 Integration**: Đăng nhập Google, Facebook
- **Traditional login**: Email/password
- **Registration**: Đăng ký tài khoản mới
- **Password reset**: Quên mật khẩu

#### 📱 Quản lý Booking cá nhân
- **My Bookings**: Danh sách đặt phòng của tôi
- **Booking details**: Chi tiết từng booking
- **Cancellation**: Hủy đặt phòng (theo policy)
- **Modification**: Thay đổi ngày (nếu có thể)

#### ⭐ Đánh giá & Feedback
- **Review system**: Đánh giá sau khi ở
- **Rating**: Cho điểm từ 1-5 sao
- **Photo upload**: Upload hình ảnh review
- **Response**: Xem phản hồi từ khách sạn

#### 🛎️ Dịch vụ bổ sung
- **Service booking**: Đặt spa, massage, ăn uống
- **Special requests**: Yêu cầu đặc biệt
- **Room service**: Gọi dịch vụ phòng

#### 💳 Thanh toán
- **Multiple payment**: Nhiều phương thức thanh toán
- **Secure payment**: Bảo mật thông tin thẻ
- **Invoice**: Hóa đơn điện tử
- **Payment history**: Lịch sử thanh toán

---

## 🔒 BẢO MẬT & PHÂN QUYỀN

### Spring Security Configuration
- **Role-based access**: ADMIN, STAFF, CUSTOMER
- **Method-level security**: @PreAuthorize annotations
- **CSRF protection**: Bảo vệ chống tấn công CSRF
- **Session management**: Quản lý phiên đăng nhập

### OAuth2 Integration
- **Google OAuth2**: Đăng nhập bằng Google
- **Facebook OAuth2**: Đăng nhập bằng Facebook
- **JWT tokens**: Token-based authentication
- **Refresh tokens**: Tự động gia hạn phiên

### Data Protection
- **Password encryption**: BCrypt hashing
- **SQL injection prevention**: JPA Prepared Statements
- **XSS protection**: Thymeleaf auto-escaping
- **HTTPS enforcement**: SSL/TLS encryption

---

## 📧 HỆ THỐNG EMAIL

### SMTP Configuration
- **Provider**: Gmail SMTP
- **Authentication**: OAuth2 hoặc App Password
- **TLS encryption**: Bảo mật kết nối

### Email Templates
- **Booking confirmation**: Xác nhận đặt phòng
- **Check-in reminder**: Nhắc nhở check-in
- **Cancellation notice**: Thông báo hủy phòng
- **Promotional emails**: Email marketing
- **Password reset**: Đặt lại mật khẩu

---

## 📱 RESPONSIVE DESIGN

### Mobile-First Approach
- **Breakpoints**: 576px, 768px, 992px, 1200px
- **Touch-friendly**: Buttons và forms tối ưu cho touch
- **Fast loading**: Optimized images và CSS
- **Offline support**: Service worker cho basic caching

### Cross-Browser Support
- **Modern browsers**: Chrome, Firefox, Safari, Edge
- **Fallbacks**: Graceful degradation cho IE11
- **Progressive enhancement**: Core functionality first

---

## 🚀 DEPLOYMENT & MONITORING

### Production Setup
- **Application server**: Tomcat embedded
- **Database**: SQL Server cluster
- **Load balancer**: Nginx reverse proxy
- **SSL certificate**: Let's Encrypt

### Monitoring & Logging
- **Application logs**: Logback với rolling files
- **Error tracking**: Custom error pages
- **Performance monitoring**: JVM metrics
- **Health checks**: Spring Actuator endpoints

---

## 🔧 MAINTENANCE & SUPPORT

### Backup Strategy
- **Database backup**: Daily automated backups
- **File backup**: Static assets và uploads
- **Configuration backup**: Application properties

### Update Process
- **Rolling updates**: Zero-downtime deployment
- **Database migrations**: Flyway/Liquibase
- **Feature flags**: Gradual feature rollout

### Support Channels
- **Admin dashboard**: System health monitoring
- **Error reporting**: Automated error notifications
- **User support**: Help desk integration

---

## 📈 FUTURE ENHANCEMENTS

### Planned Features
- **Mobile app**: React Native app
- **AI chatbot**: Customer service automation
- **IoT integration**: Smart room controls
- **Analytics dashboard**: Advanced reporting
- **Multi-language**: Internationalization support

### Scalability Improvements
- **Microservices**: Service decomposition
- **Caching layer**: Redis implementation
- **CDN integration**: Static asset delivery
- **Database sharding**: Horizontal scaling

---

*Tài liệu này được tạo để hỗ trợ việc quản lý và kiểm soát hệ thống Panacea Hotel Management System. Vui lòng cập nhật khi có thay đổi chức năng.*

**Phiên bản**: 1.0  
**Ngày cập nhật**: 2024-12-15  
**Người tạo**: System Administrator