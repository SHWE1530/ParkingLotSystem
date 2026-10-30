package com.rest.repository;

import com.rest.models.Vehicle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository layer responsible for temporary in-memory data storage of vehicles.
 * Utilizes a LinkedHashMap for O(1) key-based lookups and preserved insertion order,
 * returning ArrayList collections for batch operations.
 */
public class VehicleRepository {

    // In-memory vehicle storage: Key is the normalized vehicle registration number (uppercase)
    private final Map<String, Vehicle> vehicleStorage = new LinkedHashMap<>();

    /**
     * Normalizes the registration number to uppercase and trimmed format for uniform indexing.
     *
     * @param vehicleNumber Raw registration number.
     * @return Normalized registration number.
     */
    private String normalizeKey(String vehicleNumber) {
        return vehicleNumber != null ? vehicleNumber.trim().toUpperCase() : "";
    }

    /**
     * Saves a new vehicle into storage.
     *
     * @param vehicle The vehicle to store.
     * @return The stored Vehicle object.
     */
    public Vehicle save(Vehicle vehicle) {
        if (vehicle == null || vehicle.getVehicleNumber() == null) {
            throw new IllegalArgumentException("Cannot save null vehicle or vehicle with null registration number.");
        }
        String key = normalizeKey(vehicle.getVehicleNumber());
        vehicleStorage.put(key, vehicle);
        return vehicle;
    }

    /**
     * Finds a vehicle by its registration number.
     *
     * @param vehicleNumber Vehicle registration number.
     * @return An Optional containing the Vehicle if found, or empty Optional otherwise.
     */
    public Optional<Vehicle> findByVehicleNumber(String vehicleNumber) {
        if (vehicleNumber == null) {
            return Optional.empty();
        }
        String key = normalizeKey(vehicleNumber);
        return Optional.ofNullable(vehicleStorage.get(key));
    }

    /**
     * Retrieves all registered vehicles.
     *
     * @return An ArrayList containing all registered vehicles in order of registration.
     */
    public List<Vehicle> findAll() {
        return new ArrayList<>(vehicleStorage.values());
    }

    /**
     * Checks if a vehicle exists with the given registration number.
     *
     * @param vehicleNumber Vehicle registration number.
     * @return true if the vehicle exists, false otherwise.
     */
    public boolean existsByVehicleNumber(String vehicleNumber) {
        if (vehicleNumber == null) {
            return false;
        }
        return vehicleStorage.containsKey(normalizeKey(vehicleNumber));
    }

    /**
     * Updates an existing vehicle's details in storage.
     *
     * @param vehicle Updated vehicle object.
     * @return true if the vehicle was found and updated, false otherwise.
     */
    public boolean update(Vehicle vehicle) {
        if (vehicle == null || vehicle.getVehicleNumber() == null) {
            return false;
        }
        String key = normalizeKey(vehicle.getVehicleNumber());
        if (vehicleStorage.containsKey(key)) {
            vehicleStorage.put(key, vehicle);
            return true;
        }
        return false;
    }

    /**
     * Deletes a vehicle by its registration number.
     *
     * @param vehicleNumber Vehicle registration number.
     * @return true if vehicle was found and removed, false otherwise.
     */
    public boolean deleteByVehicleNumber(String vehicleNumber) {
        if (vehicleNumber == null) {
            return false;
        }
        String key = normalizeKey(vehicleNumber);
        return vehicleStorage.remove(key) != null;
    }

    /**
     * Returns the total count of registered vehicles.
     *
     * @return count of vehicles.
     */
    public int count() {
        return vehicleStorage.size();
    }

    /**
     * Clears all stored vehicles (helpful for testing or resetting state).
     */
    public void clear() {
        vehicleStorage.clear();
    }
}

