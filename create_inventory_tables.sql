-- Script để tạo các bảng quản lý kho trong SQL Server
-- Chạy script này trong SQL Server Management Studio hoặc công cụ quản lý database

USE HotelBookingDB_LastStand_11;
GO

-- Kiểm tra xem bảng INVENTORY_MANAGEMENT đã tồn tại chưa
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES 
               WHERE TABLE_NAME = 'INVENTORY_MANAGEMENT')
BEGIN
    -- Tạo bảng INVENTORY_MANAGEMENT
    CREATE TABLE INVENTORY_MANAGEMENT (
        id INT IDENTITY(1,1) PRIMARY KEY,
        ten_vat_pham NVARCHAR(100) NOT NULL,
        ma_vat_pham NVARCHAR(20) UNIQUE,
        loai_vat_pham NVARCHAR(50),
        don_vi_tinh NVARCHAR(20),
        so_luong_ton SMALLINT DEFAULT 0,
        so_luong_toi_thieu SMALLINT DEFAULT 0,
        gia_nhap DECIMAL(10,2),
        nha_cung_cap NVARCHAR(100),
        ngay_nhap_cuoi DATE,
        han_su_dung DATE,
        vi_tri_kho NVARCHAR(50),
        trang_thai NVARCHAR(20) DEFAULT N'Hoạt động',
        uuid_id UNIQUEIDENTIFIER,
        created_date BIGINT,
        last_modified_date BIGINT
    );
    
    PRINT N'Đã tạo bảng INVENTORY_MANAGEMENT thành công';
END
ELSE
BEGIN
    PRINT N'Bảng INVENTORY_MANAGEMENT đã tồn tại';
END
GO

-- Kiểm tra xem bảng INVENTORY_TRANSACTION đã tồn tại chưa
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES 
               WHERE TABLE_NAME = 'INVENTORY_TRANSACTION')
BEGIN
    -- Tạo bảng INVENTORY_TRANSACTION
    CREATE TABLE INVENTORY_TRANSACTION (
        id INT IDENTITY(1,1) PRIMARY KEY,
        vat_pham_id INT NOT NULL,
        so_luong SMALLINT NOT NULL,
        gia_tri DECIMAL(10,2),
        ly_do NVARCHAR(200),
        loai_giao_dich NVARCHAR(50),
        nhan_vien_id INT,
        phong_id INT,
        ngay_giao_dich DATETIME2,
        so_chung_tu NVARCHAR(50),
        uuid_id UNIQUEIDENTIFIER,
        created_date BIGINT
    );
    
    PRINT N'Đã tạo bảng INVENTORY_TRANSACTION thành công';
END
ELSE
BEGIN
    PRINT N'Bảng INVENTORY_TRANSACTION đã tồn tại';
END
GO

-- Thêm foreign key constraints
IF EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'INVENTORY_MANAGEMENT')
BEGIN
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
                   WHERE CONSTRAINT_NAME = 'FK_INVENTORY_TRANSACTION_INVENTORY')
    BEGIN
        ALTER TABLE INVENTORY_TRANSACTION
        ADD CONSTRAINT FK_INVENTORY_TRANSACTION_INVENTORY
        FOREIGN KEY (vat_pham_id) REFERENCES INVENTORY_MANAGEMENT(id);
        
        PRINT N'Đã thêm foreign key constraint cho vat_pham_id';
    END
END

