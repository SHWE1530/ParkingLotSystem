
package com.rest.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Reservation {

    private String reservationId;
    private String vehicleNumber;
    private String vehicleType;
    private String reservedSlot;
    private LocalDate reservationDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String reservationStatus;

    public Reservation() {
    }

    public Reservation(String reservationId,
                       String vehicleNumber,
                       String vehicleType,
                       String reservedSlot,
                       LocalDate reservationDate,
                       LocalTime startTime,
                       LocalTime endTime,
                       String reservationStatus) {

        this.reservationId = reservationId;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.reservedSlot = reservedSlot;
        this.reservationDate = reservationDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservationStatus = reservationStatus;
    }

package com.rest.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Represents an advance parking spot booking made by a customer.
 * Enables scheduled arrivals and pre-assigned parking slots.
 */
public class Reservation {
    private String reservationId;        // e.g. "RES-1001"
    private String vehicleNumber;        // License plate of vehicle
    private String slotId;               // Assigned parking slot identifier
    private String customerName;         // Name of the person booking
    private String contactNumber;        // Phone number
    private LocalDateTime startTime;     // Scheduled booking start
    private LocalDateTime endTime;       // Scheduled booking end
    private String status;               // "CONFIRMED", "CHECKED_IN", "CANCELLED", "EXPIRED"
    private LocalDateTime createdAt;     // Timestamp when booking was made

    public Reservation() {
        this.status = "CONFIRMED";
        this.createdAt = LocalDateTime.now();
    }

    public Reservation(String reservationId, String vehicleNumber, String slotId,
                       String customerName, String contactNumber,
                       LocalDateTime startTime, LocalDateTime endTime) {
        this.reservationId = reservationId;
        this.vehicleNumber = vehicleNumber;
        this.slotId = slotId;
        this.customerName = customerName;
        this.contactNumber = contactNumber;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "CONFIRMED";
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
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

    public String getReservedSlot() {
        return reservedSlot;
    }

    public void setReservedSlot(String reservedSlot) {
        this.reservedSlot = reservedSlot;
    }

    public LocalDate getReservationDate() {
        return reservationDate;
    }

    public void setReservationDate(LocalDate reservationDate) {
        this.reservationDate = reservationDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String reservationStatus) {
        this.reservationStatus = reservationStatus;
    public String getSlotId() {
        return slotId;
    }

    public void setSlotId(String slotId) {
        this.slotId = slotId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(
                reservationId != null ? reservationId.trim().toUpperCase() : null,
                that.reservationId != null ? that.reservationId.trim().toUpperCase() : null
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservationId != null ? reservationId.trim().toUpperCase() : null);
    }

    @Override
    public String toString() {
        return "\nReservation ID: " + reservationId +
                "\nVehicle Number: " + vehicleNumber +
                "\nVehicle Type: " + vehicleType +
                "\nReserved Slot: " + reservedSlot +
                "\nReservation Date: " + reservationDate +
                "\nStart Time: " + startTime +
                "\nEnd Time: " + endTime +
                "\nReservation Status: " + reservationStatus +
                "\n-----------------------------";
    }
}
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Reservation [" +
                "ID: " + reservationId +
                ", Vehicle: " + vehicleNumber +
                ", Slot: " + slotId +
                ", Customer: " + customerName +
                ", Phone: " + contactNumber +
                ", From: " + (startTime != null ? startTime.format(dtf) : "N/A") +
                ", To: " + (endTime != null ? endTime.format(dtf) : "N/A") +
                ", Status: " + status +
                ']';
    }
}

