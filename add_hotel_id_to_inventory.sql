-- Script để thêm cột khach_san_id vào bảng INVENTORY_MANAGEMENT
-- Chạy script này trong SQL Server Management Studio

USE HotelBookingDB_LastStand_11;
GO

-- Kiểm tra xem cột khach_san_id đã tồn tại chưa trong bảng INVENTORY_MANAGEMENT
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'INVENTORY_MANAGEMENT' AND COLUMN_NAME = 'khach_san_id')
BEGIN
    -- Thêm cột khach_san_id vào bảng INVENTORY_MANAGEMENT
    ALTER TABLE INVENTORY_MANAGEMENT 
    ADD khach_san_id INT;
    
    PRINT 'Đã thêm cột khach_san_id vào bảng INVENTORY_MANAGEMENT';
END
ELSE
BEGIN
    PRINT 'Cột khach_san_id đã tồn tại trong bảng INVENTORY_MANAGEMENT';
END
GO

-- Cập nhật dữ liệu khach_san_id cho các bản ghi hiện tại
-- Lấy hotel_id đầu tiên từ bảng HOTEL
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
           WHERE TABLE_NAME = 'INVENTORY_MANAGEMENT' AND COLUMN_NAME = 'khach_san_id')
BEGIN
    DECLARE @first_hotel_id INT;
    SELECT TOP 1 @first_hotel_id = id FROM HOTEL ORDER BY id;
    
    -- Cập nhật tất cả bản ghi chưa có khach_san_id
    UPDATE INVENTORY_MANAGEMENT 
    SET khach_san_id = @first_hotel_id
    WHERE khach_san_id IS NULL;
    
    PRINT 'Đã cập nhật khach_san_id cho tất cả InventoryManagement';
END
GO

-- Tạo foreign key constraint
IF NOT EXISTS (SELECT * FROM sys.foreign_keys 
               WHERE name = 'FK_INVENTORY_MANAGEMENT_HOTEL')
BEGIN
    ALTER TABLE INVENTORY_MANAGEMENT
    ADD CONSTRAINT FK_INVENTORY_MANAGEMENT_HOTEL
    FOREIGN KEY (khach_san_id) REFERENCES HOTEL(id);
    
    PRINT 'Đã tạo foreign key constraint cho khach_san_id';
END
ELSE
BEGIN
    PRINT 'Foreign key constraint đã tồn tại';
END
GO

-- Đặt cột khach_san_id thành NOT NULL (sau khi đã cập nhật dữ liệu)
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
           WHERE TABLE_NAME = 'INVENTORY_MANAGEMENT' AND COLUMN_NAME = 'khach_san_id' AND IS_NULLABLE = 'YES')
   AND NOT EXISTS (SELECT 1 FROM INVENTORY_MANAGEMENT WHERE khach_san_id IS NULL)
BEGIN
    ALTER TABLE INVENTORY_MANAGEMENT 
    ALTER COLUMN khach_san_id INT NOT NULL;
    
    PRINT 'Đã đặt cột khach_san_id thành NOT NULL';
END
ELSE
BEGIN
    PRINT 'Cột khach_san_id đã là NOT NULL hoặc vẫn có giá trị NULL';
END
GO

PRINT '';
PRINT '=== HOÀN THÀNH THÊM KHACH_SAN_ID VÀO INVENTORY_MANAGEMENT ===';
PRINT 'Bây giờ bạn có thể chạy lại ứng dụng Spring Boot.';
PRINT 'Bảng INVENTORY_MANAGEMENT đã có cột khach_san_id với foreign key constraint.';