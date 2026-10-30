package com.rest.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents a parking session ticket issued upon vehicle entry.
 * Tracks allocated floor and slot, entry/exit timestamps, fees, and payment status.
 */
public class ParkingTicket {

    private String ticketId;               // e.g., "TKT-1001"
    private String vehicleNumber;          // Registration number of vehicle
    private String vehicleType;            // Bike, Car, Truck, etc.
    private int floorNumber;               // Allocated floor level
    private int slotNumber;                // Allocated slot number
    private LocalDateTime entryTime;       // Entry timestamp
    private LocalDateTime exitTime;        // Exit timestamp
    private double fee;                    // Total parking fee
    private String status;                 // "ACTIVE", "PAID", "LOST", "PASS_HOLDER"
    private String paymentMethod;          // "CASH", "CARD", "UPI", "MONTHLY_PASS", "UNPAID"
    private boolean isMonthlyPass;         // Whether covered by monthly pass

    public ParkingTicket() {
        this.entryTime = LocalDateTime.now();
        this.status = "ACTIVE";
        this.paymentMethod = "UNPAID";
    }

    public ParkingTicket(String ticketId, String vehicleNumber, String vehicleType,
                         int floorNumber, int slotNumber) {
        this.ticketId = ticketId;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.floorNumber = floorNumber;
        this.slotNumber = slotNumber;
        this.entryTime = LocalDateTime.now();
        this.status = "ACTIVE";
        this.paymentMethod = "UNPAID";
        this.fee = 0.0;
        this.isMonthlyPass = false;
    }

    // Getters and Setters

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(int floorNumber) {
        this.floorNumber = floorNumber;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public boolean isMonthlyPass() {
        return isMonthlyPass;
    }

    public void setMonthlyPass(boolean monthlyPass) {
        isMonthlyPass = monthlyPass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParkingTicket that = (ParkingTicket) o;
        return Objects.equals(
                ticketId != null ? ticketId.trim().toUpperCase() : null,
                that.ticketId != null ? that.ticketId.trim().toUpperCase() : null
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(ticketId != null ? ticketId.trim().toUpperCase() : null);
    }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "ParkingTicket [" +
                "ID: " + ticketId +
                ", Vehicle: " + vehicleNumber + " (" + vehicleType + ")" +
                ", Location: Floor " + floorNumber + ", Slot " + slotNumber +
                ", Entry: " + (entryTime != null ? entryTime.format(dtf) : "N/A") +
                ", Exit: " + (exitTime != null ? exitTime.format(dtf) : "N/A") +
                ", Fee: $" + String.format("%.2f", fee) +
                ", Status: " + status +
                ", Payment: " + paymentMethod +
                ']';
    }
}

