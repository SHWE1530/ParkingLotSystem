package com.rest.models;

import java.util.Objects;

/**
 * Represents a physical floor or level inside a multi-story Parking Lot.
 * Bridges ParkingLot and individual ParkingSlots.
 */
public class ParkingFloor {
    private String floorId;        // e.g. "FL-1", "FL-B1"
    private int floorNumber;       // e.g. 1, 2, or -1 for basement
    private String floorName;      // e.g. "Ground Floor", "Basement 1"
    private int maxCapacity;       // Total number of parking slots on this floor
    private int occupiedSlots;     // Current number of slots in use
    private boolean isActive;      // true if open for parking, false if under maintenance

    /**
     * Default constructor.
     */
    public ParkingFloor() {
        this.isActive = true;
    }

    /**
     * Parameterized constructor.
     *
     * @param floorId     Unique identifier for the floor.
     * @param floorNumber Numerical floor level.
     * @param floorName   Descriptive label.
     * @param maxCapacity Total slot capacity.
     */
    public ParkingFloor(String floorId, int floorNumber, String floorName, int maxCapacity) {
        this.floorId = floorId;
        this.floorNumber = floorNumber;
        this.floorName = floorName;
        this.maxCapacity = maxCapacity;
        this.occupiedSlots = 0;
        this.isActive = true;
    }

    // Getters and Setters

    public String getFloorId() {
        return floorId;
    }

    public void setFloorId(String floorId) {
        this.floorId = floorId;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public int getOccupiedSlots() {
        return occupiedSlots;
    }

    public void setOccupiedSlots(int occupiedSlots) {
        this.occupiedSlots = occupiedSlots;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Calculates currently available free slots on this floor.
     */
    public int getAvailableSlots() {
        return Math.max(0, maxCapacity - occupiedSlots);
    }

    /**
     * Checks if this floor has remaining free slots.
     */
    public boolean hasAvailableSlot() {
        return isActive && getAvailableSlots() > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingFloor that = (ParkingFloor) o;
        return Objects.equals(
                floorId != null ? floorId.trim().toUpperCase() : null,
                that.floorId != null ? that.floorId.trim().toUpperCase() : null
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(floorId != null ? floorId.trim().toUpperCase() : null);
    }

    @Override
    public String toString() {
        return "ParkingFloor [" +
                "ID: " + floorId +
                ", Level: " + floorNumber +
                ", Name: '" + floorName + '\'' +
                ", Capacity: " + occupiedSlots + "/" + maxCapacity +
                ", Available: " + getAvailableSlots() +
                ", Status: " + (isActive ? "ACTIVE" : "MAINTENANCE") +
                ']';
    }
}

