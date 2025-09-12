-- Thêm cột inventory_item_id vào bảng SERVICE_DETAIL
ALTER TABLE SERVICE_DETAIL ADD inventory_item_id INT;

-- Thêm foreign key constraint để liên kết với bảng INVENTORY_MANAGEMENT
ALTER TABLE SERVICE_DETAIL 
ADD CONSTRAINT FK_SERVICE_DETAIL_INVENTORY 
FOREIGN KEY (inventory_item_id) REFERENCES INVENTORY_MANAGEMENT(id);

-- Thêm extended property để mô tả cột
EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'ID của vật phẩm tồn kho được sử dụng cho dịch vụ này', 
    @level0type = N'SCHEMA', @level0name = N'dbo', 
    @level1type = N'TABLE', @level1name = N'SERVICE_DETAIL', 
    @level2type = N'COLUMN', @level2name = N'inventory_item_id';

-- Kiểm tra cấu trúc bảng sau khi thêm cột
SELECT COLUMN_NAME, DATA_TYPE, IS_NULLABLE 
FROM INFORMATION_SCHEMA.COLUMNS 
WHERE TABLE_NAME = 'SERVICE_DETAIL' AND COLUMN_NAME = 'inventory_item_id';

PRINT N'Đã thêm cột inventory_item_id vào bảng SERVICE_DETAIL thành công!';