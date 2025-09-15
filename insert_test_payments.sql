-- Insert test payments data
-- Make sure you have bookings in the database first

-- Check if there are any bookings
SELECT COUNT(*) as booking_count FROM BOOKING;

-- Insert test payments (adjust booking_id based on your actual data)
INSERT INTO PAYMENT (ma_thanh_toan, dat_phong_id, so_tien, phuong_thuc, noi_dung, trang_thai, ngay_thanh_toan, created_date, last_modified_date, uuid_id)
VALUES 
('PAY001', 1, 100000.00, 'Cash', 'Thanh toán test 1', 'DANG_XU_LY', NOW(), UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, UUID()),
('PAY002', 1, 200000.00, 'Credit Card', 'Thanh toán test 2', 'THANH_CONG', NOW(), UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, UUID()),
('PAY003', 2, 300000.00, 'Bank Transfer', 'Thanh toán test 3', 'THAT_BAI', NOW(), UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, UUID()),
('PAY004', 2, 150000.00, 'E-wallet', 'Thanh toán test 4', 'HOAN_TIEN', NOW(), UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, UUID());

-- Verify the data
SELECT * FROM PAYMENT;
