-- Tạo bảng ROOM_USAGE để theo dõi lịch sử sử dụng đồ của phòng
-- SQL Server

CREATE TABLE ROOM_USAGE (
    id INT IDENTITY(1,1) PRIMARY KEY,
    so_phong NVARCHAR(10) NOT NULL,
    ten_vat_pham NVARCHAR(100) NOT NULL,
    ma_vat_pham NVARCHAR(20),
    so_luong_su_dung INT NOT NULL DEFAULT 1,
    don_vi_tinh NVARCHAR(20),
    ngay_su_dung DATETIME2 NOT NULL,
    loai_su_dung NVARCHAR(50),
    ghi_chu NVARCHAR(500),
    nhan_vien_ghi_nhan NVARCHAR(100),
    trang_thai NVARCHAR(20) DEFAULT 'Hoạt động',
    uuid_id UNIQUEIDENTIFIER,
    created_date BIGINT,
    last_modified_date BIGINT,
    phong_id INT,
    vat_pham_id INT,
    
    -- Foreign key constraints
    CONSTRAINT FK_ROOM_USAGE_PHONG FOREIGN KEY (phong_id) REFERENCES ROOM(id),
    CONSTRAINT FK_ROOM_USAGE_VAT_PHAM FOREIGN KEY (vat_pham_id) REFERENCES INVENTORY_MANAGEMENT(id)
);

-- Tạo index để tối ưu hóa truy vấn
CREATE INDEX IX_ROOM_USAGE_SO_PHONG ON ROOM_USAGE(so_phong);
CREATE INDEX IX_ROOM_USAGE_NGAY_SU_DUNG ON ROOM_USAGE(ngay_su_dung);
CREATE INDEX IX_ROOM_USAGE_LOAI_SU_DUNG ON ROOM_USAGE(loai_su_dung);
CREATE INDEX IX_ROOM_USAGE_TEN_VAT_PHAM ON ROOM_USAGE(ten_vat_pham);

-- Thêm dữ liệu mẫu
INSERT INTO ROOM_USAGE (so_phong, ten_vat_pham, ma_vat_pham, so_luong_su_dung, don_vi_tinh, ngay_su_dung, loai_su_dung, ghi_chu, nhan_vien_ghi_nhan, trang_thai, uuid_id, created_date, last_modified_date)
VALUES 
('101', 'Nước suối', 'NS001', 2, 'chai', GETDATE(), 'Amenities', 'Khách sử dụng 2 chai nước suối', 'nhanvien1', 'Hoạt động', NEWID(), DATEDIFF(s, '1970-01-01', GETUTCDATE()) * 1000, DATEDIFF(s, '1970-01-01', GETUTCDATE()) * 1000),
('101', 'Snack', 'SN001', 1, 'gói', GETDATE(), 'Food', 'Khách sử dụng 1 gói snack', 'nhanvien1', 'Hoạt động', NEWID(), DATEDIFF(s, '1970-01-01', GETUTCDATE()) * 1000, DATEDIFF(s, '1970-01-01', GETUTCDATE()) * 1000),
('102', 'Khăn tắm', 'KT001', 2, 'cái', GETDATE(), 'Amenities', 'Thay khăn tắm cho khách', 'nhanvien2', 'Hoạt động', NEWID(), DATEDIFF(s, '1970-01-01', GETUTCDATE()) * 1000, DATEDIFF(s, '1970-01-01', GETUTCDATE()) * 1000),
('103', 'Nước rửa chén', 'NRC001', 1, 'chai', GETDATE(), 'Cleaning', 'Sử dụng để vệ sinh phòng', 'nhanvien3', 'Hoạt động', NEWID(), DATEDIFF(s, '1970-01-01', GETUTCDATE()) * 1000, DATEDIFF(s, '1970-01-01', GETUTCDATE()) * 1000),
('104', 'Bóng đèn', 'BD001', 1, 'cái', GETDATE(), 'Maintenance', 'Thay bóng đèn hỏng', 'nhanvien4', 'Hoạt động', NEWID(), DATEDIFF(s, '1970-01-01', GETUTCDATE()) * 1000, DATEDIFF(s, '1970-01-01', GETUTCDATE()) * 1000);

-- Kiểm tra dữ liệu
SELECT * FROM ROOM_USAGE ORDER BY ngay_su_dung DESC;
