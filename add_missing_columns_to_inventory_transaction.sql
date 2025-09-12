USE HotelBookingDB_LastStand_11;
GO

-- Thêm các cột còn thiếu vào bảng INVENTORY_TRANSACTION
PRINT 'Đang thêm các cột còn thiếu vào bảng INVENTORY_TRANSACTION...';

-- Thêm cột trang_thai_duyet
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
               AND COLUMN_NAME = 'trang_thai_duyet')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION 
    ADD trang_thai_duyet NVARCHAR(20) DEFAULT 'CHO_DUYET';
    
    PRINT 'Đã thêm cột trang_thai_duyet vào bảng INVENTORY_TRANSACTION';
END
ELSE
BEGIN
    PRINT 'Cột trang_thai_duyet đã tồn tại trong bảng INVENTORY_TRANSACTION';
END
GO

-- Thêm cột admin_duyet_id
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
               AND COLUMN_NAME = 'admin_duyet_id')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION 
    ADD admin_duyet_id INT;
    
    PRINT 'Đã thêm cột admin_duyet_id vào bảng INVENTORY_TRANSACTION';
END
ELSE
BEGIN
    PRINT 'Cột admin_duyet_id đã tồn tại trong bảng INVENTORY_TRANSACTION';
END
GO

-- Thêm cột ngay_duyet
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
               AND COLUMN_NAME = 'ngay_duyet')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION 
    ADD ngay_duyet DATETIME2;
    
    PRINT 'Đã thêm cột ngay_duyet vào bảng INVENTORY_TRANSACTION';
END
ELSE
BEGIN
    PRINT 'Cột ngay_duyet đã tồn tại trong bảng INVENTORY_TRANSACTION';
END
GO

-- Thêm cột ghi_chu_duyet
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
               AND COLUMN_NAME = 'ghi_chu_duyet')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION 
    ADD ghi_chu_duyet NVARCHAR(500);
    
    PRINT 'Đã thêm cột ghi_chu_duyet vào bảng INVENTORY_TRANSACTION';
END
ELSE
BEGIN
    PRINT 'Cột ghi_chu_duyet đã tồn tại trong bảng INVENTORY_TRANSACTION';
END
GO

PRINT '';
PRINT '=== HOÀN THÀNH THÊM CÁC CỘT CÒN THIẾU VÀO INVENTORY_TRANSACTION ===';
PRINT 'Đã thêm các cột: trang_thai_duyet, admin_duyet_id, ngay_duyet, ghi_chu_duyet';
PRINT 'Hãy chạy lại ứng dụng Spring Boot.';