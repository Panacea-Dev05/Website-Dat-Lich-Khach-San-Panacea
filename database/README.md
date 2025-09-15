# Database Scripts

Thư mục này chứa các script SQL để quản lý database của dự án Hotel Booking.

## Các file script:

### 1. fix_event_calendar_table.sql
- Tạo bảng EVENT_CALENDAR với cột khach_san_id
- Thiết lập foreign key constraints
- Sử dụng khi cần tạo mới bảng sự kiện

### 2. insert_test_payments.sql  
- Chèn dữ liệu test cho bảng PAYMENT
- Sử dụng để test chức năng thanh toán
- Cần có dữ liệu booking trước khi chạy

### 3. update_inventory_prices.sql
- Cập nhật giá bán cho vật phẩm tồn kho
- Tự động tính giá bán = giá nhập + 20%
- Thiết lập giá cho các vật phẩm amenities

## Hướng dẫn sử dụng:
1. Kết nối đến SQL Server
2. Chọn database HotelBookingDB_LastStand_11
3. Chạy script theo thứ tự cần thiết
4. Kiểm tra kết quả sau khi chạy