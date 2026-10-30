package com.rest.services;

import com.rest.models.ParkingLot;
import com.rest.models.ParkingSlot;
import com.rest.repository.ParkingLotRepository;

public class ParkingLotService {

    private ParkingLotRepository repository;

    public ParkingLotService(ParkingLotRepository repository) {
        this.repository = repository;
    }

    public ParkingLotRepository getRepository() {
        return repository;
    }

    public void createParkingLot(String name, String address) {

        if (parkingLotName == null || parkingLotName.isBlank()) {
            System.out.println("Parking lot name cannot be empty.");
            return;
        }

        if (repository.parkingLotExists()) {
            System.out.println("Parking lot already exists.");
            return;
        }

        ParkingLot parkingLot = new ParkingLot(parkingLotName);

        repository.saveParkingLot(parkingLot);

        System.out.println("Parking lot created successfully!");
    }

    public void addParkingSlot(ParkingSlot slot) {

        if (!repository.parkingLotExists()) {
            System.out.println("Create parking lot first.");
            return;
        }

        if (slot == null) {
            System.out.println("Invalid parking slot.");
            return;
        }

        ParkingLot parkingLot = repository.getParkingLot();

        if (parkingLot.getParkingSlots().contains(slot)) {
            System.out.println("Parking slot already added.");
            return;
        }

        parkingLot.getParkingSlots().add(slot);

        System.out.println("Parking slot added successfully!");
    }

    public void displayParkingLot() {

        if (!repository.parkingLotExists()) {
            System.out.println("Parking lot not created.");
            return;
        }

        ParkingLot parkingLot = repository.getParkingLot();

        System.out.println("\n===== PARKING LOT DETAILS =====");
        System.out.println("Parking Lot Name: "
                + parkingLot.getParkingLotName());

        System.out.println("Total Slots: " + getTotalSlotCount());
        System.out.println("Available Slots: " + getAvailableSlotCount());
        System.out.println("Occupied Slots: " + getOccupiedSlotCount());

        System.out.println("\n===== PARKING SLOT DETAILS =====");

        if (parkingLot.getParkingSlots().isEmpty()) {
            System.out.println("No parking slots available.");
            return;
        }

        for (ParkingSlot slot : parkingLot.getParkingSlots()) {
            System.out.println(slot);
        }
    }

    public int getTotalSlotCount() {

        if (!repository.parkingLotExists()) {
            return 0;
        }

        return repository.getParkingLot()
                .getParkingSlots().size();
    }

    public int getAvailableSlotCount() {

        if (!repository.parkingLotExists()) {
            return 0;
        }

        int count = 0;

        for (ParkingSlot slot :
                repository.getParkingLot().getParkingSlots()) {

            if ("Free".equalsIgnoreCase(slot.getStatus())) {
                count++;
            }
        }

        return count;
    }

    public int getOccupiedSlotCount() {

        if (!repository.parkingLotExists()) {
            return 0;
        }

        int count = 0;

        for (ParkingSlot slot :
                repository.getParkingLot().getParkingSlots()) {

            if ("Occupied".equalsIgnoreCase(slot.getStatus())) {
                count++;
            }
        }

        return count;
    }
}
