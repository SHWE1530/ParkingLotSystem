
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer containing reservation booking logic, temporal conflict checks,
 * check-in workflows, and status lifecycle management.
 */
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService() {
        this.reservationRepository = new ReservationRepository();
    }

    public ReservationService(ReservationRepository reservationRepository) {
        if (reservationRepository == null) {
            throw new IllegalArgumentException("ReservationRepository cannot be null.");
        }
        this.reservationRepository = reservationRepository;
    }

    /**
     * Creates an advance reservation after verifying temporal slot availability.
     *
     * @param reservationId  Unique ID (e.g. RES-101)
     * @param vehicleNumber  Vehicle license plate
     * @param slotId         Target parking slot ID
     * @param customerName   Name of customer
     * @param contactNumber  Phone number
     * @param startTime      Booking start timestamp
     * @param endTime        Booking end timestamp
     * @return true if booked successfully, false if conflict or validation fails
     */
    public boolean createReservation(String reservationId, String vehicleNumber, String slotId,
                                     String customerName, String contactNumber,
                                     LocalDateTime startTime, LocalDateTime endTime) {

        if (!isValidString(reservationId) || !isValidString(vehicleNumber) ||
                !isValidString(slotId) || !isValidString(customerName) || !isValidString(contactNumber)) {
            System.err.println("[Validation Error] All reservation fields are mandatory.");
            return false;
        }

        if (startTime == null || endTime == null) {
            System.err.println("[Validation Error] Reservation start and end time are required.");
            return false;
        }

        if (endTime.isBefore(startTime) || endTime.isEqual(startTime)) {
            System.err.println("[Validation Error] Reservation end time must be strictly after start time.");
            return false;
        }

        String normalizedResId = reservationId.trim().toUpperCase();
        if (reservationRepository.existsById(normalizedResId)) {
            System.err.println("[Business Rule Error] Reservation ID '" + normalizedResId + "' already exists.");
            return false;
        }

        // Check for conflicting reservations for this slot during the given time window
        if (!isSlotAvailableForInterval(slotId, startTime, endTime)) {
            System.err.println("[Conflict Error] Slot '" + slotId + "' is already reserved by another vehicle during this time period.");
            return false;
        }

        Reservation res = new Reservation(
                normalizedResId,
                vehicleNumber.trim().toUpperCase(),
                slotId.trim().toUpperCase(),
                customerName.trim(),
                contactNumber.trim(),
                startTime,
                endTime
        );

        reservationRepository.save(res);
        return true;
    }

    /**
     * Checks if a parking slot is free from overlapping reservations in a specified time window.
     */
    public boolean isSlotAvailableForInterval(String slotId, LocalDateTime targetStart, LocalDateTime targetEnd) {
        List<Reservation> existingList = reservationRepository.findBySlotId(slotId);

        for (Reservation r : existingList) {
            // Only active/confirmed reservations count towards slot conflict
            if ("CONFIRMED".equalsIgnoreCase(r.getStatus()) || "CHECKED_IN".equalsIgnoreCase(r.getStatus())) {
                boolean overlap = targetStart.isBefore(r.getEndTime()) && targetEnd.isAfter(r.getStartTime());
                if (overlap) {
                    return false;
                }
            }
        }
        return true;
    }

    public Optional<Reservation> getReservation(String reservationId) {
        if (!isValidString(reservationId)) return Optional.empty();
        return reservationRepository.findById(reservationId.trim().toUpperCase());
    }

    public List<Reservation> getReservationsByVehicle(String vehicleNumber) {
        if (!isValidString(vehicleNumber)) return List.of();
        return reservationRepository.findByVehicleNumber(vehicleNumber.trim().toUpperCase());
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    /**
     * Checks in a vehicle against an active reservation upon arrival.
     */
    public boolean checkInReservation(String reservationId) {
        Optional<Reservation> resOpt = getReservation(reservationId);
        if (resOpt.isEmpty()) {
            System.err.println("[Error] Reservation not found: " + reservationId);
            return false;
        }

        Reservation res = resOpt.get();
        if (!"CONFIRMED".equalsIgnoreCase(res.getStatus())) {
            System.err.println("[Business Rule Error] Cannot check-in reservation with status: " + res.getStatus());
            return false;
        }

        res.setStatus("CHECKED_IN");
        return reservationRepository.update(res);
    }

    /**
     * Cancels an existing reservation.
     */
    public boolean cancelReservation(String reservationId) {
        Optional<Reservation> resOpt = getReservation(reservationId);
        if (resOpt.isEmpty()) {
            System.err.println("[Error] Reservation not found: " + reservationId);
            return false;
        }

        Reservation res = resOpt.get();
        if ("COMPLETED".equalsIgnoreCase(res.getStatus()) || "CANCELLED".equalsIgnoreCase(res.getStatus())) {
            System.err.println("[Business Rule Error] Reservation is already " + res.getStatus());
            return false;
        }

        res.setStatus("CANCELLED");
        return reservationRepository.update(res);
    }

    public ReservationRepository getReservationRepository() {
        return reservationRepository;
    }

    private boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }
}

