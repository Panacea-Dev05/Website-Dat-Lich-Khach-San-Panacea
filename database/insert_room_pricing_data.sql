-- Insert room pricing data for testing
-- This script will add pricing data for room types

-- First, check existing room types
SELECT id, ma_loai_phong, ten_loai_phong FROM ROOM_TYPE;

-- Check existing pricing data
SELECT * FROM ROOM_PRICING;

-- Insert pricing data for room types
-- Assuming room type IDs exist, adjust as needed

-- Insert pricing for room type 1006 (or adjust ID as needed)
INSERT INTO ROOM_PRICING (
    loai_phong_id, 
    loai_gia, 
    gia_tri, 
    gia_gio, 
    gia_ngay, 
    gia_qua_dem, 
    ngay_bat_dau, 
    ngay_ket_thuc, 
    ap_dung_cho, 
    he_so_dieu_chinh, 
    trang_thai, 
    created_date, 
    last_modified_date, 
    uuid_id
) VALUES 
-- Room type 1006 pricing
(1006, 'BASE', 1500000.00, 200000.00, 1500000.00, 800000.00, '2024-01-01', '2025-12-31', 'All', 1.00, 'Hoạt động', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, UUID()),

-- Add pricing for other room types if they exist
(1, 'BASE', 1200000.00, 150000.00, 1200000.00, 600000.00, '2024-01-01', '2025-12-31', 'All', 1.00, 'Hoạt động', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, UUID()),
(2, 'BASE', 1800000.00, 250000.00, 1800000.00, 900000.00, '2024-01-01', '2025-12-31', 'All', 1.00, 'Hoạt động', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, UUID()),
(3, 'BASE', 2200000.00, 300000.00, 2200000.00, 1100000.00, '2024-01-01', '2025-12-31', 'All', 1.00, 'Hoạt động', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, UUID());

-- Verify the inserted data
SELECT 
    rp.id,
    rt.ten_loai_phong,
    rp.loai_gia,
    rp.gia_ngay,
    rp.gia_gio,
    rp.gia_qua_dem,
    rp.gia_tri
FROM ROOM_PRICING rp
JOIN ROOM_TYPE rt ON rp.loai_phong_id = rt.id
ORDER BY rt.id;
