-- Script để sửa lỗi bảng SUPPLY_REQUEST - thêm cột ghi_chu_admin nếu chưa có
-- Chạy script này trong SQL Server Management Studio hoặc công cụ quản lý database

USE HotelBookingDB_LastStand_11;
GO

-- Kiểm tra xem bảng SUPPLY_REQUEST có tồn tại không
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'SUPPLY_REQUEST')
BEGIN
    PRINT N'Bảng SUPPLY_REQUEST đã tồn tại.';
    
    -- Kiểm tra xem cột ghi_chu_admin có tồn tại không
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_NAME = 'SUPPLY_REQUEST' 
                   AND COLUMN_NAME = 'ghi_chu_admin')
    BEGIN
        PRINT N'Cột ghi_chu_admin chưa tồn tại. Đang thêm...';
        
        -- Thêm cột ghi_chu_admin
        ALTER TABLE SUPPLY_REQUEST 
        ADD ghi_chu_admin NVARCHAR(500);
        
        PRINT N'Đã thêm cột ghi_chu_admin vào bảng SUPPLY_REQUEST thành công';
    END
    ELSE
    BEGIN
        PRINT N'Cột ghi_chu_admin đã tồn tại trong bảng SUPPLY_REQUEST';
    END
    
    -- Kiểm tra xem cột muc_do_uu_tien có tồn tại không
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_NAME = 'SUPPLY_REQUEST' 
                   AND COLUMN_NAME = 'muc_do_uu_tien')
    BEGIN
        PRINT N'Cột muc_do_uu_tien chưa tồn tại. Đang thêm...';
        
        -- Thêm cột muc_do_uu_tien
        ALTER TABLE SUPPLY_REQUEST 
        ADD muc_do_uu_tien NVARCHAR(50) DEFAULT N'Binh thuong';
        
        PRINT N'Đã thêm cột muc_do_uu_tien vào bảng SUPPLY_REQUEST thành công';
    END
    ELSE
    BEGIN
        PRINT N'Cột muc_do_uu_tien đã tồn tại trong bảng SUPPLY_REQUEST';
    END
END
ELSE
BEGIN
    PRINT N'Bảng SUPPLY_REQUEST chưa tồn tại. Đang tạo bảng mới...';
    
    -- Tạo bảng SUPPLY_REQUEST với đầy đủ các cột
    CREATE TABLE SUPPLY_REQUEST (
        id INT IDENTITY(1,1) PRIMARY KEY,
        vat_pham_id INT NOT NULL,
        so_luong_yeu_cau SMALLINT NOT NULL,
        ly_do_yeu_cau NVARCHAR(500),
        nhan_vien_yeu_cau INT NOT NULL,
        trang_thai NVARCHAR(20) DEFAULT 'CHO_DUYET',
        admin_phe_duyet INT,
        ngay_yeu_cau DATETIME2,
        ngay_phe_duyet DATETIME2,
        ghi_chu_admin NVARCHAR(500),
        muc_do_uu_tien NVARCHAR(50) DEFAULT N'Binh thuong',
        uuid_id UNIQUEIDENTIFIER,
        created_date BIGINT,
        last_modified_date BIGINT,
        
        -- Thêm constraints
        CONSTRAINT CHK_SUPPLY_REQUEST_TRANG_THAI 
            CHECK (trang_thai IN ('CHO_DUYET', 'DA_DUYET', 'TU_CHOI', 'DA_THUC_HIEN')),
        CONSTRAINT CHK_SUPPLY_REQUEST_MUC_DO_UU_TIEN 
            CHECK (muc_do_uu_tien IN (N'Khan cap', N'Cao', N'Binh thuong', N'Thap'))
    );
    
    PRINT N'Đã tạo bảng SUPPLY_REQUEST thành công';
END
GO

-- Hiển thị cấu trúc bảng sau khi sửa
PRINT N'=== CẤU TRÚC BẢNG SUPPLY_REQUEST SAU KHI SỬA ===';
SELECT 
    COLUMN_NAME as 'Tên cột',
    DATA_TYPE as 'Kiểu dữ liệu',
    CHARACTER_MAXIMUM_LENGTH as 'Độ dài tối đa',
    IS_NULLABLE as 'Cho phép NULL',
    COLUMN_DEFAULT as 'Giá trị mặc định'
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'SUPPLY_REQUEST' 
ORDER BY ORDINAL_POSITION;
GO

PRINT N'=== HOÀN THÀNH SỬA LỖI BẢNG SUPPLY_REQUEST ===';
PRINT N'Bây giờ bạn có thể chạy lại ứng dụng Spring Boot.';
