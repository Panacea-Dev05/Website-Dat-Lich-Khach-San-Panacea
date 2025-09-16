# TÀI LIỆU CHỨC NĂNG HỆ THỐNG WEBSITE ĐẶT LỊCH KHÁCH SẠN PANACEA

## 📋 TỔNG QUAN HỆ THỐNG

Hệ thống Website Đặt Lịch Khách Sạn Panacea là một ứng dụng web quản lý khách sạn toàn diện được xây dựng bằng Spring Boot, cung cấp các chức năng cho 3 nhóm người dùng chính:

- **Admin**: Quản lý toàn bộ hệ thống
- **Nhân viên**: Xử lý các tác vụ hàng ngày
- **Khách hàng**: Đặt phòng và sử dụng dịch vụ

---

## 🏗️ CẤU TRÚC DỮ LIỆU CHÍNH

### 1. **Hotel (Khách sạn)**
- **Chức năng**: Lưu trữ thông tin cơ bản về khách sạn
- **Thuộc tính chính**: Mã khách sạn, tên, địa chỉ, số sao, chính sách hủy
- **Trạng thái**: Hoạt động, Bảo trì, Đóng cửa

### 2. **Customer (Khách hàng)**
- **Chức năng**: Quản lý thông tin khách hàng
- **Thuộc tính chính**: Mã KH, họ tên, email, SĐT, CMND/CCCD, điểm tích lũy
- **Giới tính**: Nam, Nữ, Khác

### 3. **Room (Phòng)**
- **Chức năng**: Quản lý thông tin phòng khách sạn
- **Thuộc tính chính**: Số phòng, tầng, view, giá cơ bản
- **Trạng thái**: Sẵn sàng, Đã đặt, Đang sử dụng, Bảo trì, Dọn dẹp

### 4. **RoomType (Loại phòng)**
- **Chức năng**: Định nghĩa các loại phòng (Standard, Superior, Deluxe, Suite)
- **Thuộc tính chính**: Mã loại phòng, tên, diện tích, số giường, sức chứa

### 5. **Booking (Đặt phòng)**
- **Chức năng**: Quản lý thông tin đặt phòng
- **Trạng thái đặt phòng**: Chờ xác nhận, Đã xác nhận, Đã nhận phòng, Đã hoàn thành, Đã hủy
- **Trạng thái thanh toán**: Chưa thanh toán, Đã cọc, Đã thanh toán, Hoàn tiền
- **Loại booking**: Theo ngày, Theo giờ, Qua đêm

### 6. **ServiceEntity (Dịch vụ)**
- **Chức năng**: Quản lý các dịch vụ khách sạn
- **Thuộc tính chính**: Mã dịch vụ, tên, loại, đơn giá, đơn vị tính

### 7. **Payment (Thanh toán)**
- **Chức năng**: Quản lý thông tin thanh toán
- **Trạng thái**: Thành công, Thất bại, Đang xử lý, Đã hủy

---

## 🎯 CHỨC NĂNG THEO NHÓM NGƯỜI DÙNG

## 👑 ADMIN - QUẢN TRỊ HỆ THỐNG

### 1. **Quản lý Đặt phòng (AdminBookingController)**
- **Mục đích**: Quản lý toàn bộ booking trong hệ thống
- **Chức năng**:
  - ✅ Xem danh sách tất cả booking
  - ✅ Tạo booking mới cho khách hàng
  - ✅ Cập nhật thông tin booking
  - ✅ Xóa booking
  - ✅ Xem chi tiết booking đầy đủ

### 2. **Quản lý Khách hàng (AdminCustomerService)**
- **Mục đích**: Quản lý thông tin khách hàng
- **Chức năng**:
  - ✅ Xem danh sách tất cả khách hàng
  - ✅ Tạo/cập nhật thông tin khách hàng
  - ✅ Quản lý preferences khách hàng
  - ✅ Theo dõi session người dùng

### 3. **Quản lý Phòng (AdminRoomService)**
- **Mục đích**: Quản lý phòng và loại phòng
- **Chức năng**:
  - ✅ CRUD operations cho phòng
  - ✅ Quản lý loại phòng
  - ✅ Quản lý hình ảnh phòng
  - ✅ Quản lý giá phòng theo thời gian
  - ✅ Lọc phòng theo nhiều tiêu chí

