-- Script thêm cột ma_yeu_cau vào bảng SUPPLY_REQUEST
-- Chạy script này để khắc phục lỗi "Invalid column name 'ma_yeu_cau'"

-- Kiểm tra xem cột đã tồn tại chưa trước khi thêm
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'SUPPLY_REQUEST' 
               AND COLUMN_NAME = 'ma_yeu_cau')
BEGIN
    -- Thêm cột ma_yeu_cau
    ALTER TABLE SUPPLY_REQUEST 
    ADD ma_yeu_cau NVARCHAR(50) NULL;
    
    -- Tạo unique constraint nếu cần
    -- ALTER TABLE SUPPLY_REQUEST 
    -- ADD CONSTRAINT UK_SUPPLY_REQUEST_MA_YEU_CAU UNIQUE (ma_yeu_cau);
    
    PRINT 'Đã thêm cột ma_yeu_cau vào bảng SUPPLY_REQUEST';
END
ELSE
BEGIN
    PRINT 'Cột ma_yeu_cau đã tồn tại trong bảng SUPPLY_REQUEST';
END
