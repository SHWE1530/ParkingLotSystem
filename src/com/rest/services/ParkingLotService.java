package com.rest.services;

import com.rest.models.ParkingLot;
import com.rest.models.ParkingLot.Floor;
import com.rest.models.ParkingLot.Slot;
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

        if (name == null || name.isBlank()
                || address == null || address.isBlank()) {
            throw new IllegalArgumentException(
                "Name and address cannot be empty."
            );
        }

        ParkingLot parkingLot = new ParkingLot(name, address);

        repository.saveParkingLot(parkingLot);

        System.out.println("Parking lot created successfully!");
    }

    public void addFloor(int floorNumber) {

        if (floorNumber <= 0) {
            throw new IllegalArgumentException(
                "Floor number must be positive."
            );
        }

        if (repository.getParkingLot() == null) {
            System.out.println("Create parking lot first.");
            return;
        }

        if (repository.getFloor(floorNumber) != null) {
            System.out.println("Floor already exists.");
            return;
        }

        Floor floor = new Floor(floorNumber);

        repository.addFloor(floor);

        System.out.println("Floor added successfully!");
    }

    public void addSlot(int floorNumber, int slotNumber, String type) {

        if (repository.getParkingLot() == null) {
            System.out.println("Create parking lot first.");
            return;
        }

        Floor floor = repository.getFloor(floorNumber);

        if (floor == null) {
            System.out.println("Floor not found.");
            return;
        }

        if (floor.isClosed()) {
            System.out.println("Cannot add slots to a closed floor.");
            return;
        }

        if (slotNumber <= 0) {
            System.out.println("Invalid slot number.");
            return;
        }

        if (!isValidType(type)) {
            System.out.println("Invalid slot type.");
            return;
        }

        if (repository.isSlotNumberExists(floorNumber, slotNumber)) {
            System.out.println("Slot number already exists on this floor.");
            return;
        }

        Slot slot = new Slot(slotNumber, type);

        repository.addSlot(floorNumber, slot);

        System.out.println("Slot added successfully!");
    }

    public void changeSlotType(
            int floorNumber, int slotNumber, String newType) {

        Floor floor = repository.getFloor(floorNumber);

        if (floor == null) {
            System.out.println("Floor not found.");
            return;
        }

        if (!isValidType(newType)) {
            System.out.println("Invalid slot type.");
            return;
        }

        for (Slot slot : floor.getSlots()) {

            if (slot.getSlotNumber() == slotNumber) {

                try {
                    slot.setType(newType);
                    System.out.println("Slot type updated successfully!");
                } catch (IllegalStateException e) {
                    System.out.println(e.getMessage());
                }

                return;
            }
        }

        System.out.println("Slot not found.");
    }

    public void markUnderMaintenance(int floorNumber, int slotNumber) {

        Floor floor = repository.getFloor(floorNumber);

        if (floor == null) {
            System.out.println("Floor not found.");
            return;
        }

        for (Slot slot : floor.getSlots()) {

            if (slot.getSlotNumber() == slotNumber) {

                try {
                    slot.setStatus("Under Maintenance");
                    System.out.println("Slot marked under maintenance.");
                } catch (IllegalStateException e) {
                    System.out.println(e.getMessage());
                }

                return;
            }
        }

        System.out.println("Slot not found.");
    }

    public void closeFloor(int floorNumber) {

        Floor floor = repository.getFloor(floorNumber);

        if (floor == null) {
            System.out.println("Floor not found.");
            return;
        }

        floor.setClosed(true);

        System.out.println("Floor closed successfully!");
    }

    public void displayParkingLot() {

        ParkingLot parkingLot = repository.getParkingLot();

        if (parkingLot == null) {
            System.out.println("Parking lot not created.");
            return;
        }

        System.out.println("\n========== PARKING LOT ==========");
        System.out.println("Name    : " + parkingLot.getName());
        System.out.println("Address : " + parkingLot.getAddress());

        for (Floor floor : parkingLot.getFloors()) {

            System.out.println("\nFloor: " + floor.getFloorNumber());
            System.out.println("Status: "
                    + (floor.isClosed() ? "Closed" : "Open"));

            for (Slot slot : floor.getSlots()) {

                System.out.println(
                    "Slot: " + slot.getSlotNumber()
                    + " | Type: " + slot.getType()
                    + " | Status: " + slot.getStatus()
                );
            }
        }
    }

    private boolean isValidType(String type) {

        return type != null &&
                (type.equalsIgnoreCase("Bike")
                || type.equalsIgnoreCase("Car")
                || type.equalsIgnoreCase("Truck"));
    }
}
