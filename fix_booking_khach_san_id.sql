USE HotelBookingDB_LastStand_11;
GO

-- Kiểm tra và thêm cột khach_san_id vào bảng BOOKING
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'BOOKING' 
               AND COLUMN_NAME = 'khach_san_id')
BEGIN
    PRINT N'Thêm cột khach_san_id vào bảng BOOKING...';
    
    -- Đầu tiên thêm cột cho phép NULL
    ALTER TABLE BOOKING ADD khach_san_id INT NULL;
    
    -- Cập nhật giá trị cho tất cả các record hiện có
    UPDATE BOOKING SET khach_san_id = 1;
    
    -- Sau đó thay đổi cột thành NOT NULL
    ALTER TABLE BOOKING ALTER COLUMN khach_san_id INT NOT NULL;
    
    PRINT N'Đã thêm cột khach_san_id vào bảng BOOKING thành công';
END
ELSE
BEGIN
    PRINT N'Cột khach_san_id đã tồn tại trong bảng BOOKING';
    
    -- Đảm bảo tất cả record có giá trị khach_san_id
    UPDATE BOOKING SET khach_san_id = 1 WHERE khach_san_id IS NULL;
END
GO

-- Kiểm tra và thêm foreign key constraint
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
               WHERE CONSTRAINT_NAME = 'FK_BOOKING_HOTEL'
               AND TABLE_NAME = 'BOOKING')
BEGIN
    PRINT N'Thêm foreign key constraint cho khach_san_id...';
    
    -- Thêm foreign key constraint
    ALTER TABLE BOOKING
    ADD CONSTRAINT FK_BOOKING_HOTEL
    FOREIGN KEY (khach_san_id) REFERENCES HOTEL(id);
    
    PRINT N'Đã thêm foreign key constraint FK_BOOKING_HOTEL thành công';
END
ELSE
BEGIN
    PRINT N'Foreign key constraint FK_BOOKING_HOTEL đã tồn tại';
END
GO

-- Hiển thị cấu trúc bảng BOOKING sau khi cập nhật
PRINT N'=== CẤU TRÚC BẢNG BOOKING SAU KHI CẬP NHẬT ===';
SELECT 
    COLUMN_NAME as 'Tên cột',
    DATA_TYPE as 'Kiểu dữ liệu',
    CHARACTER_MAXIMUM_LENGTH as 'Độ dài tối đa',
    IS_NULLABLE as 'Cho phép NULL',
    COLUMN_DEFAULT as 'Giá trị mặc định'
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'BOOKING' 
ORDER BY ORDINAL_POSITION;
GO

-- Hiển thị các foreign key constraints của bảng BOOKING
PRINT N'=== FOREIGN KEY CONSTRAINTS CỦA BẢNG BOOKING ===';
SELECT 
    tc.CONSTRAINT_NAME as 'Tên constraint',
    kcu.COLUMN_NAME as 'Cột',
    ccu.TABLE_NAME as 'Bảng tham chiếu',
    ccu.COLUMN_NAME as 'Cột tham chiếu'
FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc
JOIN INFORMATION_SCHEMA.KEY_COLUMN_USAGE kcu 
    ON tc.CONSTRAINT_NAME = kcu.CONSTRAINT_NAME
JOIN INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE ccu 
    ON tc.CONSTRAINT_NAME = ccu.CONSTRAINT_NAME
WHERE tc.TABLE_NAME = 'BOOKING' 
    AND tc.CONSTRAINT_TYPE = 'FOREIGN KEY';
GO

PRINT N'=== HOÀN THÀNH SỬA LỖI BẢNG BOOKING ===';
PRINT N'Cột khach_san_id đã được thêm vào bảng BOOKING với foreign key constraint.';
PRINT N'Bây giờ bạn có thể chạy lại ứng dụng Spring Boot.';