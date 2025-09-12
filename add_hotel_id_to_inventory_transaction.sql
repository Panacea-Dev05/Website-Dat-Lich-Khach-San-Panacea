USE HotelBookingDB_LastStand_11;
GO

-- Thêm cột khach_san_id vào bảng INVENTORY_TRANSACTION
PRINT 'Đang thêm cột khach_san_id vào bảng INVENTORY_TRANSACTION...';

IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
               AND COLUMN_NAME = 'khach_san_id')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION 
    ADD khach_san_id INT NOT NULL DEFAULT 1;
    
    PRINT 'Đã thêm cột khach_san_id vào bảng INVENTORY_TRANSACTION';
END
ELSE
BEGIN
    PRINT 'Cột khach_san_id đã tồn tại trong bảng INVENTORY_TRANSACTION';
END
GO

-- Thêm foreign key constraint
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
               WHERE CONSTRAINT_NAME = 'FK_INVENTORY_TRANSACTION_HOTEL')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION
    ADD CONSTRAINT FK_INVENTORY_TRANSACTION_HOTEL
    FOREIGN KEY (khach_san_id) REFERENCES HOTEL(id);
    
    PRINT 'Đã thêm foreign key constraint cho khach_san_id';
END
ELSE
BEGIN
    PRINT 'Foreign key constraint cho khach_san_id đã tồn tại';
END
GO

-- Cập nhật dữ liệu hiện có (nếu có)
UPDATE INVENTORY_TRANSACTION 
SET khach_san_id = 1 
WHERE khach_san_id IS NULL OR khach_san_id = 0;

PRINT '';
PRINT '=== HOÀN THÀNH THÊM KHACH_SAN_ID VÀO INVENTORY_TRANSACTION ===';
PRINT 'Đã thêm cột khach_san_id và foreign key constraint';
PRINT 'Hãy chạy lại ứng dụng Spring Boot.';