package com.rest.repository;

import com.rest.models.ParkingTicket;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * In-memory repository for ParkingTicket records and Monthly Pass subscriptions.
 * Utilizes LinkedHashMap for O(1) key lookups and preserved issuance ordering.
 */
public class ParkingTicketRepository {

    private final Map<String, ParkingTicket> ticketStorage = new LinkedHashMap<>();
    private final Map<String, LocalDateTime> monthlyPassStorage = new LinkedHashMap<>();

    private String normalizeKey(String key) {
        return key != null ? key.trim().toUpperCase() : "";
    }

    public ParkingTicket save(ParkingTicket ticket) {
        if (ticket == null || ticket.getTicketId() == null) {
            throw new IllegalArgumentException("Cannot save null ticket or ticket without ID.");
        }
        ticketStorage.put(normalizeKey(ticket.getTicketId()), ticket);
        return ticket;
    }

    public Optional<ParkingTicket> findById(String ticketId) {
        if (ticketId == null) return Optional.empty();
        return Optional.ofNullable(ticketStorage.get(normalizeKey(ticketId)));
    }

    public Optional<ParkingTicket> findActiveByVehicleNumber(String vehicleNumber) {
        if (vehicleNumber == null) return Optional.empty();
        String normalized = normalizeKey(vehicleNumber);
        return ticketStorage.values().stream()
                .filter(t -> normalizeKey(t.getVehicleNumber()).equals(normalized) && "ACTIVE".equalsIgnoreCase(t.getStatus()))
                .findFirst();
    }

    public List<ParkingTicket> findByVehicleNumber(String vehicleNumber) {
        if (vehicleNumber == null) return new ArrayList<>();
        String normalized = normalizeKey(vehicleNumber);
        return ticketStorage.values().stream()
                .filter(t -> normalizeKey(t.getVehicleNumber()).equals(normalized))
                .collect(Collectors.toList());
    }

    public List<ParkingTicket> findAll() {
        return new ArrayList<>(ticketStorage.values());
    }

    public List<ParkingTicket> findAllActive() {
        return ticketStorage.values().stream()
                .filter(t -> "ACTIVE".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());
    }

    public List<ParkingTicket> findAllPaid() {
        return ticketStorage.values().stream()
                .filter(t -> "PAID".equalsIgnoreCase(t.getStatus()) || "LOST".equalsIgnoreCase(t.getStatus()))
                .collect(Collectors.toList());
    }

    public boolean update(ParkingTicket ticket) {
        if (ticket == null || ticket.getTicketId() == null) return false;
        String key = normalizeKey(ticket.getTicketId());
        if (ticketStorage.containsKey(key)) {
            ticketStorage.put(key, ticket);
            return true;
        }
        return false;
    }

    public boolean deleteById(String ticketId) {
        if (ticketId == null) return false;
        return ticketStorage.remove(normalizeKey(ticketId)) != null;
    }

    public int count() {
        return ticketStorage.size();
    }

    // --- Monthly Pass Management ---

    public void saveMonthlyPass(String vehicleNumber, LocalDateTime expiryDate) {
        if (vehicleNumber != null && expiryDate != null) {
            monthlyPassStorage.put(normalizeKey(vehicleNumber), expiryDate);
        }
    }

    public Optional<LocalDateTime> getMonthlyPassExpiry(String vehicleNumber) {
        if (vehicleNumber == null) return Optional.empty();
        return Optional.ofNullable(monthlyPassStorage.get(normalizeKey(vehicleNumber)));
    }

    public Map<String, LocalDateTime> getAllMonthlyPasses() {
        return new LinkedHashMap<>(monthlyPassStorage);
    }
}