-- Thêm dữ liệu mẫu
IF NOT EXISTS (SELECT * FROM INVENTORY_MANAGEMENT WHERE ma_vat_pham = 'VP001')
BEGIN
    INSERT INTO INVENTORY_MANAGEMENT (ten_vat_pham, ma_vat_pham, loai_vat_pham, don_vi_tinh, so_luong_ton, so_luong_toi_thieu, gia_nhap, nha_cung_cap, vi_tri_kho, trang_thai, uuid_id, created_date, last_modified_date)
    VALUES 
        (N'Khăn tắm', 'VP001', 'Amenities', N'Cái', 50, 10, 50000, N'Công ty ABC', 'A1-B1', N'Hoạt động', NEWID(), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE()), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE())),
        (N'Xà phòng', 'VP002', 'Amenities', N'Bánh', 100, 20, 15000, N'Công ty XYZ', 'A1-B2', N'Hoạt động', NEWID(), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE()), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE())),
        (N'Dầu gội đầu', 'VP003', 'Amenities', N'Chai', 30, 5, 80000, N'Công ty DEF', 'A1-B3', N'Hoạt động', NEWID(), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE()), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE())),
        (N'Bàn chải đánh răng', 'VP004', 'Amenities', N'Cái', 80, 15, 25000, N'Công ty GHI', 'A1-C1', N'Hoạt động', NEWID(), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE()), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE())),
        (N'Kem đánh răng', 'VP005', 'Amenities', N'Tuýp', 40, 8, 35000, N'Công ty JKL', 'A1-C2', N'Hoạt động', NEWID(), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE()), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE())),
        (N'Nước rửa chén', 'VP006', 'Cleaning', N'Chai', 25, 5, 45000, N'Công ty MNO', 'A2-B1', N'Hoạt động', NEWID(), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE()), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE())),
        (N'Khăn lau', 'VP007', 'Cleaning', N'Cái', 60, 12, 20000, N'Công ty PQR', 'A2-B2', N'Hoạt động', NEWID(), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE()), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE())),
        (N'Bột giặt', 'VP008', 'Cleaning', N'Kg', 20, 3, 120000, N'Công ty STU', 'A2-B3', N'Hoạt động', NEWID(), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE()), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE())),
        (N'Bóng đèn LED', 'VP009', 'Maintenance', N'Cái', 15, 3, 150000, N'Công ty VWX', 'A3-B1', N'Hoạt động', NEWID(), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE()), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE())),
        (N'Ổ cắm điện', 'VP010', 'Maintenance', N'Cái', 25, 5, 80000, N'Công ty YZA', 'A3-B2', N'Hoạt động', NEWID(), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE()), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE()));
    
    PRINT N'Đã thêm dữ liệu mẫu vào bảng INVENTORY_MANAGEMENT';
END

-- Thêm một số giao dịch mẫu
IF NOT EXISTS (SELECT * FROM INVENTORY_TRANSACTION WHERE so_chung_tu = 'GD001')
BEGIN
    INSERT INTO INVENTORY_TRANSACTION (vat_pham_id, so_luong, gia_tri, ly_do, loai_giao_dich, nhan_vien_id, ngay_giao_dich, so_chung_tu, uuid_id, created_date)
    VALUES 
        (1, 50, 2500000, N'Nhập kho lần đầu', 'NHAP_KHO', 1, GETDATE(), 'GD001', NEWID(), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE())),
        (2, 100, 1500000, N'Nhập kho lần đầu', 'NHAP_KHO', 1, GETDATE(), 'GD002', NEWID(), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE())),
        (3, 30, 2400000, N'Nhập kho lần đầu', 'NHAP_KHO', 1, GETDATE(), 'GD003', NEWID(), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE())),
        (1, -5, 250000, N'Xuất cho phòng 101', 'XUAT_KHO', 2, GETDATE(), 'GD004', NEWID(), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE())),
        (2, -10, 150000, N'Xuất cho phòng 102', 'XUAT_KHO', 2, GETDATE(), 'GD005', NEWID(), DATEDIFF_BIG(MILLISECOND, '1970-01-01', GETDATE()));
    
    PRINT N'Đã thêm dữ liệu mẫu vào bảng INVENTORY_TRANSACTION';
END

-- Tạo indexes để tối ưu hiệu suất
IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_INVENTORY_MANAGEMENT_MA_VAT_PHAM')
BEGIN
    CREATE INDEX IX_INVENTORY_MANAGEMENT_MA_VAT_PHAM ON INVENTORY_MANAGEMENT(ma_vat_pham);
    PRINT N'Đã tạo index cho ma_vat_pham';
END

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_INVENTORY_MANAGEMENT_LOAI_VAT_PHAM')
BEGIN
    CREATE INDEX IX_INVENTORY_MANAGEMENT_LOAI_VAT_PHAM ON INVENTORY_MANAGEMENT(loai_vat_pham);
    PRINT N'Đã tạo index cho loai_vat_pham';
END

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_INVENTORY_TRANSACTION_VAT_PHAM_ID')
BEGIN
    CREATE INDEX IX_INVENTORY_TRANSACTION_VAT_PHAM_ID ON INVENTORY_TRANSACTION(vat_pham_id);
    PRINT N'Đã tạo index cho vat_pham_id';
END

IF NOT EXISTS (SELECT * FROM sys.indexes WHERE name = 'IX_INVENTORY_TRANSACTION_NGAY_GIAO_DICH')
BEGIN
    CREATE INDEX IX_INVENTORY_TRANSACTION_NGAY_GIAO_DICH ON INVENTORY_TRANSACTION(ngay_giao_dich);
    PRINT N'Đã tạo index cho ngay_giao_dich';
END

PRINT '';
PRINT N'=== HOÀN THÀNH TẠO CÁC BẢNG QUẢN LÝ KHO ===';
PRINT N'Bây giờ bạn có thể chạy lại ứng dụng Spring Boot.';
PRINT N'Các bảng INVENTORY_MANAGEMENT và INVENTORY_TRANSACTION đã được tạo với dữ liệu mẫu.';
