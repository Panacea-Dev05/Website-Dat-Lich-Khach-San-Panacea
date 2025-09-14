package panacea.website_dat_lich_khach_san.dto;

public class FloorDTO {
    private Byte floorNumber;
    private int totalRooms;
    private int availableRooms;
    private int occupiedRooms;
    private int maintenanceRooms;

    // Constructors
    public FloorDTO() {}

    public FloorDTO(Byte floorNumber, int totalRooms, int availableRooms, int occupiedRooms, int maintenanceRooms) {
        this.floorNumber = floorNumber;
        this.totalRooms = totalRooms;
        this.availableRooms = availableRooms;
        this.occupiedRooms = occupiedRooms;
        this.maintenanceRooms = maintenanceRooms;
    }

    // Getters and Setters
    public Byte getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Byte floorNumber) {
        this.floorNumber = floorNumber;
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(int totalRooms) {
        this.totalRooms = totalRooms;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(int availableRooms) {
        this.availableRooms = availableRooms;
    }

    public int getOccupiedRooms() {
        return occupiedRooms;
    }

    public void setOccupiedRooms(int occupiedRooms) {
        this.occupiedRooms = occupiedRooms;
    }

    public int getMaintenanceRooms() {
        return maintenanceRooms;
    }

    public void setMaintenanceRooms(int maintenanceRooms) {
        this.maintenanceRooms = maintenanceRooms;
    }

    // Utility methods
    public double getOccupancyRate() {
        if (totalRooms == 0) {
            return 0.0;
        }
        return Math.round((double) occupiedRooms / totalRooms * 100.0 * 100.0) / 100.0;
    }

    public double getAvailabilityRate() {
        if (totalRooms == 0) {
            return 0.0;
        }
        return Math.round((double) availableRooms / totalRooms * 100.0 * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return "FloorDTO{" +
                "floorNumber=" + floorNumber +
                ", totalRooms=" + totalRooms +
                ", availableRooms=" + availableRooms +
                ", occupiedRooms=" + occupiedRooms +
                ", maintenanceRooms=" + maintenanceRooms +
                ", occupancyRate=" + getOccupancyRate() + "%" +
                '}';
    }
}