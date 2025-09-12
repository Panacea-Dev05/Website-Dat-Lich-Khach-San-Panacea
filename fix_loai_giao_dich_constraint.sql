USE HotelBookingDB_LastStand_11;
GO

-- Script để sửa CHECK constraint cho cột loai_giao_dich
PRINT '=== FIXING LOAI_GIAO_DICH CHECK CONSTRAINT ===';

-- Xóa CHECK constraint cũ nếu tồn tại
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
           WHERE CONSTRAINT_NAME = 'CK__INVENTORY__loai___4A4E069C')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION
    DROP CONSTRAINT CK__INVENTORY__loai___4A4E069C;
    
    PRINT '✓ Dropped old CHECK constraint CK__INVENTORY__loai___4A4E069C';
END
GO

-- Xóa tất cả CHECK constraints khác cho loai_giao_dich nếu có
DECLARE @sql NVARCHAR(MAX) = '';
SELECT @sql = @sql + 'ALTER TABLE INVENTORY_TRANSACTION DROP CONSTRAINT ' + CONSTRAINT_NAME + ';' + CHAR(13)
FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
  AND CONSTRAINT_TYPE = 'CHECK'
  AND CONSTRAINT_NAME LIKE '%loai%';

IF @sql != ''
BEGIN
    EXEC sp_executesql @sql;
    PRINT '✓ Dropped other loai_giao_dich CHECK constraints';
END
GO

-- Thêm CHECK constraint mới với các giá trị đúng từ enum
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
               WHERE CONSTRAINT_NAME = 'CHK_INVENTORY_TRANSACTION_LOAI_GIAO_DICH')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION
    ADD CONSTRAINT CHK_INVENTORY_TRANSACTION_LOAI_GIAO_DICH
    CHECK (loai_giao_dich IN ('NHAP_KHO', 'XUAT_KHO', 'DIEU_CHINH', 'KIEM_KE', 'HUY_BO'));
    
    PRINT '✓ Added new CHECK constraint for loai_giao_dich with correct enum values';
END
ELSE
BEGIN
    PRINT '✓ CHECK constraint for loai_giao_dich already exists';
END
GO

-- Cập nhật dữ liệu hiện có nếu có giá trị không hợp lệ
UPDATE INVENTORY_TRANSACTION 
SET loai_giao_dich = 'NHAP_KHO'
WHERE loai_giao_dich NOT IN ('NHAP_KHO', 'XUAT_KHO', 'DIEU_CHINH', 'KIEM_KE', 'HUY_BO')
   OR loai_giao_dich IS NULL;

PRINT '✓ Updated existing data with valid loai_giao_dich values';

-- Hiển thị thống kê dữ liệu
SELECT 
    loai_giao_dich,
    COUNT(*) as so_luong
FROM INVENTORY_TRANSACTION 
GROUP BY loai_giao_dich
ORDER BY loai_giao_dich;

PRINT '';
PRINT '=== LOAI_GIAO_DICH CONSTRAINT FIX COMPLETED ===';
PRINT 'CHECK constraint đã được cập nhật với các giá trị enum đúng:';
PRINT '- NHAP_KHO (Nhập kho)';
PRINT '- XUAT_KHO (Xuất kho)';
PRINT '- DIEU_CHINH (Điều chỉnh)';
PRINT '- KIEM_KE (Kiểm kê)';
PRINT '- HUY_BO (Hủy bỏ)';
