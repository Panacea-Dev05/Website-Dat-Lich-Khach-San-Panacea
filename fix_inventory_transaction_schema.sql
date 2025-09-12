USE HotelBookingDB_LastStand_11;
GO

-- Script to fix INVENTORY_TRANSACTION table schema
-- This script adds all missing columns that the entity expects

PRINT '=== FIXING INVENTORY_TRANSACTION TABLE SCHEMA ===';
PRINT 'Adding missing columns to match InventoryTransaction entity...';

-- Add trang_thai_duyet column
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
               AND COLUMN_NAME = 'trang_thai_duyet')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION 
    ADD trang_thai_duyet NVARCHAR(20) DEFAULT 'CHO_DUYET';
    
    PRINT '✓ Added trang_thai_duyet column';
END
ELSE
BEGIN
    PRINT '✓ trang_thai_duyet column already exists';
END
GO

-- Add admin_duyet_id column
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
               AND COLUMN_NAME = 'admin_duyet_id')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION 
    ADD admin_duyet_id INT;
    
    PRINT '✓ Added admin_duyet_id column';
END
ELSE
BEGIN
    PRINT '✓ admin_duyet_id column already exists';
END
GO

-- Add ngay_duyet column
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
               AND COLUMN_NAME = 'ngay_duyet')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION 
    ADD ngay_duyet DATETIME2;
    
    PRINT '✓ Added ngay_duyet column';
END
ELSE
BEGIN
    PRINT '✓ ngay_duyet column already exists';
END
GO

-- Add ghi_chu_duyet column
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
               AND COLUMN_NAME = 'ghi_chu_duyet')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION 
    ADD ghi_chu_duyet NVARCHAR(500);
    
    PRINT '✓ Added ghi_chu_duyet column';
END
ELSE
BEGIN
    PRINT '✓ ghi_chu_duyet column already exists';
END
GO

-- Add khach_san_id column
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.COLUMNS 
               WHERE TABLE_NAME = 'INVENTORY_TRANSACTION' 
               AND COLUMN_NAME = 'khach_san_id')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION 
    ADD khach_san_id INT NOT NULL DEFAULT 1;
    
    PRINT '✓ Added khach_san_id column';
END
ELSE
BEGIN
    PRINT '✓ khach_san_id column already exists';
END
GO

-- Add foreign key constraint for khach_san_id
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
               WHERE CONSTRAINT_NAME = 'FK_INVENTORY_TRANSACTION_HOTEL')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION
    ADD CONSTRAINT FK_INVENTORY_TRANSACTION_HOTEL
    FOREIGN KEY (khach_san_id) REFERENCES HOTEL(id);
    
    PRINT '✓ Added foreign key constraint for khach_san_id';
END
ELSE
BEGIN
    PRINT '✓ Foreign key constraint for khach_san_id already exists';
END
GO

-- Update existing data to set khach_san_id = 1 for any NULL values
UPDATE INVENTORY_TRANSACTION 
SET khach_san_id = 1 
WHERE khach_san_id IS NULL;

PRINT '✓ Updated existing records with default khach_san_id';

-- Add check constraint for trang_thai_duyet values
IF NOT EXISTS (SELECT * FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS 
               WHERE CONSTRAINT_NAME = 'CHK_INVENTORY_TRANSACTION_TRANG_THAI_DUYET')
BEGIN
    ALTER TABLE INVENTORY_TRANSACTION
    ADD CONSTRAINT CHK_INVENTORY_TRANSACTION_TRANG_THAI_DUYET
    CHECK (trang_thai_duyet IN ('CHO_DUYET', 'DA_DUYET', 'TU_CHOI'));
    
    PRINT '✓ Added check constraint for trang_thai_duyet values';
END
ELSE
BEGIN
    PRINT '✓ Check constraint for trang_thai_duyet already exists';
END
GO

PRINT '';
PRINT '=== INVENTORY_TRANSACTION SCHEMA FIX COMPLETED ===';
PRINT 'All required columns have been added to match the entity definition.';
PRINT 'You can now restart the Spring Boot application.';

