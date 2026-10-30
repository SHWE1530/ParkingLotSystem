package com.rest.repository;

import com.rest.models.ParkingFloor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository layer for in-memory temporary storage of ParkingFloor entities.
 * Uses a LinkedHashMap for fast O(1) lookups by floor ID while preserving floor addition order.
 */
public class ParkingFloorRepository {

    private final Map<String, ParkingFloor> floorStorage = new LinkedHashMap<>();

    private String normalizeKey(String floorId) {
        return floorId != null ? floorId.trim().toUpperCase() : "";
    }

    public ParkingFloor save(ParkingFloor floor) {
        if (floor == null || floor.getFloorId() == null) {
            throw new IllegalArgumentException("Cannot save a null floor or floor with null ID.");
        }
        String key = normalizeKey(floor.getFloorId());
        floorStorage.put(key, floor);
        return floor;
    }

    public Optional<ParkingFloor> findById(String floorId) {
        if (floorId == null) return Optional.empty();
        return Optional.ofNullable(floorStorage.get(normalizeKey(floorId)));
    }

    public Optional<ParkingFloor> findByFloorNumber(int floorNumber) {
        return floorStorage.values().stream()
                .filter(f -> f.getFloorNumber() == floorNumber)
                .findFirst();
    }

    public List<ParkingFloor> findAll() {
        return new ArrayList<>(floorStorage.values());
    }

    public boolean existsById(String floorId) {
        if (floorId == null) return false;
        return floorStorage.containsKey(normalizeKey(floorId));
    }

    public boolean existsByFloorNumber(int floorNumber) {
        return floorStorage.values().stream()
                .anyMatch(f -> f.getFloorNumber() == floorNumber);
    }

    public boolean update(ParkingFloor floor) {
        if (floor == null || floor.getFloorId() == null) return false;
        String key = normalizeKey(floor.getFloorId());
        if (floorStorage.containsKey(key)) {
            floorStorage.put(key, floor);
            return true;
        }
        return false;
    }

    public boolean deleteById(String floorId) {
        if (floorId == null) return false;
        return floorStorage.remove(normalizeKey(floorId)) != null;
    }

    public int count() {
        return floorStorage.size();
    }

    public void clear() {
        floorStorage.clear();
    }
}

