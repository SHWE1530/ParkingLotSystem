package com.rest.models;

import java.util.Objects;

/**
 * Represents a Vehicle in the Parking Lot Management System.
 * Encapsulates the vehicle registration number, owner name, and vehicle type.
 */
public class Vehicle {
    private String vehicleNumber;
    private String ownerName;
    private String vehicleType;

    /**
     * Default constructor.
     */
    public Vehicle() {
    }

    /**
     * Parameterized constructor.
     *
     * @param vehicleNumber Unique registration/license plate number of the vehicle.
     * @param ownerName     Full name of the vehicle owner.
     * @param vehicleType   Type of the vehicle (e.g., Car, Bike, SUV, Truck).
     */
    public Vehicle(String vehicleNumber, String ownerName, String vehicleType) {
        this.vehicleNumber = vehicleNumber;
        this.ownerName = ownerName;
        this.vehicleType = vehicleType;
    }

    // Getters and Setters

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return Objects.equals(
                vehicleNumber != null ? vehicleNumber.trim().toUpperCase() : null,
                vehicle.vehicleNumber != null ? vehicle.vehicleNumber.trim().toUpperCase() : null
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleNumber != null ? vehicleNumber.trim().toUpperCase() : null);
    }

    @Override
    public String toString() {
        return "Vehicle [" +
                "Registration No: " + vehicleNumber +
                ", Owner: " + ownerName +
                ", Type: " + vehicleType +
                ']';
    }
}

