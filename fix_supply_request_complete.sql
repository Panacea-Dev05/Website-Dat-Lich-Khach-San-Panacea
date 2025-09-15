-- Script hoàn chỉnh để sửa lỗi bảng SUPPLY_REQUEST
-- Chạy script này trong SQL Server Management Studio

USE HotelBookingDB_LastStand_11;
GO

-- Kiểm tra cấu trúc hiện tại của bảng SUPPLY_REQUEST
PRINT N'=== CẤU TRÚC BẢNG SUPPLY_REQUEST HIỆN TẠI ===';
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

-- Xóa bảng cũ nếu tồn tại (SAO LƯU DỮ LIỆU TRƯỚC KHI CHẠY)
-- UNCOMMENT DÒNG DƯỚI NẾU MUỐN XÓA BẢNG CŨ
-- DROP TABLE IF EXISTS SUPPLY_REQUEST;
-- GO

-- Tạo lại bảng SUPPLY_REQUEST với cấu trúc đúng
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'SUPPLY_REQUEST')
BEGIN
    PRINT N'Bảng SUPPLY_REQUEST đã tồn tại. Đang thêm các cột thiếu...';
    
    -- Thêm các cột thiếu nếu chưa có
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'SUPPLY_REQUEST' AND COLUMN_NAME = 'ly_do_yeu_cau')
    BEGIN
        ALTER TABLE SUPPLY_REQUEST ADD ly_do_yeu_cau NVARCHAR(500);
        PRINT N'Đã thêm cột ly_do_yeu_cau';
    END
    
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'SUPPLY_REQUEST' AND COLUMN_NAME = 'ghi_chu_admin')
    BEGIN
        ALTER TABLE SUPPLY_REQUEST ADD ghi_chu_admin NVARCHAR(500);
        PRINT N'Đã thêm cột ghi_chu_admin';
    END
    
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'SUPPLY_REQUEST' AND COLUMN_NAME = 'muc_do_uu_tien')
    BEGIN
        ALTER TABLE SUPPLY_REQUEST ADD muc_do_uu_tien NVARCHAR(50) DEFAULT N'Binh thuong';
        PRINT N'Đã thêm cột muc_do_uu_tien';
    END
    
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'SUPPLY_REQUEST' AND COLUMN_NAME = 'ngay_yeu_cau')
    BEGIN
        ALTER TABLE SUPPLY_REQUEST ADD ngay_yeu_cau DATETIME2;
        PRINT N'Đã thêm cột ngay_yeu_cau';
    END
    
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'SUPPLY_REQUEST' AND COLUMN_NAME = 'ngay_phe_duyet')
    BEGIN
        ALTER TABLE SUPPLY_REQUEST ADD ngay_phe_duyet DATETIME2;
        PRINT N'Đã thêm cột ngay_phe_duyet';
    END
    
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'SUPPLY_REQUEST' AND COLUMN_NAME = 'admin_phe_duyet')
    BEGIN
        ALTER TABLE SUPPLY_REQUEST ADD admin_phe_duyet INT;
        PRINT N'Đã thêm cột admin_phe_duyet';
    END
    
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'SUPPLY_REQUEST' AND COLUMN_NAME = 'trang_thai')
    BEGIN
        ALTER TABLE SUPPLY_REQUEST ADD trang_thai NVARCHAR(20) DEFAULT 'CHO_DUYET';
        PRINT N'Đã thêm cột trang_thai';
    END
    
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'SUPPLY_REQUEST' AND COLUMN_NAME = 'uuid_id')
    BEGIN
        ALTER TABLE SUPPLY_REQUEST ADD uuid_id UNIQUEIDENTIFIER;
        PRINT N'Đã thêm cột uuid_id';
    END
    
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'SUPPLY_REQUEST' AND COLUMN_NAME = 'created_date')
    BEGIN
        ALTER TABLE SUPPLY_REQUEST ADD created_date BIGINT;
        PRINT N'Đã thêm cột created_date';
    END
    
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = 'SUPPLY_REQUEST' AND COLUMN_NAME = 'last_modified_date')
    BEGIN
        ALTER TABLE SUPPLY_REQUEST ADD last_modified_date BIGINT;
        PRINT N'Đã thêm cột last_modified_date';
    END
END
ELSE
BEGIN
    PRINT N'Bảng SUPPLY_REQUEST chưa tồn tại. Đang tạo bảng mới...';
    
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

-- Thêm foreign key constraints nếu các bảng liên quan tồn tại
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'INVENTORY_MANAGEMENT')
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
                   WHERE CONSTRAINT_NAME = 'FK_SUPPLY_REQUEST_INVENTORY')
    BEGIN
        ALTER TABLE SUPPLY_REQUEST
        ADD CONSTRAINT FK_SUPPLY_REQUEST_INVENTORY
        FOREIGN KEY (vat_pham_id) REFERENCES INVENTORY_MANAGEMENT(id);
        PRINT N'Đã thêm foreign key constraint cho vat_pham_id';
    END
END

IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'STAFF')
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
                   WHERE CONSTRAINT_NAME = 'FK_SUPPLY_REQUEST_STAFF_REQUESTER')
    BEGIN
        ALTER TABLE SUPPLY_REQUEST
        ADD CONSTRAINT FK_SUPPLY_REQUEST_STAFF_REQUESTER
        FOREIGN KEY (nhan_vien_yeu_cau) REFERENCES STAFF(id);
        PRINT N'Đã thêm foreign key constraint cho nhan_vien_yeu_cau';
    END
    
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
                   WHERE CONSTRAINT_NAME = 'FK_SUPPLY_REQUEST_STAFF_APPROVER')
    BEGIN
        ALTER TABLE SUPPLY_REQUEST
        ADD CONSTRAINT FK_SUPPLY_REQUEST_STAFF_APPROVER
        FOREIGN KEY (admin_phe_duyet) REFERENCES STAFF(id);
        PRINT N'Đã thêm foreign key constraint cho admin_phe_duyet';
    END
END

PRINT N'=== HOÀN THÀNH SỬA LỖI BẢNG SUPPLY_REQUEST ===';
PRINT N'Bây giờ bạn có thể chạy lại ứng dụng Spring Boot.';
