-- Script để sửa lỗi CHECK constraint CHK_SUPPLY_REQUEST_TRANG_THAI
-- Cập nhật dữ liệu cũ từ giá trị tiếng Việt sang enum name

USE HotelBookingDB_LastStand_11;
GO

-- Kiểm tra và cập nhật dữ liệu cũ
PRINT 'Đang kiểm tra và cập nhật dữ liệu trạng thái yêu cầu bổ sung...';

-- Cập nhật từ giá trị tiếng Việt sang enum name
UPDATE SUPPLY_REQUEST 
SET trang_thai = 'CHO_DUYET' 
WHERE trang_thai = N'Chờ duyệt';

UPDATE SUPPLY_REQUEST 
SET trang_thai = 'DA_DUYET' 
WHERE trang_thai = N'Đã duyệt';

UPDATE SUPPLY_REQUEST 
SET trang_thai = 'TU_CHOI' 
WHERE trang_thai = N'Từ chối';

UPDATE SUPPLY_REQUEST 
SET trang_thai = 'DA_THUC_HIEN' 
WHERE trang_thai = N'Đã thực hiện';

-- Kiểm tra kết quả
SELECT DISTINCT trang_thai, COUNT(*) as so_luong
FROM SUPPLY_REQUEST 
GROUP BY trang_thai;

PRINT '';
PRINT '=== HOÀN THÀNH SỬA LỖI TRẠNG THÁI YÊU CẦU BỔ SUNG ===';
PRINT 'Đã cập nhật tất cả trạng thái về định dạng enum name';
PRINT 'Bây giờ có thể thêm yêu cầu bổ sung mà không gặp lỗi CHECK constraint';
PRINT 'Hãy chạy lại ứng dụng Spring Boot.';