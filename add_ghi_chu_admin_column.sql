-- Script để thêm cột ghi_chu_admin vào bảng SUPPLY_REQUEST
-- Chạy script này trong SQL Server Management Studio hoặc công cụ quản lý database

USE HotelBookingDB_LastStand_11;
GO

-- Kiểm tra xem cột ghi_chu_admin đã tồn tại chưa
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'SUPPLY_REQUEST' 
               AND COLUMN_NAME = 'ghi_chu_admin')
BEGIN
    -- Thêm cột ghi_chu_admin
    ALTER TABLE SUPPLY_REQUEST 
    ADD ghi_chu_admin NVARCHAR(500);
    
    PRINT N'Đã thêm cột ghi_chu_admin vào bảng SUPPLY_REQUEST thành công';
END
ELSE
BEGIN
    PRINT N'Cột ghi_chu_admin đã tồn tại trong bảng SUPPLY_REQUEST';
END
GO

-- Kiểm tra lại cấu trúc bảng
SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'SUPPLY_REQUEST' 
ORDER BY ORDINAL_POSITION;
GO
