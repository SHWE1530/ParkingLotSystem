
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
