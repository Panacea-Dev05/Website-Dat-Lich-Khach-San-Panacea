package panacea.website_dat_lich_khach_san.enums;

/**
 * Enum định nghĩa các loại tầng đặc biệt trong khách sạn
 */
public enum FloorType {
    BASEMENT("Tầng hầm", -1),
    GROUND("Tầng trệt", 0),
    MEZZANINE("Tầng lửng", 0.5),
    REGULAR("Tầng thường", 1),
    PENTHOUSE("Tầng áp mái", 99),
    ROOFTOP("Tầng mái", 100);
    
    private final String displayName;
    private final double floorLevel;
    
    FloorType(String displayName, double floorLevel) {
        this.displayName = displayName;
        this.floorLevel = floorLevel;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public double getFloorLevel() {
        return floorLevel;
    }
    
    /**
     * Kiểm tra xem một số tầng có phải là tầng đặc biệt không
     * @param floorNumber Số tầng cần kiểm tra
     * @return FloorType tương ứng hoặc REGULAR nếu là tầng thường
     */
    public static FloorType getFloorType(Byte floorNumber) {
        if (floorNumber == null) {
            return REGULAR;
        }
        
        switch (floorNumber) {
            case -1:
                return BASEMENT;
            case 0:
                return GROUND;
            case 99:
                return PENTHOUSE;
            case 100:
                return ROOFTOP;
            default:
                return REGULAR;
        }
    }
    
    /**
     * Lấy danh sách các tầng hợp lệ
     * @return Mảng các số tầng hợp lệ
     */
    public static Byte[] getValidFloorNumbers() {
        return new Byte[]{-1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 
                         11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
                         21, 22, 23, 24, 25, 26, 27, 28, 29, 30,
                         31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
                         41, 42, 43, 44, 45, 46, 47, 48, 49, 50,
                         99, 100};
    }
    
    /**
     * Kiểm tra xem số tầng có hợp lệ không
     * @param floorNumber Số tầng cần kiểm tra
     * @return true nếu hợp lệ, false nếu không
     */
    public static boolean isValidFloor(Byte floorNumber) {
        if (floorNumber == null) {
            return false;
        }
        
        // Cho phép tầng hầm (-1), tầng trệt (0), tầng thường (1-50), tầng áp mái (99), tầng mái (100)
        return floorNumber == -1 || floorNumber == 0 || 
               (floorNumber >= 1 && floorNumber <= 50) || 
               floorNumber == 99 || floorNumber == 100;
    }
}