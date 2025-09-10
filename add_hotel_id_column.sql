-- Script để thêm cột hotel_id vào bảng ROOM_TYPE
-- Chạy script này trong SQL Server Management Studio hoặc công cụ quản lý database

USE HotelBookingDB_LastStand_11;
GO

-- Kiểm tra xem cột hotel_id đã tồn tại chưa
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'ROOM_TYPE' AND COLUMN_NAME = 'hotel_id')
BEGIN
    -- Thêm cột hotel_id vào bảng ROOM_TYPE
    ALTER TABLE ROOM_TYPE 
    ADD hotel_id INT;
    
    PRINT 'Đã thêm cột hotel_id vào bảng ROOM_TYPE';
END
ELSE
BEGIN
    PRINT 'Cột hotel_id đã tồn tại trong bảng ROOM_TYPE';
END
GO

-- Kiểm tra xem có dữ liệu trong bảng HOTEL không
IF EXISTS (SELECT 1 FROM HOTEL)
BEGIN
    -- Cập nhật tất cả RoomType để tham chiếu đến Hotel đầu tiên
    -- (vì đây là single hotel model)
    DECLARE @first_hotel_id INT;
    SELECT TOP 1 @first_hotel_id = id FROM HOTEL ORDER BY id;
    
    UPDATE ROOM_TYPE 
    SET hotel_id = @first_hotel_id 
    WHERE hotel_id IS NULL;
    
    PRINT 'Đã cập nhật hotel_id cho tất cả RoomType';
END
GO

-- Thêm foreign key constraint
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
               WHERE CONSTRAINT_NAME = 'FK_ROOM_TYPE_HOTEL')
BEGIN
    ALTER TABLE ROOM_TYPE
    ADD CONSTRAINT FK_ROOM_TYPE_HOTEL
    FOREIGN KEY (hotel_id) REFERENCES HOTEL(id);
    
    PRINT 'Đã thêm foreign key constraint FK_ROOM_TYPE_HOTEL';
END
ELSE
BEGIN
    PRINT 'Foreign key constraint FK_ROOM_TYPE_HOTEL đã tồn tại';
END
GO

-- Đặt cột hotel_id thành NOT NULL (sau khi đã cập nhật dữ liệu)
IF EXISTS (SELECT 1 FROM HOTEL) AND 
   NOT EXISTS (SELECT 1 FROM ROOM_TYPE WHERE hotel_id IS NULL)
BEGIN
    ALTER TABLE ROOM_TYPE
    ALTER COLUMN hotel_id INT NOT NULL;
    
    PRINT 'Đã đặt cột hotel_id thành NOT NULL';
END
GO

PRINT 'Hoàn thành cập nhật schema cho bảng ROOM_TYPE';