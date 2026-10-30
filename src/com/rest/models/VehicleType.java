package com.rest.models;

/**
 * Standardized vehicle categories supported by the Parking Lot Management System.
 * Enforces type safety across Vehicle, ParkingSlot, ParkingRate, and Reservation.
 */
public enum VehicleType {
    CAR("Standard Car", "SLOT_MEDIUM"),
    BIKE("Two-Wheeler / Motorcycle", "SLOT_SMALL"),
    SUV("SUV / Van", "SLOT_LARGE"),
    TRUCK("Heavy Vehicle / Truck", "SLOT_XLARGE"),
    ELECTRIC("Electric Vehicle (EV)", "SLOT_EV_CHARGING");

    private final String description;
    private final String recommendedSlotSize;

    VehicleType(String description, String recommendedSlotSize) {
        this.description = description;
        this.recommendedSlotSize = recommendedSlotSize;
    }

    public String getDescription() {
        return description;
    }

    public String getRecommendedSlotSize() {
        return recommendedSlotSize;
    }

    /**
     * Safely parses a string into a VehicleType ignoring casing and whitespace.
     *
     * @param typeStr Raw string name.
     * @return Matching VehicleType, or null if invalid.
     */
    public static VehicleType fromString(String typeStr) {
        if (typeStr == null || typeStr.trim().isEmpty()) {
            return null;
        }
        String clean = typeStr.trim().toUpperCase();
        for (VehicleType vt : values()) {
            if (vt.name().equalsIgnoreCase(clean)) {
                return vt;
            }
        }
        return null;
    }
}

