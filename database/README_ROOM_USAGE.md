# Quản lý Lịch sử Sử dụng Đồ của Phòng

## Tổng quan

Đã cập nhật hệ thống quản lý kho để thay thế phần "Giao dịch kho" bằng "Lịch sử sử dụng đồ của phòng". Điều này giúp theo dõi việc sử dụng các vật phẩm trong từng phòng cụ thể.

## Các thay đổi chính

### 1. Entity mới

- **RoomUsage.java**: Entity để lưu trữ lịch sử sử dụng đồ của phòng
  - `soPhong`: Số phòng
  - `tenVatPham`: Tên vật phẩm được sử dụng
  - `soLuongSuDung`: Số lượng sử dụng
  - `ngaySuDung`: Thời gian sử dụng
  - `loaiSuDung`: Loại sử dụng (Amenities, Cleaning, Maintenance, Food)
  - `nhanVienGhiNhan`: Nhân viên ghi nhận

### 2. Repository mới

- **RoomUsageRepository.java**: Repository với các query tối ưu
  - Tìm kiếm theo số phòng
  - Tìm kiếm theo khoảng thời gian
  - Thống kê sử dụng theo phòng
  - Tìm kiếm theo tên vật phẩm

### 3. Service cập nhật

- **QuanLyKhoService.java**: Thêm các phương thức quản lý lịch sử sử dụng
  - `getRoomUsageHistory()`: Lấy lịch sử theo phòng
  - `recordRoomUsage()`: Ghi nhận sử dụng mới
  - `getRoomUsageStatistics()`: Thống kê sử dụng

### 4. Controller cập nhật

- **QuanLyKhoController.java**: Thêm các endpoint API
  - `GET /room-usage`: Hiển thị trang lịch sử sử dụng
  - `POST /api/room-usage`: Ghi nhận sử dụng mới
  - `GET /api/room-usage/{soPhong}`: Lấy lịch sử theo phòng
  - `GET /api/room-usage/search`: Tìm kiếm theo tên vật phẩm

### 5. Template cập nhật

- **QuanLyKho.html**: Thay đổi giao diện
  - Loại bỏ form "Giao dịch kho"
  - Thêm form "Ghi nhận sử dụng đồ của phòng"
  - Thay thế bảng "Lịch sử giao dịch kho" bằng "Lịch sử sử dụng đồ của phòng"

### 6. JavaScript mới

- **room-usage.js**: Xử lý form và tìm kiếm
  - Gửi form ghi nhận sử dụng
  - Tìm kiếm theo tên vật phẩm hoặc số phòng
  - Cập nhật bảng dữ liệu động

## Cách sử dụng

### 1. Ghi nhận sử dụng đồ

1. Truy cập trang quản lý kho: `/nhanvien/quanlykho`
2. Điền form "Ghi nhận sử dụng đồ của phòng":
   - Số phòng (ví dụ: 101, 102)
   - Chọn vật phẩm từ danh sách
   - Nhập số lượng sử dụng
   - Chọn loại sử dụng
   - Nhập ngày giờ sử dụng
   - Thêm ghi chú (tùy chọn)
3. Nhấn "Ghi nhận sử dụng"

### 2. Xem lịch sử sử dụng

- Bảng "Lịch sử sử dụng đồ của phòng" hiển thị tất cả các ghi nhận
- Có thể tìm kiếm theo:
  - Tên vật phẩm
  - Số phòng
- Dữ liệu được sắp xếp theo thời gian sử dụng (mới nhất trước)

### 3. Thống kê

- Có thể xem thống kê sử dụng theo phòng
- Thống kê theo loại vật phẩm
- Báo cáo theo khoảng thời gian

## Ví dụ dữ liệu

```
Phòng 101 đã sử dụng:
- 2 chai nước suối (Amenities)
- 1 gói snack (Food)
- 2 cái khăn tắm (Amenities)

Phòng 102 đã sử dụng:
- 1 chai nước rửa chén (Cleaning)
- 1 cái bóng đèn (Maintenance)
```

## Lợi ích

1. **Theo dõi chi tiết**: Biết được phòng nào sử dụng vật phẩm gì, khi nào
2. **Quản lý kho hiệu quả**: Dự đoán nhu cầu sử dụng theo phòng
3. **Báo cáo chính xác**: Có dữ liệu để tạo báo cáo sử dụng
4. **Trách nhiệm rõ ràng**: Biết nhân viên nào ghi nhận việc sử dụng

## Cài đặt

1. Chạy script SQL: `database/create_room_usage_table.sql`
2. Restart ứng dụng Spring Boot
3. Truy cập trang quản lý kho để sử dụng tính năng mới
