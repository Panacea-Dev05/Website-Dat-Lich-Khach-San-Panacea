-- Script để tạo bảng EVENT_CALENDAR với cột khach_san_id
-- Chạy script này trong SQL Server Management Studio

USE HotelBookingDB_LastStand_11;
GO

-- Kiểm tra xem bảng EVENT_CALENDAR đã tồn tại chưa
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_NAME = 'EVENT_CALENDAR')
BEGIN
    PRINT N'Tạo bảng EVENT_CALENDAR mới...';
    
    CREATE TABLE EVENT_CALENDAR (
        id INT IDENTITY(1,1) PRIMARY KEY,
        khach_san_id INT NOT NULL,
        ten_su_kien NVARCHAR(200) NOT NULL,
        loai_su_kien NVARCHAR(100),
        mo_ta NVARCHAR(MAX),
        ngay_bat_dau DATETIME2 NOT NULL,
        ngay_ket_thuc DATETIME2 NOT NULL,
        tac_dong_gia BIT DEFAULT 0, -- Có ảnh hưởng đến giá phòng không
        he_so_tac_dong DECIMAL(5,2) DEFAULT 1.0, -- Hệ số tác động giá
        trang_thai NVARCHAR(20) DEFAULT N'Hoạt động',
        uuid_id UNIQUEIDENTIFIER DEFAULT NEWID(),
        created_date BIGINT,
        last_modified_date BIGINT,
        
        -- Thêm foreign key constraint
        CONSTRAINT FK_EVENT_CALENDAR_HOTEL
            FOREIGN KEY (khach_san_id) REFERENCES HOTEL(id),
        
        -- Thêm constraints
        CONSTRAINT CHK_EVENT_CALENDAR_TRANG_THAI 
            CHECK (trang_thai IN (N'Hoạt động', N'Tạm dừng', N'Đã kết thúc')),
        CONSTRAINT CHK_EVENT_CALENDAR_NGAY
            CHECK (ngay_ket_thuc >= ngay_bat_dau)
    );
    
    PRINT N'Đã tạo bảng EVENT_CALENDAR thành công';
END
ELSE
BEGIN
    PRINT N'Bảng EVENT_CALENDAR đã tồn tại. Kiểm tra cột khach_san_id...';
    
    -- Kiểm tra và thêm cột khach_san_id nếu chưa có
    IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
                   WHERE TABLE_NAME = 'EVENT_CALENDAR' 
                   AND COLUMN_NAME = 'khach_san_id')
    BEGIN
        ALTER TABLE EVENT_CALENDAR ADD khach_san_id INT NOT NULL DEFAULT 1;
        PRINT N'Đã thêm cột khach_san_id vào bảng EVENT_CALENDAR';
        
        -- Thêm foreign key constraint nếu chưa có
        IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
                       WHERE CONSTRAINT_NAME = 'FK_EVENT_CALENDAR_HOTEL')
        BEGIN
            ALTER TABLE EVENT_CALENDAR
            ADD CONSTRAINT FK_EVENT_CALENDAR_HOTEL
            FOREIGN KEY (khach_san_id) REFERENCES HOTEL(id);
            PRINT N'Đã thêm foreign key constraint cho khach_san_id';
        END
    END
    ELSE
    BEGIN
        PRINT N'Cột khach_san_id đã tồn tại trong bảng EVENT_CALENDAR';
    END
END
GO

-- Hiển thị cấu trúc bảng sau khi tạo/sửa
PRINT N'=== CẤU TRÚC BẢNG EVENT_CALENDAR ==='
SELECT 
    COLUMN_NAME as 'Tên cột',
    DATA_TYPE as 'Kiểu dữ liệu',
    CHARACTER_MAXIMUM_LENGTH as 'Độ dài tối đa',
    IS_NULLABLE as 'Cho phép NULL',
    COLUMN_DEFAULT as 'Giá trị mặc định'
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'EVENT_CALENDAR' 
ORDER BY ORDINAL_POSITION;
GO

-- Thêm một số dữ liệu mẫu nếu bảng trống
IF NOT EXISTS (SELECT * FROM EVENT_CALENDAR)
BEGIN
    PRINT N'Thêm dữ liệu mẫu vào bảng EVENT_CALENDAR...';
    
    INSERT INTO EVENT_CALENDAR (
        khach_san_id, ten_su_kien, loai_su_kien, mo_ta, 
        ngay_bat_dau, ngay_ket_thuc, tac_dong_gia, he_so_tac_dong,
        created_date, last_modified_date
    ) VALUES 
    (1, N'Lễ hội mùa xuân', N'Lễ hội', N'Lễ hội chào đón mùa xuân với nhiều hoạt động thú vị', 
     '2024-03-01 00:00:00', '2024-03-03 23:59:59', 1, 1.2,
     CAST(DATEDIFF_BIG(SECOND, '1970-01-01', GETDATE()) * 1000 AS BIGINT),
     CAST(DATEDIFF_BIG(SECOND, '1970-01-01', GETDATE()) * 1000 AS BIGINT)),
    (1, N'Hội nghị doanh nghiệp', N'Hội nghị', N'Hội nghị thường niên các doanh nghiệp lớn', 
     '2024-06-15 08:00:00', '2024-06-17 18:00:00', 1, 1.5,
     CAST(DATEDIFF_BIG(SECOND, '1970-01-01', GETDATE()) * 1000 AS BIGINT),
     CAST(DATEDIFF_BIG(SECOND, '1970-01-01', GETDATE()) * 1000 AS BIGINT)),
    (1, N'Đêm nhạc acoustic', N'Giải trí', N'Đêm nhạc acoustic với các nghệ sĩ nổi tiếng', 
     '2024-12-24 19:00:00', '2024-12-24 23:00:00', 0, 1.0,
     CAST(DATEDIFF_BIG(SECOND, '1970-01-01', GETDATE()) * 1000 AS BIGINT),
     CAST(DATEDIFF_BIG(SECOND, '1970-01-01', GETDATE()) * 1000 AS BIGINT));
     
    PRINT N'Đã thêm dữ liệu mẫu thành công';
END
GO

PRINT N'=== HOÀN THÀNH SỬA LỖI BẢNG EVENT_CALENDAR ==='
PRINT N'Bây giờ bạn có thể chạy lại ứng dụng Spring Boot.'