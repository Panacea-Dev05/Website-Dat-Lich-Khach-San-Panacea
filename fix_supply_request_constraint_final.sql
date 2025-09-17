-- Sửa CHECK constraint cho cột trang_thai trong bảng SUPPLY_REQUEST
-- Đảm bảo constraint khớp với enum values

-- 1. Kiểm tra constraint hiện tại
SELECT 
    cc.name AS constraint_name,
    cc.definition AS constraint_definition
FROM sys.check_constraints cc
INNER JOIN sys.tables t ON cc.parent_object_id = t.object_id
WHERE t.name = 'SUPPLY_REQUEST' 
AND cc.name LIKE '%trang%';

-- 2. Xóa constraint cũ nếu tồn tại
IF EXISTS (SELECT * FROM sys.check_constraints WHERE name = 'CK__SUPPLY_RE__trang__2057CCD0')
BEGIN
    ALTER TABLE SUPPLY_REQUEST DROP CONSTRAINT CK__SUPPLY_RE__trang__2057CCD0;
    PRINT 'Đã xóa constraint cũ: CK__SUPPLY_RE__trang__2057CCD0';
END

-- 3. Cập nhật các giá trị không hợp lệ thành CHO_DUYET
UPDATE SUPPLY_REQUEST 
SET trang_thai = 'CHO_DUYET' 
WHERE trang_thai NOT IN ('CHO_DUYET', 'DA_DUYET', 'TU_CHOI', 'DA_THUC_HIEN')
OR trang_thai IS NULL;

-- 4. Tạo constraint mới với các giá trị enum đúng
ALTER TABLE SUPPLY_REQUEST 
ADD CONSTRAINT CK_SUPPLY_REQUEST_trang_thai 
CHECK (trang_thai IN ('CHO_DUYET', 'DA_DUYET', 'TU_CHOI', 'DA_THUC_HIEN'));

-- 5. Đặt default value cho cột
ALTER TABLE SUPPLY_REQUEST 
ADD CONSTRAINT DF_SUPPLY_REQUEST_trang_thai 
DEFAULT 'CHO_DUYET' FOR trang_thai;

-- 6. Kiểm tra kết quả
SELECT 
    cc.name AS constraint_name,
    cc.definition AS constraint_definition
FROM sys.check_constraints cc
INNER JOIN sys.tables t ON cc.parent_object_id = t.object_id
WHERE t.name = 'SUPPLY_REQUEST' 
AND cc.name LIKE '%trang%';

PRINT 'Đã sửa xong CHECK constraint cho cột trang_thai';

