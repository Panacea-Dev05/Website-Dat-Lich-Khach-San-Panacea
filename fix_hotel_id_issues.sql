-- Script tổng hợp để sửa tất cả vấn đề về hotel_id
-- Chạy script này trong SQL Server Management Studio

USE HotelBookingDB_LastStand_11;
GO

PRINT '=== BẮT ĐẦU SỬA LỖI HOTEL_ID ===';
PRINT '';

-- 1. Kiểm tra và thêm cột hotel_id vào bảng ROOM_TYPE
PRINT '1. Kiểm tra bảng ROOM_TYPE...';
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'ROOM_TYPE' AND COLUMN_NAME = 'hotel_id')
BEGIN
    ALTER TABLE ROOM_TYPE ADD hotel_id INT;
    PRINT '   ✓ Đã thêm cột hotel_id vào bảng ROOM_TYPE';
END
ELSE
    PRINT '   ✓ Cột hotel_id đã tồn tại trong bảng ROOM_TYPE';
GO

-- 2. Kiểm tra và thêm cột hotel_id vào bảng HOTEL_AMENITIES
PRINT '2. Kiểm tra bảng HOTEL_AMENITIES...';
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'HOTEL_AMENITIES' AND COLUMN_NAME = 'hotel_id')
BEGIN
    ALTER TABLE HOTEL_AMENITIES ADD hotel_id INT;
    PRINT '   ✓ Đã thêm cột hotel_id vào bảng HOTEL_AMENITIES';
END
ELSE
    PRINT '   ✓ Cột hotel_id đã tồn tại trong bảng HOTEL_AMENITIES';
GO

-- 3. Cập nhật dữ liệu hotel_id cho ROOM_TYPE
PRINT '3. Cập nhật dữ liệu cho ROOM_TYPE...';
IF EXISTS (SELECT 1 FROM HOTEL)
BEGIN
    DECLARE @first_hotel_id INT;
    SELECT TOP 1 @first_hotel_id = id FROM HOTEL ORDER BY id;
    
    UPDATE ROOM_TYPE 
    SET hotel_id = @first_hotel_id 
    WHERE hotel_id IS NULL;
    
    DECLARE @updated_rooms INT = @@ROWCOUNT;
    PRINT '   ✓ Đã cập nhật hotel_id cho ' + CAST(@updated_rooms AS VARCHAR(10)) + ' RoomType';
END
ELSE
    PRINT '   ⚠ Không tìm thấy Hotel nào trong database';
GO

-- 4. Cập nhật dữ liệu hotel_id cho HOTEL_AMENITIES
PRINT '4. Cập nhật dữ liệu cho HOTEL_AMENITIES...';
IF EXISTS (SELECT 1 FROM HOTEL)
BEGIN
    DECLARE @first_hotel_id INT;
    SELECT TOP 1 @first_hotel_id = id FROM HOTEL ORDER BY id;
    
    UPDATE HOTEL_AMENITIES 
    SET hotel_id = @first_hotel_id 
    WHERE hotel_id IS NULL;
    
    DECLARE @updated_amenities INT = @@ROWCOUNT;
    PRINT '   ✓ Đã cập nhật hotel_id cho ' + CAST(@updated_amenities AS VARCHAR(10)) + ' HotelAmenities';
END
GO

-- 5. Thêm foreign key constraints
PRINT '5. Thêm foreign key constraints...';

-- FK cho ROOM_TYPE
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
               WHERE CONSTRAINT_NAME = 'FK_ROOM_TYPE_HOTEL')
BEGIN
    ALTER TABLE ROOM_TYPE
    ADD CONSTRAINT FK_ROOM_TYPE_HOTEL
    FOREIGN KEY (hotel_id) REFERENCES HOTEL(id);
    PRINT '   ✓ Đã thêm FK_ROOM_TYPE_HOTEL';
END
ELSE
    PRINT '   ✓ FK_ROOM_TYPE_HOTEL đã tồn tại';

-- FK cho HOTEL_AMENITIES
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
               WHERE CONSTRAINT_NAME = 'FK_HOTEL_AMENITIES_HOTEL')
BEGIN
    ALTER TABLE HOTEL_AMENITIES
    ADD CONSTRAINT FK_HOTEL_AMENITIES_HOTEL
    FOREIGN KEY (hotel_id) REFERENCES HOTEL(id);
    PRINT '   ✓ Đã thêm FK_HOTEL_AMENITIES_HOTEL';
END
ELSE
    PRINT '   ✓ FK_HOTEL_AMENITIES_HOTEL đã tồn tại';
GO

-- 6. Đặt cột hotel_id thành NOT NULL
PRINT '6. Đặt cột hotel_id thành NOT NULL...';

-- Cho ROOM_TYPE
IF EXISTS (SELECT 1 FROM HOTEL) AND 
   NOT EXISTS (SELECT 1 FROM ROOM_TYPE WHERE hotel_id IS NULL)
BEGIN
    IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'ROOM_TYPE' AND COLUMN_NAME = 'hotel_id' AND IS_NULLABLE = 'YES')
    BEGIN
        ALTER TABLE ROOM_TYPE ALTER COLUMN hotel_id INT NOT NULL;
        PRINT '   ✓ Đã đặt ROOM_TYPE.hotel_id thành NOT NULL';
    END
    ELSE
        PRINT '   ✓ ROOM_TYPE.hotel_id đã là NOT NULL';
END

-- Cho HOTEL_AMENITIES
IF EXISTS (SELECT 1 FROM HOTEL) AND 
   NOT EXISTS (SELECT 1 FROM HOTEL_AMENITIES WHERE hotel_id IS NULL)
BEGIN
    IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'HOTEL_AMENITIES' AND COLUMN_NAME = 'hotel_id' AND IS_NULLABLE = 'YES')
    BEGIN
        ALTER TABLE HOTEL_AMENITIES ALTER COLUMN hotel_id INT NOT NULL;
        PRINT '   ✓ Đã đặt HOTEL_AMENITIES.hotel_id thành NOT NULL';
    END
    ELSE
        PRINT '   ✓ HOTEL_AMENITIES.hotel_id đã là NOT NULL';
END
GO

-- 7. Kiểm tra kết quả cuối cùng
PRINT '';
PRINT '=== KIỂM TRA KẾT QUẢ ===';

SELECT 
    'ROOM_TYPE' as TableName,
    COUNT(*) as TotalRecords,
    COUNT(hotel_id) as RecordsWithHotelId,
    COUNT(*) - COUNT(hotel_id) as RecordsWithoutHotelId
FROM ROOM_TYPE

UNION ALL

SELECT 
    'HOTEL_AMENITIES' as TableName,
    COUNT(*) as TotalRecords,
    COUNT(hotel_id) as RecordsWithHotelId,
    COUNT(*) - COUNT(hotel_id) as RecordsWithoutHotelId
FROM HOTEL_AMENITIES;

PRINT '';
PRINT '=== HOÀN THÀNH SỬA LỖI HOTEL_ID ===';
PRINT 'Bây giờ bạn có thể chạy lại ứng dụng Spring Boot.';