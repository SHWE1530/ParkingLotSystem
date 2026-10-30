package com.rest.services;

import com.rest.models.Reservation;
import com.rest.repository.ReservationRepository;

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

