package com.rest.services;

import com.rest.models.ParkingFloor;
import com.rest.repository.ParkingFloorRepository;

import java.util.List;
import java.util.Optional;

/**
 * Service layer containing business rules and validations for Parking Floors.
 */
public class ParkingFloorService {

    private final ParkingFloorRepository floorRepository;

    public ParkingFloorService() {
        this.floorRepository = new ParkingFloorRepository();
    }

    public ParkingFloorService(ParkingFloorRepository floorRepository) {
        if (floorRepository == null) {
            throw new IllegalArgumentException("ParkingFloorRepository cannot be null.");
        }
        this.floorRepository = floorRepository;
    }

    /**
     * Adds a new parking floor with validation.
     *
     * @param floorId     Unique ID (e.g. FL-1).
     * @param floorNumber Numeric level.
     * @param floorName   Descriptive name.
     * @param maxCapacity Capacity (> 0).
     * @return true if added, false if duplicate or invalid.
     */
    public boolean addFloor(String floorId, int floorNumber, String floorName, int maxCapacity) {
        if (!isValidString(floorId) || !isValidString(floorName)) {
            System.err.println("[Validation Error] Floor ID and Name cannot be blank.");
            return false;
        }

        if (maxCapacity <= 0) {
            System.err.println("[Validation Error] Maximum capacity must be greater than 0.");
            return false;
        }

        String normalizedId = floorId.trim().toUpperCase();

        if (floorRepository.existsById(normalizedId)) {
            System.err.println("[Business Rule Error] Floor with ID '" + normalizedId + "' already exists.");
            return false;
        }

        if (floorRepository.existsByFloorNumber(floorNumber)) {
            System.err.println("[Business Rule Error] Floor level number " + floorNumber + " is already registered.");
            return false;
        }

        ParkingFloor floor = new ParkingFloor(normalizedId, floorNumber, floorName.trim(), maxCapacity);
        floorRepository.save(floor);
        return true;
    }

    public Optional<ParkingFloor> getFloor(String floorId) {
        if (!isValidString(floorId)) return Optional.empty();
        return floorRepository.findById(floorId.trim().toUpperCase());
    }

    public List<ParkingFloor> getAllFloors() {
        return floorRepository.findAll();
    }

    /**
     * Updates floor details like name, capacity, or active status.
     */
    public boolean updateFloor(String floorId, String newName, Integer newCapacity, Boolean isActive) {
        if (!isValidString(floorId)) return false;

        Optional<ParkingFloor> floorOpt = floorRepository.findById(floorId.trim().toUpperCase());
        if (floorOpt.isEmpty()) {
            System.err.println("[Error] Floor not found: " + floorId);
            return false;
        }

        ParkingFloor floor = floorOpt.get();

        if (isValidString(newName)) {
            floor.setFloorName(newName.trim());
        }

        if (newCapacity != null) {
            if (newCapacity < floor.getOccupiedSlots()) {
                System.err.println("[Business Rule Error] New capacity (" + newCapacity +
                        ") cannot be less than currently occupied slots (" + floor.getOccupiedSlots() + ").");
                return false;
            }
            floor.setMaxCapacity(newCapacity);
        }

        if (isActive != null) {
            floor.setActive(isActive);
        }

        return floorRepository.update(floor);
    }

    /**
     * Deletes a floor only if no vehicles/slots are currently occupied.
     */
    public boolean deleteFloor(String floorId) {
        if (!isValidString(floorId)) return false;

        Optional<ParkingFloor> floorOpt = floorRepository.findById(floorId.trim().toUpperCase());
        if (floorOpt.isEmpty()) {
            System.err.println("[Error] Floor not found: " + floorId);
            return false;
        }

        ParkingFloor floor = floorOpt.get();
        if (floor.getOccupiedSlots() > 0) {
            System.err.println("[Business Rule Error] Cannot delete floor '" + floorId +
                    "' while vehicles are currently parked on it (" + floor.getOccupiedSlots() + " occupied).");
            return false;
        }

        return floorRepository.deleteById(floor.getFloorId());
    }

    /**
     * Records an occupied slot increment when a vehicle parks on this floor.
     */
    public boolean incrementOccupancy(String floorId) {
        Optional<ParkingFloor> floorOpt = getFloor(floorId);
        if (floorOpt.isEmpty()) return false;
        ParkingFloor floor = floorOpt.get();
        if (!floor.hasAvailableSlot()) return false;
        floor.setOccupiedSlots(floor.getOccupiedSlots() + 1);
        return floorRepository.update(floor);
    }

    /**
     * Decrements occupied slots when a vehicle exits.
     */
    public boolean decrementOccupancy(String floorId) {
        Optional<ParkingFloor> floorOpt = getFloor(floorId);
        if (floorOpt.isEmpty()) return false;
        ParkingFloor floor = floorOpt.get();
        if (floor.getOccupiedSlots() <= 0) return false;
        floor.setOccupiedSlots(floor.getOccupiedSlots() - 1);
        return floorRepository.update(floor);
    }

    public ParkingFloorRepository getFloorRepository() {
        return floorRepository;
    }

    private boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }
}

