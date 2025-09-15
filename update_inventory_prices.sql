USE HotelBookingDB_LastStand_11;
GO

PRINT N'=== CẬP NHẬT GIÁ BÁN CHO VẬT PHẨM TỒN KHO ===';
GO

-- Cập nhật giá bán cho các vật phẩm chưa có giá bán
-- Sử dụng giá nhập + 20% làm giá bán mặc định
BEGIN TRY
    UPDATE INVENTORY_MANAGEMENT 
    SET gia_ban = CASE 
        WHEN gia_nhap IS NOT NULL THEN gia_nhap * 1.2
        ELSE 50000  -- Giá mặc định 50,000 VND nếu không có giá nhập
    END
    WHERE gia_ban IS NULL;
    
    DECLARE @updated_count INT = @@ROWCOUNT;
    PRINT N'✓ Đã cập nhật giá bán cho ' + CAST(@updated_count AS NVARCHAR(10)) + N' vật phẩm';
END TRY
BEGIN CATCH
    PRINT N'❌ Lỗi khi cập nhật giá bán: ' + ERROR_MESSAGE();
END CATCH
GO

-- Cập nhật giá bán cụ thể cho một số vật phẩm phổ biến
BEGIN TRY
    -- Cập nhật giá cho các vật phẩm amenities
    UPDATE INVENTORY_MANAGEMENT 
    SET gia_ban = CASE 
        WHEN ten_vat_pham LIKE N'%dầu gội%' OR ten_vat_pham LIKE N'%shampoo%' THEN 15000
        WHEN ten_vat_pham LIKE N'%sữa tắm%' OR ten_vat_pham LIKE N'%shower gel%' THEN 15000
        WHEN ten_vat_pham LIKE N'%khăn%' OR ten_vat_pham LIKE N'%towel%' THEN 50000
        WHEN ten_vat_pham LIKE N'%nước%' OR ten_vat_pham LIKE N'%water%' THEN 10000
        WHEN ten_vat_pham LIKE N'%bánh%' OR ten_vat_pham LIKE N'%snack%' THEN 25000
        WHEN ten_vat_pham LIKE N'%nước lau%' OR ten_vat_pham LIKE N'%cleaning%' THEN 30000
        WHEN ten_vat_pham LIKE N'%giấy%' OR ten_vat_pham LIKE N'%tissue%' THEN 20000
        ELSE gia_ban  -- Giữ nguyên giá hiện tại
    END
    WHERE gia_ban IS NOT NULL;
    
    PRINT N'✓ Đã cập nhật giá bán theo loại vật phẩm';
END TRY
BEGIN CATCH
    PRINT N'❌ Lỗi khi cập nhật giá theo loại: ' + ERROR_MESSAGE();
END CATCH
GO

-- Kiểm tra kết quả
PRINT N'=== KIỂM TRA KẾT QUẢ ===';
GO

SELECT 
    COUNT(*) as 'Tổng số vật phẩm',
    COUNT(CASE WHEN gia_ban IS NOT NULL THEN 1 END) as 'Có giá bán',
    COUNT(CASE WHEN gia_ban IS NULL THEN 1 END) as 'Chưa có giá bán'
FROM INVENTORY_MANAGEMENT;
GO

-- Hiển thị một số vật phẩm mẫu
SELECT TOP 10
    ten_vat_pham,
    gia_nhap,
    gia_ban,
    so_luong_ton
FROM INVENTORY_MANAGEMENT
WHERE so_luong_ton > 0
ORDER BY id;
GO

PRINT N'=== HOÀN THÀNH ===';
PRINT N'Đã cập nhật giá bán cho các vật phẩm tồn kho.';
GO