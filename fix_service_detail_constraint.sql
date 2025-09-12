-- Fix SERVICE_DETAIL table constraint to allow NULL for dich_vu_id
-- This allows inventory items to be added without requiring a service ID

USE HotelBookingDB_LastStand_11;
GO

-- Check if the column exists and is NOT NULL
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
           WHERE TABLE_NAME = 'SERVICE_DETAIL' 
           AND COLUMN_NAME = 'dich_vu_id' 
           AND IS_NULLABLE = 'NO')
BEGIN
    -- Alter the column to allow NULL values
    ALTER TABLE SERVICE_DETAIL 
    ALTER COLUMN dich_vu_id INT NULL;
    
    PRINT 'Đã thay đổi cột dich_vu_id để cho phép NULL';
END
ELSE
BEGIN
    PRINT 'Cột dich_vu_id đã cho phép NULL hoặc không tồn tại';
END
GO

-- Add comment to explain the change
EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'ID dịch vụ - có thể NULL khi là vật phẩm tồn kho', 
    @level0type = N'SCHEMA', @level0name = N'dbo', 
    @level1type = N'TABLE', @level1name = N'SERVICE_DETAIL', 
    @level2type = N'COLUMN', @level2name = N'dich_vu_id';

PRINT '';
PRINT '=== HOÀN THÀNH SỬA LỖI CONSTRAINT SERVICE_DETAIL ===';
PRINT 'Bây giờ có thể thêm vật phẩm tồn kho vào booking mà không cần dich_vu_id';
PRINT 'Hãy chạy lại ứng dụng Spring Boot.';