package com.rest.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents the parking tariff/pricing structure per vehicle type.
 * Powers fee calculations in the ParkingPayment module.
 */
public class ParkingRate {
    private String rateId;               // e.g. "RATE-CAR", "RATE-BIKE"
    private String vehicleType;          // Vehicle category this rate applies to
    private double baseFee;              // Flat base fee (covers the initial 1 hour)
    private double hourlyRate;           // Cost per additional hour
    private double penaltyPerHour;       // Surcharge/penalty per hour for unauthorized overstay
    private LocalDateTime lastUpdated;   // Timestamp of rate creation or revision

    public ParkingRate() {
        this.lastUpdated = LocalDateTime.now();
    }

    public ParkingRate(String rateId, String vehicleType, double baseFee, double hourlyRate, double penaltyPerHour) {
        this.rateId = rateId;
        this.vehicleType = vehicleType;
        this.baseFee = baseFee;
        this.hourlyRate = hourlyRate;
        this.penaltyPerHour = penaltyPerHour;
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters and Setters

    public String getRateId() {
        return rateId;
    }

    public void setRateId(String rateId) {
        this.rateId = rateId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public double getBaseFee() {
        return baseFee;
    }

    public void setBaseFee(double baseFee) {
        this.baseFee = baseFee;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public double getPenaltyPerHour() {
        return penaltyPerHour;
    }

    public void setPenaltyPerHour(double penaltyPerHour) {
        this.penaltyPerHour = penaltyPerHour;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingRate that = (ParkingRate) o;
        return Objects.equals(
                rateId != null ? rateId.trim().toUpperCase() : null,
                that.rateId != null ? that.rateId.trim().toUpperCase() : null
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(rateId != null ? rateId.trim().toUpperCase() : null);
    }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "ParkingRate [" +
                "Rate ID: " + rateId +
                ", Type: " + vehicleType +
                ", Base Fee (1st hr): $" + String.format("%.2f", baseFee) +
                ", Hourly Rate: $" + String.format("%.2f", hourlyRate) + "/hr" +
                ", Penalty Rate: $" + String.format("%.2f", penaltyPerHour) + "/hr" +
                ", Last Updated: " + (lastUpdated != null ? lastUpdated.format(dtf) : "N/A") +
                ']';
    }
}

