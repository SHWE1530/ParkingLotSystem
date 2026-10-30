package com.rest.services;

import com.rest.models.Vehicle;
import com.rest.repository.VehicleRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service layer containing core business logic and validations for Vehicle Management.
 * Sits between the CLI/UI controllers and the Repository layer.
 */
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    /**
     * Default constructor initializing with a new VehicleRepository instance.
     */
    public VehicleService() {
        this.vehicleRepository = new VehicleRepository();
    }

    /**
     * Parameterized constructor allowing repository dependency injection.
     * Useful for integration across modules or unit testing.
     *
     * @param vehicleRepository Repository instance to use.
     */
    public VehicleService(VehicleRepository vehicleRepository) {
        if (vehicleRepository == null) {
            throw new IllegalArgumentException("VehicleRepository cannot be null.");
        }
        this.vehicleRepository = vehicleRepository;
    }

    /**
     * Registers a new vehicle into the system after validating inputs and checking for duplicates.
     *
     * @param vehicleNumber Vehicle registration number.
     * @param ownerName     Owner's full name.
     * @param vehicleType   Vehicle category/type.
     * @return true if successfully added, false if validation fails or vehicle already exists.
     */
    public boolean addVehicle(String vehicleNumber, String ownerName, String vehicleType) {
        if (!isValidString(vehicleNumber) || !isValidString(ownerName) || !isValidString(vehicleType)) {
            System.err.println("[Validation Error] Registration number, owner name, and vehicle type cannot be empty.");
            return false;
        }

        String normalizedNumber = vehicleNumber.trim().toUpperCase();

        // Business Rule: Registration number must be unique
        if (vehicleRepository.existsByVehicleNumber(normalizedNumber)) {
            System.err.println("[Business Rule Error] Vehicle with number '" + normalizedNumber + "' is already registered.");
            return false;
        }

        Vehicle newVehicle = new Vehicle(normalizedNumber, ownerName.trim(), vehicleType.trim());
        vehicleRepository.save(newVehicle);
        return true;
    }

    /**
     * Overloaded method to add a pre-instantiated Vehicle object.
     *
     * @param vehicle Vehicle instance to add.
     * @return true if successfully added, false otherwise.
     */
    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            System.err.println("[Validation Error] Vehicle object cannot be null.");
            return false;
        }
        return addVehicle(vehicle.getVehicleNumber(), vehicle.getOwnerName(), vehicle.getVehicleType());
    }

    /**
     * Searches for a vehicle by registration number.
     *
     * @param vehicleNumber Registration number to search.
     * @return Optional containing the Vehicle if found, or empty Optional.
     */
    public Optional<Vehicle> searchVehicle(String vehicleNumber) {
        if (!isValidString(vehicleNumber)) {
            System.err.println("[Validation Error] Vehicle registration number cannot be blank.");
            return Optional.empty();
        }
        return vehicleRepository.findByVehicleNumber(vehicleNumber.trim().toUpperCase());
    }

    /**
     * Retrieves all registered vehicles.
     *
     * @return List of all registered vehicles.
     */
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    /**
     * Updates details (owner name and/or vehicle type) of an existing vehicle.
     *
     * @param vehicleNumber Registration number of the vehicle to update.
     * @param newOwnerName  New owner name (or null/blank to keep current).
     * @param newVehicleType New vehicle type (or null/blank to keep current).
     * @return true if vehicle was found and updated, false otherwise.
     */
    public boolean updateVehicle(String vehicleNumber, String newOwnerName, String newVehicleType) {
        if (!isValidString(vehicleNumber)) {
            System.err.println("[Validation Error] Vehicle registration number cannot be blank.");
            return false;
        }

        String normalizedNumber = vehicleNumber.trim().toUpperCase();
        Optional<Vehicle> existingOpt = vehicleRepository.findByVehicleNumber(normalizedNumber);

        if (existingOpt.isEmpty()) {
            System.err.println("[Error] No vehicle found with registration number: " + normalizedNumber);
            return false;
        }

        Vehicle existingVehicle = existingOpt.get();

        boolean updated = false;
        if (isValidString(newOwnerName)) {
            existingVehicle.setOwnerName(newOwnerName.trim());
            updated = true;
        }

        if (isValidString(newVehicleType)) {
            existingVehicle.setVehicleType(newVehicleType.trim());
            updated = true;
        }

        if (!updated) {
            System.err.println("[Warning] No new values provided to update.");
            return false;
        }

        return vehicleRepository.update(existingVehicle);
    }

    /**
     * Deletes a vehicle by its registration number.
     *
     * @param vehicleNumber Registration number of the vehicle to delete.
     * @return true if successfully removed, false if not found.
     */
    public boolean deleteVehicle(String vehicleNumber) {
        if (!isValidString(vehicleNumber)) {
            System.err.println("[Validation Error] Vehicle registration number cannot be blank.");
            return false;
        }

        String normalizedNumber = vehicleNumber.trim().toUpperCase();

        if (!vehicleRepository.existsByVehicleNumber(normalizedNumber)) {
            System.err.println("[Error] Vehicle with number '" + normalizedNumber + "' does not exist.");
            return false;
        }

        return vehicleRepository.deleteByVehicleNumber(normalizedNumber);
    }

    /**
     * Verifies if a vehicle is registered in the system.
     *
     * @param vehicleNumber Registration number to verify.
     * @return true if registered, false otherwise.
     */
    public boolean isVehicleRegistered(String vehicleNumber) {
        if (!isValidString(vehicleNumber)) {
            return false;
        }
        return vehicleRepository.existsByVehicleNumber(vehicleNumber.trim().toUpperCase());
    }

    /**
     * Gets the total number of registered vehicles.
     *
     * @return count of vehicles.
     */
    public int getTotalCount() {
        return vehicleRepository.count();
    }

    /**
     * Returns the underlying repository instance for cross-module integration.
     *
     * @return The VehicleRepository instance.
     */
    public VehicleRepository getVehicleRepository() {
        return vehicleRepository;
    }

    /**
     * Helper to validate that a string is neither null nor empty when trimmed.
     *
     * @param str The string to inspect.
     * @return true if string contains non-whitespace characters.
     */
    private boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }
}