### 4. **Quản lý Dịch vụ (AdminServiceService)**
- **Mục đích**: Quản lý các dịch vụ khách sạn
- **Chức năng**:
  - ✅ CRUD operations cho dịch vụ
  - ✅ Phân trang danh sách dịch vụ
  - ✅ Quản lý trạng thái dịch vụ

### 5. **Quản lý Thanh toán (AdminPaymentService)**
- **Mục đích**: Quản lý và theo dõi thanh toán
- **Chức năng**:
  - ✅ Xem lịch sử thanh toán
  - ✅ Lọc thanh toán theo booking/khách hàng
  - ✅ Quản lý trạng thái thanh toán

### 6. **Quản lý Đánh giá (AdminReviewService)**
- **Mục đích**: Quản lý đánh giá của khách hàng
- **Chức năng**:
  - ✅ Xem tất cả đánh giá
  - ✅ Duyệt/từ chối đánh giá
  - ✅ Phản hồi đánh giá

### 7. **Quản lý Nhân viên (AdminStaffService)**
- **Mục đích**: Quản lý thông tin nhân viên
- **Chức năng**:
  - ✅ CRUD operations cho nhân viên
  - ✅ Quản lý quyền hạn nhân viên

### 8. **Quản lý Khuyến mãi (AdminPromotionService)**
- **Mục đích**: Quản lý các chương trình khuyến mãi
- **Chức năng**:
  - ✅ CRUD operations cho promotion
  - ✅ Áp dụng khuyến mãi cho booking

### 9. **Quản lý Tầng (AdminFloorService)**
- **Mục đích**: Quản lý thông tin các tầng
- **Chức năng**:
  - ✅ Xem thống kê phòng theo tầng
  - ✅ Theo dõi trạng thái phòng từng tầng

---

## 👨‍💼 NHÂN VIÊN - XỬ LÝ TÁC VỤ HÀNG NGÀY

### 1. **Quản lý Đặt phòng (QuanLyDatPhongService)**
- **Mục đích**: Xử lý booking hàng ngày
- **Chức năng**:
  - ✅ Tạo booking mới
  - ✅ Check-in/Check-out khách hàng
  - ✅ Cập nhật trạng thái booking
  - ✅ Quản lý dịch vụ trong booking
  - ✅ Tính toán chi phí tự động

### 2. **Quản lý Phòng (QuanLyPhongService)**
- **Mục đích**: Quản lý trạng thái phòng
- **Chức năng**:
  - ✅ Xem danh sách phòng
  - ✅ Cập nhật trạng thái phòng
  - ✅ Quản lý loại phòng
  - ✅ Phân trang danh sách phòng

### 3. **Quản lý Dịch vụ (DichVuService)**
- **Mục đích**: Xử lý dịch vụ cho khách
- **Chức năng**:
  - ✅ Xem danh sách dịch vụ
  - ✅ Thêm dịch vụ cho booking
  - ✅ Quản lý inventory items

### 4. **Thanh toán (ThanhToanService)**
- **Mục đích**: Xử lý thanh toán
- **Chức năng**:
  - ✅ Xem danh sách thanh toán
  - ✅ Xử lý thanh toán mới
  - ✅ Theo dõi trạng thái thanh toán

### 5. **Thông tin Khách hàng (ThongTinKhachHangService)**
- **Mục đích**: Quản lý thông tin khách hàng
- **Chức năng**:
  - ✅ Xem danh sách khách hàng
  - ✅ Cập nhật thông tin khách hàng
  - ✅ Quản lý preferences

### 6. **Dashboard (HomeService)**
- **Mục đích**: Hiển thị thống kê tổng quan
- **Chức năng**:
  - ✅ Thống kê booking
  - ✅ Thống kê phòng
  - ✅ Thống kê thanh toán

---

## 👥 KHÁCH HÀNG - DỊCH VỤ ĐẶT PHÒNG

