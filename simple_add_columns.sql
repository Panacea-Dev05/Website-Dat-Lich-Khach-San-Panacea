USE HotelBookingDB_LastStand_11;
GO

PRINT N'=== THÊM CỘT KHACH_SAN_ID ĐƠN GIẢN ==='
GO

-- Thêm cột cho bảng BOOKING
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('BOOKING') AND name = 'khach_san_id')
    BEGIN
        ALTER TABLE BOOKING ADD khach_san_id INT NULL;
        PRINT N'✓ Đã thêm cột khach_san_id vào BOOKING';
    END
    ELSE
        PRINT N'✓ BOOKING đã có cột khach_san_id';
END TRY
BEGIN CATCH
    PRINT N'❌ Lỗi khi thêm cột vào BOOKING: ' + ERROR_MESSAGE();
END CATCH
GO

-- Thêm cột cho bảng ROOM
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('ROOM') AND name = 'khach_san_id')
    BEGIN
        ALTER TABLE ROOM ADD khach_san_id INT NULL;
        PRINT N'✓ Đã thêm cột khach_san_id vào ROOM';
    END
    ELSE
        PRINT N'✓ ROOM đã có cột khach_san_id';
END TRY
BEGIN CATCH
    PRINT N'❌ Lỗi khi thêm cột vào ROOM: ' + ERROR_MESSAGE();
END CATCH
GO

-- Thêm cột cho bảng INVENTORY_MANAGEMENT
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('INVENTORY_MANAGEMENT') AND name = 'khach_san_id')
    BEGIN
        ALTER TABLE INVENTORY_MANAGEMENT ADD khach_san_id INT NULL;
        PRINT N'✓ Đã thêm cột khach_san_id vào INVENTORY_MANAGEMENT';
    END
    ELSE
        PRINT N'✓ INVENTORY_MANAGEMENT đã có cột khach_san_id';
END TRY
BEGIN CATCH
    PRINT N'❌ Lỗi khi thêm cột vào INVENTORY_MANAGEMENT: ' + ERROR_MESSAGE();
END CATCH
GO

-- Thêm cột cho bảng INVENTORY_TRANSACTION
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('INVENTORY_TRANSACTION') AND name = 'khach_san_id')
    BEGIN
        ALTER TABLE INVENTORY_TRANSACTION ADD khach_san_id INT NULL;
        PRINT N'✓ Đã thêm cột khach_san_id vào INVENTORY_TRANSACTION';
    END
    ELSE
        PRINT N'✓ INVENTORY_TRANSACTION đã có cột khach_san_id';
END TRY
BEGIN CATCH
    PRINT N'❌ Lỗi khi thêm cột vào INVENTORY_TRANSACTION: ' + ERROR_MESSAGE();
END CATCH
GO

-- Thêm cột cho bảng EVENT_CALENDAR
BEGIN TRY
    IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('EVENT_CALENDAR') AND name = 'khach_san_id')
    BEGIN
        ALTER TABLE EVENT_CALENDAR ADD khach_san_id INT NULL;
        PRINT N'✓ Đã thêm cột khach_san_id vào EVENT_CALENDAR';
    END
    ELSE
        PRINT N'✓ EVENT_CALENDAR đã có cột khach_san_id';
END TRY
BEGIN CATCH
    PRINT N'❌ Lỗi khi thêm cột vào EVENT_CALENDAR: ' + ERROR_MESSAGE();
END CATCH
GO

PRINT N'=== CẬP NHẬT GIÁ TRỊ MẶC ĐỊNH ==='
GO

-- Cập nhật giá trị mặc định cho tất cả các bảng
UPDATE BOOKING SET khach_san_id = 1 WHERE khach_san_id IS NULL;
UPDATE ROOM SET khach_san_id = 1 WHERE khach_san_id IS NULL;
UPDATE INVENTORY_MANAGEMENT SET khach_san_id = 1 WHERE khach_san_id IS NULL;
UPDATE INVENTORY_TRANSACTION SET khach_san_id = 1 WHERE khach_san_id IS NULL;
UPDATE EVENT_CALENDAR SET khach_san_id = 1 WHERE khach_san_id IS NULL;

PRINT N'✓ Đã cập nhật giá trị mặc định cho tất cả các bảng';
GO

PRINT N'=== KIỂM TRA KẾT QUẢ ==='
GO

-- Kiểm tra các cột đã được tạo
SELECT 
    t.name AS 'Bảng',
    CASE WHEN c.name IS NOT NULL THEN 'CÓ' ELSE 'KHÔNG' END AS 'Có cột khach_san_id'
FROM (
    SELECT 'BOOKING' as name
    UNION SELECT 'ROOM'
    UNION SELECT 'INVENTORY_MANAGEMENT'
    UNION SELECT 'INVENTORY_TRANSACTION'
    UNION SELECT 'EVENT_CALENDAR'
) t
LEFT JOIN sys.tables st ON t.name = st.name
LEFT JOIN sys.columns c ON st.object_id = c.object_id AND c.name = 'khach_san_id'
ORDER BY t.name;
GO

PRINT N'=== HOÀN THÀNH ==='
PRINT N'Chạy script này trước, sau đó chạy lại ứng dụng để kiểm tra.';
GO