package com.rest.cli;

import java.util.Scanner;

import com.rest.models.ParkingSlot;
import com.rest.repository.ParkingLotRepository;
import com.rest.services.ParkingLotService;

public class ParkingLotMenu {

    private Scanner scanner;
    private ParkingLotService service;

    public ParkingLotMenu() {

        scanner = new Scanner(System.in);

        ParkingLotRepository repository =
                new ParkingLotRepository();

        service = new ParkingLotService(repository);
    }

    public void start() {

        int choice;

        do {

            System.out.println("\n===== PARKING LOT MANAGEMENT =====");
            System.out.println("1. Create Parking Lot");
            System.out.println("2. Add Parking Slot");
            System.out.println("3. Display Parking Lot Details");
            System.out.println("4. Display Total Slot Count");
            System.out.println("5. Display Available Slot Count");
            System.out.println("6. Display Occupied Slot Count");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:

                    System.out.print("Enter parking lot name: ");
                    String name = scanner.nextLine();

                    service.createParkingLot(name);
                    break;

                case 2:

                    System.out.print("Enter slot number: ");
                    int slotNumber = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter slot type (Bike/Car/Truck): ");
                    String slotType = scanner.nextLine();

                    ParkingSlot slot =
                            new ParkingSlot(slotNumber, slotType, "Free");

                    service.addParkingSlot(slot);
                    break;

                case 3:

                    service.displayParkingLot();
                    break;

                case 4:

                    System.out.println("Total Slots: "
                            + service.getTotalSlotCount());
                    break;

                case 5:

                    System.out.println("Available Slots: "
                            + service.getAvailableSlotCount());
                    break;

                case 6:

                    System.out.println("Occupied Slots: "
                            + service.getOccupiedSlotCount());
                    break;

                case 0:

                    System.out.println("Exiting Parking Lot Management.");
                    break;

                default:

                    System.out.println("Invalid choice.");
            }

        } while (choice != 0);
    }
}