### 1. **Dịch vụ Khách hàng (KhachHangService)**
- **Mục đích**: Cung cấp dịch vụ cho khách hàng
- **Chức năng**:
  - ✅ Xem danh sách loại phòng chính
  - ✅ Tìm kiếm phòng theo tiêu chí
  - ✅ Đặt phòng online
  - ✅ Xem lịch sử booking
  - ✅ Quản lý thông tin cá nhân

---

## 🔧 CẤU HÌNH VÀ TIỆN ÍCH

### 1. **Khởi tạo Dữ liệu (WebsiteDatLichKhachSanApplication)**
- **Mục đích**: Khởi tạo dữ liệu mẫu cho hệ thống
- **Chức năng**:
  - ✅ Tạo dữ liệu Hotel mẫu
  - ✅ Tạo các loại phòng chuẩn
  - ✅ Tạo phòng mẫu với giá
  - ✅ Cấu hình async processing

### 2. **Enums và Converters**
- **Mục đích**: Quản lý trạng thái và chuyển đổi dữ liệu
- **Các enum chính**:
  - TrangThaiDatPhong, TrangThaiThanhToan
  - TrangThaiPhong, TrangThaiHotel
  - LoaiGiaoDich, TrangThaiYeuCau
  - BookingType, CancellationPolicy

---

## 📊 BÁO CÁO VÀ THỐNG KÊ

### 1. **Revenue Analytics**
- **Mục đích**: Phân tích doanh thu
- **Chức năng**:
  - ✅ Thống kê doanh thu theo ngày
  - ✅ Phân tích doanh thu phòng/dịch vụ
  - ✅ Tỷ lệ lấp đầy phòng
  - ✅ Giá phòng trung bình (ADR)
  - ✅ Doanh thu mỗi phòng (RevPAR)

### 2. **Hotel Amenities**
- **Mục đích**: Quản lý tiện ích khách sạn
- **Chức năng**:
  - ✅ Quản lý danh sách tiện ích
  - ✅ Cấu hình giờ hoạt động
  - ✅ Quản lý phí sử dụng

---

## 🎨 GIAO DIỆN NGƯỜI DÙNG

### 1. **Admin Interface**
- Trang quản lý đặt phòng
- Trang quản lý khách hàng
- Trang quản lý phòng
- Trang quản lý dịch vụ
- Trang báo cáo và thống kê

### 2. **Staff Interface**
- Dashboard nhân viên
- Quản lý đặt phòng
- Quản lý phòng
- Xử lý thanh toán
- Quản lý dịch vụ

### 3. **Customer Interface**
- Trang chủ khách sạn
- Trang đặt phòng
- Trang chi tiết phòng
- Lịch sử booking
- Thông tin cá nhân

---

## 🔐 BẢO MẬT VÀ PHÂN QUYỀN

### 1. **Authentication**
- Đăng nhập cho Admin/Staff/Customer
- Quản lý session
- Bảo mật mật khẩu

### 2. **Authorization**
- Phân quyền theo role
- Kiểm soát truy cập chức năng
- Audit log cho các thao tác quan trọng

---

## 📱 TÍNH NĂNG ĐẶC BIỆT

### 1. **Booking Management**
- Hỗ trợ booking theo ngày/giờ/qua đêm
- Tự động tính toán giá
- Quản lý cancellation policy
- Xử lý refund tự động

### 2. **Inventory Management**
- Quản lý kho vật tư
- Theo dõi xuất/nhập kho
- Cảnh báo hết hàng

### 3. **Promotion System**
- Tạo mã giảm giá
- Áp dụng khuyến mãi tự động
- Theo dõi hiệu quả promotion

---

## 🚀 CÔNG NGHỆ SỬ DỤNG

- **Backend**: Spring Boot, JPA/Hibernate
- **Database**: SQL Server
- **Frontend**: Thymeleaf, HTML/CSS/JavaScript
- **Security**: Spring Security
- **Build Tool**: Maven

---

*Tài liệu này cung cấp cái nhìn tổng quan về tất cả chức năng trong hệ thống. Để biết chi tiết implementation, vui lòng tham khảo source code và comments trong từng file.*