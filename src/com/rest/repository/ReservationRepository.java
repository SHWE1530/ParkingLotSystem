package com.rest.repository;

import com.rest.models.Reservation;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Repository layer for in-memory temporary storage of Reservation entities.
 * Preserves insertion order and provides flexible query methods.
 */
public class ReservationRepository {

    private final Map<String, Reservation> reservationStorage = new LinkedHashMap<>();

    private String normalizeKey(String key) {
        return key != null ? key.trim().toUpperCase() : "";
    }

    public Reservation save(Reservation reservation) {
        if (reservation == null || reservation.getReservationId() == null) {
            throw new IllegalArgumentException("Cannot save null reservation or reservation with null ID.");
        }
        reservationStorage.put(normalizeKey(reservation.getReservationId()), reservation);
        return reservation;
    }

    public Optional<Reservation> findById(String reservationId) {
        if (reservationId == null) return Optional.empty();
        return Optional.ofNullable(reservationStorage.get(normalizeKey(reservationId)));
    }

    public List<Reservation> findByVehicleNumber(String vehicleNumber) {
        if (vehicleNumber == null) return new ArrayList<>();
        String normalized = normalizeKey(vehicleNumber);
        return reservationStorage.values().stream()
                .filter(r -> normalizeKey(r.getVehicleNumber()).equalsIgnoreCase(normalized))
                .collect(Collectors.toList());
    }

    public List<Reservation> findBySlotId(String slotId) {
        if (slotId == null) return new ArrayList<>();
        String normalized = normalizeKey(slotId);
        return reservationStorage.values().stream()
                .filter(r -> normalizeKey(r.getSlotId()).equalsIgnoreCase(normalized))
                .collect(Collectors.toList());
    }

    public List<Reservation> findAll() {
        return new ArrayList<>(reservationStorage.values());
    }

    public boolean existsById(String reservationId) {
        if (reservationId == null) return false;
        return reservationStorage.containsKey(normalizeKey(reservationId));
    }

    public boolean update(Reservation reservation) {
        if (reservation == null || reservation.getReservationId() == null) return false;
        String key = normalizeKey(reservation.getReservationId());
        if (reservationStorage.containsKey(key)) {
            reservationStorage.put(key, reservation);
            return true;
        }
        return false;
    }

    public boolean deleteById(String reservationId) {
        if (reservationId == null) return false;
        return reservationStorage.remove(normalizeKey(reservationId)) != null;
    }

    public int count() {
        return reservationStorage.size();
    }

    public void clear() {
        reservationStorage.clear();
    }
}

