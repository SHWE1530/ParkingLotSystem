package com.rest.repository;

import com.rest.models.ParkingRate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository layer for in-memory temporary storage of ParkingRate entities.
 * Keyed by rate ID, with fast lookup capabilities by vehicle category.
 */
public class ParkingRateRepository {

    private final Map<String, ParkingRate> rateStorage = new LinkedHashMap<>();

    private String normalizeKey(String key) {
        return key != null ? key.trim().toUpperCase() : "";
    }

    public ParkingRate save(ParkingRate rate) {
        if (rate == null || rate.getRateId() == null) {
            throw new IllegalArgumentException("Cannot save null rate or rate with null ID.");
        }
        rateStorage.put(normalizeKey(rate.getRateId()), rate);
        return rate;
    }

    public Optional<ParkingRate> findById(String rateId) {
        if (rateId == null) return Optional.empty();
        return Optional.ofNullable(rateStorage.get(normalizeKey(rateId)));
    }

    public Optional<ParkingRate> findByVehicleType(String vehicleType) {
        if (vehicleType == null) return Optional.empty();
        String normalizedType = normalizeKey(vehicleType);
        return rateStorage.values().stream()
                .filter(r -> normalizeKey(r.getVehicleType()).equalsIgnoreCase(normalizedType))
                .findFirst();
    }

    public List<ParkingRate> findAll() {
        return new ArrayList<>(rateStorage.values());
    }

    public boolean existsById(String rateId) {
        if (rateId == null) return false;
        return rateStorage.containsKey(normalizeKey(rateId));
    }

    public boolean existsByVehicleType(String vehicleType) {
        return findByVehicleType(vehicleType).isPresent();
    }

    public boolean update(ParkingRate rate) {
        if (rate == null || rate.getRateId() == null) return false;
        String key = normalizeKey(rate.getRateId());
        if (rateStorage.containsKey(key)) {
            rateStorage.put(key, rate);
            return true;
        }
        return false;
    }

    public boolean deleteById(String rateId) {
        if (rateId == null) return false;
        return rateStorage.remove(normalizeKey(rateId)) != null;
    }

    public int count() {
        return rateStorage.size();
    }

    public void clear() {
        rateStorage.clear();
    }
}

