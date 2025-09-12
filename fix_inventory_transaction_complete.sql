USE HotelBookingDB_LastStand_11;
GO

-- Script tổng hợp để sửa tất cả vấn đề với INVENTORY_TRANSACTION
PRINT '=== COMPLETE INVENTORY_TRANSACTION FIX ===';

-- 1. Xóa tất cả CHECK constraints cũ cho loai_giao_dich
PRINT '1. Removing old CHECK constraints...';

DECLARE @sql NVARCHAR(MAX) = '';
SELECT @sql = @sql + 'ALTER TABLE INVENTORY_TRANSACTION DROP CONSTRAINT ' + CONSTRAINT_NAME + ';' + CHAR(13)
FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
  AND CONSTRAINT_TYPE = 'CHECK'
  AND (CONSTRAINT_NAME LIKE '%loai%' OR CONSTRAINT_NAME LIKE '%CK__INVENTORY%');

IF @sql != ''
BEGIN
    EXEC sp_executesql @sql;
    PRINT '✓ Dropped old CHECK constraints';
END
ELSE
BEGIN
    PRINT '✓ No old CHECK constraints found';
END
GO

-- 2. Thêm các cột còn thiếu (nếu chưa có)
PRINT '2. Adding missing columns...';

-- Add trang_thai_duyet column
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
               AND COLUMN_NAME = 'trang_thai_duyet')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION 
    ADD trang_thai_duyet NVARCHAR(20) DEFAULT 'CHO_DUYET';
    PRINT '✓ Added trang_thai_duyet column';
END

-- Add admin_duyet_id column
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
               AND COLUMN_NAME = 'admin_duyet_id')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION 
    ADD admin_duyet_id INT;
    PRINT '✓ Added admin_duyet_id column';
END

-- Add ngay_duyet column
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
               AND COLUMN_NAME = 'ngay_duyet')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION 
    ADD ngay_duyet DATETIME2;
    PRINT '✓ Added ngay_duyet column';
END

-- Add ghi_chu_duyet column
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
               AND COLUMN_NAME = 'ghi_chu_duyet')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION 
    ADD ghi_chu_duyet NVARCHAR(500);
    PRINT '✓ Added ghi_chu_duyet column';
END

-- Add khach_san_id column
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
               AND COLUMN_NAME = 'khach_san_id')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION 
    ADD khach_san_id INT NOT NULL DEFAULT 1;
    PRINT '✓ Added khach_san_id column';
END
GO

-- 3. Cập nhật dữ liệu hiện có
PRINT '3. Updating existing data...';

-- Cập nhật khach_san_id cho các record NULL
UPDATE INVENTORY_TRANSACTION 
SET khach_san_id = 1 
WHERE khach_san_id IS NULL;

-- Cập nhật loai_giao_dich cho các giá trị không hợp lệ
UPDATE INVENTORY_TRANSACTION 
SET loai_giao_dich = 'NHAP_KHO'
WHERE loai_giao_dich NOT IN ('NHAP_KHO', 'XUAT_KHO', 'DIEU_CHINH', 'KIEM_KE', 'HUY_BO')
   OR loai_giao_dich IS NULL;

-- Cập nhật trang_thai_duyet cho các record NULL
UPDATE INVENTORY_TRANSACTION 
SET trang_thai_duyet = 'CHO_DUYET'
WHERE trang_thai_duyet IS NULL;

PRINT '✓ Updated existing data';
GO

-- 4. Thêm CHECK constraints mới
PRINT '4. Adding new CHECK constraints...';

-- CHECK constraint cho loai_giao_dich
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
               WHERE CONSTRAINT_NAME = 'CHK_INVENTORY_TRANSACTION_LOAI_GIAO_DICH')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION
    ADD CONSTRAINT CHK_INVENTORY_TRANSACTION_LOAI_GIAO_DICH
    CHECK (loai_giao_dich IN ('NHAP_KHO', 'XUAT_KHO', 'DIEU_CHINH', 'KIEM_KE', 'HUY_BO'));
    PRINT '✓ Added CHECK constraint for loai_giao_dich';
END

-- CHECK constraint cho trang_thai_duyet
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
               WHERE CONSTRAINT_NAME = 'CHK_INVENTORY_TRANSACTION_TRANG_THAI_DUYET')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION
    ADD CONSTRAINT CHK_INVENTORY_TRANSACTION_TRANG_THAI_DUYET
    CHECK (trang_thai_duyet IN ('CHO_DUYET', 'DA_DUYET', 'TU_CHOI'));
    PRINT '✓ Added CHECK constraint for trang_thai_duyet';
END
GO

-- 5. Thêm foreign key constraints
PRINT '5. Adding foreign key constraints...';

-- Foreign key cho khach_san_id
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
               WHERE CONSTRAINT_NAME = 'FK_INVENTORY_TRANSACTION_HOTEL')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION
    ADD CONSTRAINT FK_INVENTORY_TRANSACTION_HOTEL
    FOREIGN KEY (khach_san_id) REFERENCES HOTEL(id);
    PRINT '✓ Added foreign key constraint for khach_san_id';
END

-- Foreign key cho vat_pham_id (nếu chưa có)
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
               WHERE CONSTRAINT_NAME = 'FK_INVENTORY_TRANSACTION_INVENTORY')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION
    ADD CONSTRAINT FK_INVENTORY_TRANSACTION_INVENTORY
    FOREIGN KEY (vat_pham_id) REFERENCES INVENTORY_MANAGEMENT(id);
    PRINT '✓ Added foreign key constraint for vat_pham_id';
END
GO

-- 6. Hiển thị thống kê dữ liệu
PRINT '6. Data statistics:';

SELECT 
    'loai_giao_dich' as column_name,
    loai_giao_dich as value,
    COUNT(*) as count
FROM INVENTORY_TRANSACTION 
GROUP BY loai_giao_dich
UNION ALL
SELECT 
    'trang_thai_duyet' as column_name,
    trang_thai_duyet as value,
    COUNT(*) as count
FROM INVENTORY_TRANSACTION 
GROUP BY trang_thai_duyet
ORDER BY column_name, value;

PRINT '';
PRINT '=== INVENTORY_TRANSACTION FIX COMPLETED ===';
PRINT 'Tất cả các vấn đề đã được khắc phục:';
PRINT '✓ Các cột còn thiếu đã được thêm';
PRINT '✓ CHECK constraints đã được cập nhật với giá trị đúng';
PRINT '✓ Dữ liệu hiện có đã được cập nhật';
PRINT '✓ Foreign key constraints đã được thêm';
PRINT '';
PRINT 'Bây giờ bạn có thể khởi động lại ứng dụng Spring Boot.';
