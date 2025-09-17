-- Kiểm tra CHECK constraint hiện tại cho cột trang_thai
SELECT 
    cc.name AS constraint_name,
    cc.definition AS constraint_definition
FROM sys.check_constraints cc
INNER JOIN sys.tables t ON cc.parent_object_id = t.object_id
WHERE t.name = 'SUPPLY_REQUEST' 
AND cc.name LIKE '%trang%';

-- Kiểm tra dữ liệu hiện tại trong cột trang_thai
SELECT DISTINCT trang_thai, COUNT(*) as count
FROM SUPPLY_REQUEST 
GROUP BY trang_thai;

-- Nếu constraint không khớp với enum values, hãy chạy các lệnh sau:

-- 1. Xóa constraint cũ
-- ALTER TABLE SUPPLY_REQUEST DROP CONSTRAINT CK__SUPPLY_RE__trang__2057CCD0;

-- 2. Cập nhật các giá trị không hợp lệ thành CHO_DUYET
-- UPDATE SUPPLY_REQUEST 
-- SET trang_thai = 'CHO_DUYET' 
-- WHERE trang_thai NOT IN ('CHO_DUYET', 'DA_DUYET', 'TU_CHOI', 'DA_THUC_HIEN');

-- 3. Tạo constraint mới
-- ALTER TABLE SUPPLY_REQUEST 
-- ADD CONSTRAINT CK_SUPPLY_REQUEST_trang_thai 
-- CHECK (trang_thai IN ('CHO_DUYET', 'DA_DUYET', 'TU_CHOI', 'DA_THUC_HIEN'));

-- 4. Đặt default value
-- ALTER TABLE SUPPLY_REQUEST 
-- ADD CONSTRAINT DF_SUPPLY_REQUEST_trang_thai 
-- DEFAULT 'CHO_DUYET' FOR trang_thai;

