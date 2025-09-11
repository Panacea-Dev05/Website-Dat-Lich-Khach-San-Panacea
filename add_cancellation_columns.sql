-- Script để thêm các cột liên quan đến chính sách hủy vào bảng BOOKING

USE [your_database_name]; -- Thay thế bằng tên database thực tế
GO

-- Thêm cột cancellation_policy
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[BOOKING]') AND name = 'cancellation_policy')
BEGIN
    ALTER TABLE [dbo].[BOOKING] ADD [cancellation_policy] NVARCHAR(20) NULL;
    PRINT 'Đã thêm cột cancellation_policy';
END
ELSE
BEGIN
    PRINT 'Cột cancellation_policy đã tồn tại';
END
GO

-- Thêm cột refund_amount
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[BOOKING]') AND name = 'refund_amount')
BEGIN
    ALTER TABLE [dbo].[BOOKING] ADD [refund_amount] DECIMAL(15,2) NULL DEFAULT 0;
    PRINT 'Đã thêm cột refund_amount';
END
ELSE
BEGIN
    PRINT 'Cột refund_amount đã tồn tại';
END
GO

-- Thêm cột cancellation_fee
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID(N'[dbo].[BOOKING]') AND name = 'cancellation_fee')
BEGIN
    ALTER TABLE [dbo].[BOOKING] ADD [cancellation_fee] DECIMAL(15,2) NULL DEFAULT 0;
    PRINT 'Đã thêm cột cancellation_fee';
END
ELSE
BEGIN
    PRINT 'Cột cancellation_fee đã tồn tại';
END
GO

-- Cập nhật giá trị mặc định cho các bản ghi hiện có
UPDATE [dbo].[BOOKING] 
SET [cancellation_policy] = 'MODERATE'
WHERE [cancellation_policy] IS NULL;

UPDATE [dbo].[BOOKING] 
SET [refund_amount] = 0
WHERE [refund_amount] IS NULL;

UPDATE [dbo].[BOOKING] 
SET [cancellation_fee] = 0
WHERE [cancellation_fee] IS NULL;

PRINT 'Đã cập nhật giá trị mặc định cho các bản ghi hiện có';
GO

PRINT 'Hoàn thành việc thêm các cột liên quan đến chính sách hủy!';