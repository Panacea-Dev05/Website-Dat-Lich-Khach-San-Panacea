-- Thêm cột booking_type vào bảng booking
ALTER TABLE booking ADD COLUMN booking_type VARCHAR(20) DEFAULT 'DAY';

-- Cập nhật tất cả các booking hiện tại với giá trị mặc định
UPDATE booking SET booking_type = 'DAY' WHERE booking_type IS NULL;

-- Thêm ràng buộc NOT NULL sau khi đã cập nhật dữ liệu
ALTER TABLE booking ALTER COLUMN booking_type SET NOT NULL;

-- Thêm ràng buộc CHECK để đảm bảo chỉ nhận các giá trị hợp lệ
ALTER TABLE booking ADD CONSTRAINT chk_booking_type CHECK (booking_type IN ('DAY', 'HOUR', 'OVERNIGHT'));