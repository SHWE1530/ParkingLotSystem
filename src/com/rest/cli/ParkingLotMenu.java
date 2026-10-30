package com.rest.cli;

import java.util.Scanner;

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
            System.out.println("2. Add Floor");
            System.out.println("3. Add Slot");
            System.out.println("4. Change Slot Type");
            System.out.println("5. Mark Slot Under Maintenance");
            System.out.println("6. Close Floor");
            System.out.println("7. Display Parking Lot");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Enter parking lot name: ");
                    String name = scanner.nextLine();

                    System.out.print("Enter address: ");
                    String address = scanner.nextLine();

                    service.createParkingLot(name, address);
                    break;

                case 2:
                    System.out.print("Enter floor number: ");
                    int floor = scanner.nextInt();

                    service.addFloor(floor);
                    break;

                case 3:
                    System.out.print("Enter floor number: ");
                    int floorNumber = scanner.nextInt();

                    System.out.print("Enter slot number: ");
                    int slotNumber = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter slot type (Bike/Car/Truck): ");
                    String type = scanner.nextLine();

                    service.addSlot(floorNumber, slotNumber, type);
                    break;

                case 4:
                    System.out.print("Enter floor number: ");
                    int f = scanner.nextInt();

                    System.out.print("Enter slot number: ");
                    int s = scanner.nextInt();
                    scanner.nextLine();

                    System.out.print("Enter new slot type: ");
                    String newType = scanner.nextLine();

                    service.changeSlotType(f, s, newType);
                    break;

                case 5:
                    System.out.print("Enter floor number: ");
                    int mf = scanner.nextInt();

                    System.out.print("Enter slot number: ");
                    int ms = scanner.nextInt();

                    service.markUnderMaintenance(mf, ms);
                    break;

                case 6:
                    System.out.print("Enter floor number to close: ");
                    int cf = scanner.nextInt();

                    service.closeFloor(cf);
                    break;

                case 7:
                    service.displayParkingLot();
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