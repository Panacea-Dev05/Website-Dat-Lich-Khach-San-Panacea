USE HotelBookingDB_LastStand_11;
GO

PRINT N'=== KIỂM TRA VÀ THÊM CỘT KHACH_SAN_ID NHANH ==='
GO

-- Kiểm tra bảng BOOKING
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('BOOKING') AND name = 'khach_san_id')
BEGIN
    PRINT N'Thêm cột khach_san_id vào bảng BOOKING...'
    ALTER TABLE BOOKING ADD khach_san_id INT NULL
    UPDATE BOOKING SET khach_san_id = 1
    ALTER TABLE BOOKING ALTER COLUMN khach_san_id INT NOT NULL
    PRINT N'✓ Hoàn thành BOOKING'
END
ELSE
    PRINT N'✓ BOOKING đã có cột khach_san_id'
GO

-- Kiểm tra bảng ROOM
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('ROOM') AND name = 'khach_san_id')
BEGIN
    PRINT N'Thêm cột khach_san_id vào bảng ROOM...'
    ALTER TABLE ROOM ADD khach_san_id INT NULL
    UPDATE ROOM SET khach_san_id = 1
    ALTER TABLE ROOM ALTER COLUMN khach_san_id INT NOT NULL
    PRINT N'✓ Hoàn thành ROOM'
END
ELSE
    PRINT N'✓ ROOM đã có cột khach_san_id'
GO

-- Kiểm tra bảng INVENTORY_MANAGEMENT
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('INVENTORY_MANAGEMENT') AND name = 'khach_san_id')
BEGIN
    PRINT N'Thêm cột khach_san_id vào bảng INVENTORY_MANAGEMENT...'
    ALTER TABLE INVENTORY_MANAGEMENT ADD khach_san_id INT NULL
    UPDATE INVENTORY_MANAGEMENT SET khach_san_id = 1
    ALTER TABLE INVENTORY_MANAGEMENT ALTER COLUMN khach_san_id INT NOT NULL
    PRINT N'✓ Hoàn thành INVENTORY_MANAGEMENT'
END
ELSE
    PRINT N'✓ INVENTORY_MANAGEMENT đã có cột khach_san_id'
GO

-- Kiểm tra bảng INVENTORY_TRANSACTION
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('INVENTORY_TRANSACTION') AND name = 'khach_san_id')
BEGIN
    PRINT N'Thêm cột khach_san_id vào bảng INVENTORY_TRANSACTION...'
    ALTER TABLE INVENTORY_TRANSACTION ADD khach_san_id INT NULL
    UPDATE INVENTORY_TRANSACTION SET khach_san_id = 1
    ALTER TABLE INVENTORY_TRANSACTION ALTER COLUMN khach_san_id INT NOT NULL
    PRINT N'✓ Hoàn thành INVENTORY_TRANSACTION'
END
ELSE
    PRINT N'✓ INVENTORY_TRANSACTION đã có cột khach_san_id'
GO

-- Kiểm tra bảng EVENT_CALENDAR
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('EVENT_CALENDAR') AND name = 'khach_san_id')
BEGIN
    PRINT N'Thêm cột khach_san_id vào bảng EVENT_CALENDAR...'
    ALTER TABLE EVENT_CALENDAR ADD khach_san_id INT NULL
    UPDATE EVENT_CALENDAR SET khach_san_id = 1
    ALTER TABLE EVENT_CALENDAR ALTER COLUMN khach_san_id INT NOT NULL
    PRINT N'✓ Hoàn thành EVENT_CALENDAR'
END
ELSE
    PRINT N'✓ EVENT_CALENDAR đã có cột khach_san_id'
GO

PRINT N'=== THÊM FOREIGN KEY (NẾU CẦN) ==='
GO

-- Thêm FK cho BOOKING
IF NOT EXISTS (SELECT * FROM sys.foreign_keys WHERE name = 'FK_BOOKING_HOTEL')
BEGIN
    ALTER TABLE BOOKING ADD CONSTRAINT FK_BOOKING_HOTEL FOREIGN KEY (khach_san_id) REFERENCES HOTEL(id)
    PRINT N'✓ Đã thêm FK_BOOKING_HOTEL'
END
ELSE
    PRINT N'✓ FK_BOOKING_HOTEL đã tồn tại'
GO

PRINT N'=== HOÀN THÀNH ==='
PRINT N'Chạy lại ứng dụng Spring Boot để kiểm tra.'
GO