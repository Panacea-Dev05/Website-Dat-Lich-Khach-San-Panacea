-- Script cập nhật trạng thái phòng
-- Đặt một số phòng về trạng thái SAN_SANG để test

-- Cập nhật phòng 101, 102, 103 về trạng thái SAN_SANG
UPDATE ROOM 
SET trang_thai = 'SAN_SANG' 
WHERE so_phong IN ('101', '102', '103');

-- Cập nhật phòng 201, 202 về trạng thái DANG_SU_DUNG
UPDATE ROOM 
SET trang_thai = 'DANG_SU_DUNG' 
WHERE so_phong IN ('201', '202');

-- Cập nhật phòng 301 về trạng thái BAO_TRI
UPDATE ROOM 
SET trang_thai = 'BAO_TRI' 
WHERE so_phong = '301';

-- Kiểm tra kết quả
SELECT so_phong, trang_thai, 
       CASE trang_thai
           WHEN 'SAN_SANG' THEN 'Sẵn sàng'
           WHEN 'DA_DAT' THEN 'Đã đặt'
           WHEN 'DANG_SU_DUNG' THEN 'Đang sử dụng'
           WHEN 'BAO_TRI' THEN 'Bảo trì'
           WHEN 'DON_DEP' THEN 'Dọn dẹp'
           ELSE 'Không xác định'
       END as trang_thai_text
FROM ROOM 
ORDER BY so_phong;
