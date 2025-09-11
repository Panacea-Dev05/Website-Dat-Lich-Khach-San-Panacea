-- Thêm cột yeu_cau_dac_biet vào bảng booking (SQL Server syntax)
ALTER TABLE booking ADD yeu_cau_dac_biet NVARCHAR(1000);

-- Thêm extended property để mô tả cột (thay cho COMMENT trong SQL Server)
EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Yêu cầu đặc biệt từ khách hàng như vị trí phòng, view, v.v.', 
    @level0type = N'SCHEMA', @level0name = N'dbo', 
    @level1type = N'TABLE', @level1name = N'booking', 
    @level2type = N'COLUMN', @level2name = N'yeu_cau_dac_biet';

-- Kiểm tra cấu trúc bảng sau khi thêm cột
SELECT COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH, IS_NULLABLE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'booking' AND COLUMN_NAME = 'yeu_cau_dac_biet';