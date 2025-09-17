-- Kiểm tra CHECK constraint hiện tại cho cột trang_thai
SELECT 
    cc.name AS constraint_name,
    cc.definition AS constraint_definition,
    cc.is_disabled,
    cc.is_not_trusted
FROM sys.check_constraints cc
INNER JOIN sys.tables t ON cc.parent_object_id = t.object_id
WHERE t.name = 'SUPPLY_REQUEST' 
AND cc.name LIKE '%trang%';

-- Kiểm tra dữ liệu hiện tại trong cột trang_thai
SELECT DISTINCT trang_thai, COUNT(*) as count
FROM SUPPLY_REQUEST 
GROUP BY trang_thai;

-- Kiểm tra cấu trúc cột trang_thai
SELECT 
    c.name AS column_name,
    c.is_nullable,
    c.column_default,
    t.name AS data_type,
    c.max_length
FROM sys.columns c
INNER JOIN sys.types t ON c.user_type_id = t.user_type_id
INNER JOIN sys.tables tb ON c.object_id = tb.object_id
WHERE tb.name = 'SUPPLY_REQUEST' 
AND c.name = 'trang_thai';

