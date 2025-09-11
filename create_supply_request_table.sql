-- Script để tạo bảng SUPPLY_REQUEST trong SQL Server
-- Chạy script này trong SQL Server Management Studio hoặc công cụ quản lý database

USE HotelBookingDB_LastStand_11;
GO

-- Kiểm tra xem bảng SUPPLY_REQUEST đã tồn tại chưa
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES 
               WHERE TABLE_NAME = 'SUPPLY_REQUEST')
BEGIN
    -- Tạo bảng SUPPLY_REQUEST
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
    
    PRINT N'Da tao bang SUPPLY_REQUEST thanh cong';
END
ELSE
BEGIN
    PRINT N'Bang SUPPLY_REQUEST da ton tai';
END
GO

-- Thêm foreign key constraints (nếu các bảng liên quan đã tồn tại)
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'INVENTORY_MANAGEMENT')
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
                   WHERE CONSTRAINT_NAME = 'FK_SUPPLY_REQUEST_INVENTORY')
    BEGIN
        ALTER TABLE SUPPLY_REQUEST
        ADD CONSTRAINT FK_SUPPLY_REQUEST_INVENTORY
        FOREIGN KEY (vat_pham_id) REFERENCES INVENTORY_MANAGEMENT(id);
        
        PRINT N'Da them foreign key constraint cho vat_pham_id';
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
        
        PRINT N'Da them foreign key constraint cho nhan_vien_yeu_cau';
    END
    
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
                   WHERE CONSTRAINT_NAME = 'FK_SUPPLY_REQUEST_STAFF_APPROVER')
    BEGIN
        ALTER TABLE SUPPLY_REQUEST
        ADD CONSTRAINT FK_SUPPLY_REQUEST_STAFF_APPROVER
        FOREIGN KEY (admin_phe_duyet) REFERENCES STAFF(id);
        
        PRINT N'Da them foreign key constraint cho admin_phe_duyet';
    END
END

-- Tạo indexes để tối ưu hiệu suất
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_SUPPLY_REQUEST_TRANG_THAI')
BEGIN
    CREATE INDEX IX_SUPPLY_REQUEST_TRANG_THAI ON SUPPLY_REQUEST(trang_thai);
    PRINT N'Da tao index cho trang_thai';
END

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_SUPPLY_REQUEST_NHAN_VIEN_YEU_CAU')
BEGIN
    CREATE INDEX IX_SUPPLY_REQUEST_NHAN_VIEN_YEU_CAU ON SUPPLY_REQUEST(nhan_vien_yeu_cau);
    PRINT N'Da tao index cho nhan_vien_yeu_cau';
END

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_SUPPLY_REQUEST_NGAY_YEU_CAU')
BEGIN
    CREATE INDEX IX_SUPPLY_REQUEST_NGAY_YEU_CAU ON SUPPLY_REQUEST(ngay_yeu_cau);
    PRINT N'Da tao index cho ngay_yeu_cau';
END

PRINT '';
PRINT N'=== HOAN THANH TAO BANG SUPPLY_REQUEST ===';
PRINT N'Bay gio ban co the chay lai ung dung Spring Boot.';
PRINT N'Bang SUPPLY_REQUEST da duoc tao voi tat ca constraints va indexes can thiet.';