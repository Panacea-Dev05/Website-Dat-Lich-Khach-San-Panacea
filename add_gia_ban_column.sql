-- Add gia_ban column to INVENTORY_MANAGEMENT table
ALTER TABLE INVENTORY_MANAGEMENT 
ADD gia_ban DECIMAL(10,2);

-- Update existing records with default selling price (can be adjusted later)
-- Setting selling price to 1.2 times the purchase price as a starting point
UPDATE INVENTORY_MANAGEMENT 
SET gia_ban = gia_nhap * 1.2 
WHERE gia_nhap IS NOT NULL;

-- Add comment to the column
EXEC sp_addextendedproperty 
    @name = N'MS_Description', 
    @value = N'Giá bán của vật phẩm', 
    @level0type = N'SCHEMA', @level0name = N'dbo', 
    @level1type = N'TABLE', @level1name = N'INVENTORY_MANAGEMENT', 
    @level2type = N'COLUMN', @level2name = N'gia_ban';