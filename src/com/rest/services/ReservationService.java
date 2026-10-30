
package com.rest.services;

import com.rest.models.Reservation;
import com.rest.repository.ReservationRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class ReservationService {

    private ReservationRepository repository;

    private AtomicLong reservationCounter = new AtomicLong(1000);

    public ReservationService(ReservationRepository repository) {
        this.repository = repository;
    }

    // Generate unique reservation ID
    private String generateReservationId() {

        return "RES-" + reservationCounter.incrementAndGet();
    }

    // Check whether time periods overlap
    private boolean isOverlapping(
            LocalTime newStart,
            LocalTime newEnd,
            LocalTime existingStart,
            LocalTime existingEnd) {

        return newStart.isBefore(existingEnd)
                && newEnd.isAfter(existingStart);
    }

    // Check duplicate reservations
    private boolean hasConflict(
            String vehicleNumber,
            String reservedSlot,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {

        List<Reservation> reservations = repository.findAll();

        for (Reservation existing : reservations) {

            if (!"CONFIRMED".equals(existing.getReservationStatus())) {
                continue;
            }

            if (!existing.getReservationDate().equals(date)) {
                continue;
            }

            boolean overlap = isOverlapping(
                    startTime,
                    endTime,
                    existing.getStartTime(),
                    existing.getEndTime()
            );

            if (!overlap) {
                continue;
            }

            // Same vehicle cannot reserve overlapping time
            if (existing.getVehicleNumber()
                    .equalsIgnoreCase(vehicleNumber)) {
                System.out.println(
                        "Vehicle already has a reservation during this time.");
                return true;
            }

            // Same slot cannot be reserved by another vehicle
            if (existing.getReservedSlot()
                    .equalsIgnoreCase(reservedSlot)) {
                System.out.println(
                        "Parking slot is already reserved during this time.");
                return true;
            }
        }

        return false;
    }

    // Create reservation
    public Reservation createReservation(
            String vehicleNumber,
            String vehicleType,
            String reservedSlot,
            LocalDate date,
            LocalTime startTime,
            LocalTime endTime) {

        // Validate empty values
        if (vehicleNumber == null || vehicleNumber.isBlank()
                || vehicleType == null || vehicleType.isBlank()
                || reservedSlot == null || reservedSlot.isBlank()
                || date == null || startTime == null || endTime == null) {

            System.out.println("All fields are required.");
            return null;
        }

        // Validate reservation date
        if (date.isBefore(LocalDate.now())) {

            System.out.println("Reservation date cannot be in the past.");
            return null;
        }

        // Validate start and end times
        if (!startTime.isBefore(endTime)) {

            System.out.println("End time must be after start time.");
            return null;
        }

        // Validate start time for today's reservations
        if (date.equals(LocalDate.now())
                && !startTime.isAfter(LocalTime.now())) {

            System.out.println("Start time must be in the future.");
            return null;
        }

        // Check overlapping reservations
        if (hasConflict(
                vehicleNumber,
                reservedSlot,
                date,
                startTime,
                endTime)) {

            return null;
        }

        // Create reservation
        String id = generateReservationId();

        Reservation reservation = new Reservation(
                id,
                vehicleNumber,
                vehicleType,
                reservedSlot,
                date,
                startTime,
                endTime,
                "CONFIRMED"
        );

        repository.add(reservation);

        System.out.println("Reservation created successfully!");
        System.out.println("Reservation ID: " + id);

        return reservation;
    }

    // Search by reservation ID
    public Reservation searchById(String id) {

        return repository.findById(id);
    }

    // Search by vehicle number
    public List<Reservation> searchByVehicle(String vehicleNumber) {

        return repository.findByVehicleNumber(vehicleNumber);
    }

    // Display all reservations
    public List<Reservation> getAllReservations() {

        return repository.findAll();
    }

    // Display confirmed reservations
    public List<Reservation> getActiveReservations() {

        return repository.findActive();
    }

    // Display cancelled reservations
    public List<Reservation> getCancelledReservations() {

        return repository.findCancelled();
    }

    // Display completed reservations
    public List<Reservation> getCompletedReservations() {

        return repository.findCompleted();
    }

    // Cancel reservation
    public boolean cancelReservation(String id) {

        Reservation reservation = repository.findById(id);

        if (reservation == null) {
            System.out.println("Reservation not found.");
            return false;
        }

        if (!"CONFIRMED".equals(reservation.getReservationStatus())) {

            System.out.println(
                    "Only confirmed reservations can be cancelled.");
            return false;
        }

        reservation.setReservationStatus("CANCELLED");

        System.out.println("Reservation cancelled successfully.");

        return true;
    }

    // Mark reservation as completed
    public boolean completeReservation(String id) {

        Reservation reservation = repository.findById(id);

        if (reservation == null) {
            System.out.println("Reservation not found.");
            return false;
        }

        if (!"CONFIRMED".equals(reservation.getReservationStatus())) {

            System.out.println(
                    "Only confirmed reservations can be completed.");
            return false;
        }

        reservation.setReservationStatus("COMPLETED");

        System.out.println("Reservation completed successfully.");

        return true;
    }

    // Automatically complete expired reservations
    public void updateCompletedReservations() {

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        for (Reservation reservation : repository.findActive()) {

            boolean expired = reservation.getReservationDate()
                    .isBefore(today)
                    || (reservation.getReservationDate().equals(today)
                    && !reservation.getEndTime().isAfter(now));

            if (expired) {
                reservation.setReservationStatus("COMPLETED");
            }
        }
    }
}
