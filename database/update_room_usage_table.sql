-- Cập nhật bảng ROOM_USAGE để thêm thông tin booking
-- Chạy script này để thêm các cột mới vào bảng ROOM_USAGE

-- Thêm các cột mới cho thông tin booking
ALTER TABLE ROOM_USAGE 
ADD booking_id INT,
ADD ma_dat_phong NVARCHAR(20),
ADD ten_khach_hang NVARCHAR(100),
ADD so_dien_thoai NVARCHAR(15);

-- Thêm foreign key constraint cho booking_id
ALTER TABLE ROOM_USAGE 
ADD CONSTRAINT FK_ROOM_USAGE_BOOKING 
FOREIGN KEY (booking_id) REFERENCES BOOKING(id);

-- Thêm index để tối ưu hóa truy vấn
CREATE INDEX IX_ROOM_USAGE_BOOKING_ID ON ROOM_USAGE(booking_id);
CREATE INDEX IX_ROOM_USAGE_MA_DAT_PHONG ON ROOM_USAGE(ma_dat_phong);
CREATE INDEX IX_ROOM_USAGE_TEN_KHACH_HANG ON ROOM_USAGE(ten_khach_hang);

-- Cập nhật dữ liệu mẫu (nếu cần)
-- UPDATE ROOM_USAGE SET ma_dat_phong = 'BK' + CAST(id AS VARCHAR(10)) WHERE ma_dat_phong IS NULL;
-- UPDATE ROOM_USAGE SET ten_khach_hang = 'Khách hàng ' + CAST(id AS VARCHAR(10)) WHERE ten_khach_hang IS NULL;

PRINT 'Đã cập nhật bảng ROOM_USAGE thành công!';
