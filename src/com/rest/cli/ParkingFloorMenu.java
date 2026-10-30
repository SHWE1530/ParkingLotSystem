package com.rest.cli;

import com.rest.models.ParkingFloor;
import com.rest.services.ParkingFloorService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * CLI interface for managing Parking Floors in multi-level parking facilities.
 */
public class ParkingFloorMenu {

    private final ParkingFloorService floorService;
    private final Scanner scanner;
    private final boolean shouldCloseScannerOnExit;

    public ParkingFloorMenu() {
        this(new ParkingFloorService(), new Scanner(System.in), false);
    }

    public ParkingFloorMenu(ParkingFloorService floorService, Scanner scanner, boolean shouldCloseScannerOnExit) {
        this.floorService = (floorService != null) ? floorService : new ParkingFloorService();
        this.scanner = (scanner != null) ? scanner : new Scanner(System.in);
        this.shouldCloseScannerOnExit = shouldCloseScannerOnExit;
    }

    public void displayMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println();
            System.out.println("==================================================");
            System.out.println("         PARKING FLOOR MANAGEMENT (CLI)           ");
            System.out.println("==================================================");
            System.out.println("  1. Add New Parking Floor");
            System.out.println("  2. View Floor by ID");
            System.out.println("  3. Display All Floors & Live Capacity");
            System.out.println("  4. Update Floor Details");
            System.out.println("  5. Toggle Floor Maintenance / Active Status");
            System.out.println("  6. Delete Parking Floor");
            System.out.println("  7. Return to Main Menu");
            System.out.println("==================================================");
            System.out.print("Enter your choice (1-7): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleAddFloor();
                case "2" -> handleViewFloor();
                case "3" -> handleDisplayAllFloors();
                case "4" -> handleUpdateFloor();
                case "5" -> handleToggleStatus();
                case "6" -> handleDeleteFloor();
                case "7" -> {
                    System.out.println("Returning to Main Menu...");
                    exit = true;
                }
                default -> System.out.println("[Error] Invalid option. Please select 1-7.");
            }
        }

        if (shouldCloseScannerOnExit) {
            scanner.close();
        }
    }

    private void handleAddFloor() {
        System.out.println("\n--- [ Add New Parking Floor ] ---");

        System.out.print("Enter Floor ID (e.g. FL-1, FL-B1): ");
        String floorId = scanner.nextLine().trim();
        if (floorId.isEmpty()) {
            System.out.println("[Error] Floor ID cannot be empty.");
            return;
        }

        System.out.print("Enter Floor Level Number (e.g. 1 for 1st floor, -1 for basement): ");
        int levelNumber;
        try {
            levelNumber = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[Error] Invalid level number.");
            return;
        }

        System.out.print("Enter Floor Name (e.g. Ground Floor, Basement 1): ");
        String floorName = scanner.nextLine().trim();

        System.out.print("Enter Maximum Slot Capacity on this floor: ");
        int capacity;
        try {
            capacity = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[Error] Invalid capacity number.");
            return;
        }

        boolean success = floorService.addFloor(floorId, levelNumber, floorName, capacity);
        if (success) {
            System.out.println("[Success] Floor " + floorId.toUpperCase() + " registered successfully!");
        } else {
            System.out.println("[Failed] Could not create parking floor.");
        }
    }

    private void handleViewFloor() {
        System.out.println("\n--- [ View Floor by ID ] ---");
        System.out.print("Enter Floor ID to search: ");
        String floorId = scanner.nextLine().trim();

        Optional<ParkingFloor> floorOpt = floorService.getFloor(floorId);
        if (floorOpt.isPresent()) {
            ParkingFloor f = floorOpt.get();
            System.out.println("\n+--------------------------------------------------+");
            System.out.printf("| Floor ID     : %-33s |\n", f.getFloorId());
            System.out.printf("| Floor Level  : %-33d |\n", f.getFloorNumber());
            System.out.printf("| Floor Name   : %-33s |\n", f.getFloorName());
            System.out.printf("| Max Capacity : %-33d |\n", f.getMaxCapacity());
            System.out.printf("| Occupied     : %-33d |\n", f.getOccupiedSlots());
            System.out.printf("| Free Slots   : %-33d |\n", f.getAvailableSlots());
            System.out.printf("| Status       : %-33s |\n", f.isActive() ? "ACTIVE" : "MAINTENANCE");
            System.out.println("+--------------------------------------------------+");
        } else {
            System.out.println("[Not Found] No floor found with ID: " + floorId);
        }
    }

    private void handleDisplayAllFloors() {
        System.out.println("\n--- [ All Parking Floors & Capacity ] ---");
        List<ParkingFloor> floors = floorService.getAllFloors();

        if (floors.isEmpty()) {
            System.out.println("[Info] No parking floors configured yet.");
            return;
        }

        System.out.println("+---------+-------+--------------------+----------+----------+-----------+-------------+");
        System.out.printf("| %-7s | %-5s | %-18s | %-8s | %-8s | %-9s | %-11s |\n",
                "FloorID", "Level", "Name", "Capacity", "Occupied", "Available", "Status");
        System.out.println("+---------+-------+--------------------+----------+----------+-----------+-------------+");

        for (ParkingFloor f : floors) {
            System.out.printf("| %-7s | %-5d | %-18s | %-8d | %-8d | %-9d | %-11s |\n",
                    f.getFloorId(),
                    f.getFloorNumber(),
                    f.getFloorName(),
                    f.getMaxCapacity(),
                    f.getOccupiedSlots(),
                    f.getAvailableSlots(),
                    f.isActive() ? "ACTIVE" : "MAINTENANCE");
        }
        System.out.println("+---------+-------+--------------------+----------+----------+-----------+-------------+");
        System.out.println("Total Configured Floors: " + floors.size());
    }

    private void handleUpdateFloor() {
        System.out.println("\n--- [ Update Floor Details ] ---");
        System.out.print("Enter Floor ID to update: ");
        String floorId = scanner.nextLine().trim();

        Optional<ParkingFloor> floorOpt = floorService.getFloor(floorId);
        if (floorOpt.isEmpty()) {
            System.out.println("[Not Found] Floor does not exist: " + floorId);
            return;
        }

        ParkingFloor f = floorOpt.get();
        System.out.println("Current Floor Details: " + f);
        System.out.println("(Press [ENTER] to leave any field unchanged)");

        System.out.print("Enter New Name [" + f.getFloorName() + "]: ");
        String newName = scanner.nextLine().trim();

        System.out.print("Enter New Capacity [" + f.getMaxCapacity() + "]: ");
        String capInput = scanner.nextLine().trim();
        Integer newCap = null;
        if (!capInput.isEmpty()) {
            try {
                newCap = Integer.parseInt(capInput);
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid capacity number.");
                return;
            }
        }

        boolean updated = floorService.updateFloor(floorId, newName, newCap, null);
        if (updated) {
            System.out.println("[Success] Floor updated successfully!");
        } else {
            System.out.println("[Failed] Could not update floor.");
        }
    }

    private void handleToggleStatus() {
        System.out.println("\n--- [ Toggle Maintenance / Active Status ] ---");
        System.out.print("Enter Floor ID: ");
        String floorId = scanner.nextLine().trim();

        Optional<ParkingFloor> floorOpt = floorService.getFloor(floorId);
        if (floorOpt.isEmpty()) {
            System.out.println("[Not Found] Floor does not exist: " + floorId);
            return;
        }

        ParkingFloor f = floorOpt.get();
        boolean newStatus = !f.isActive();
        boolean updated = floorService.updateFloor(floorId, null, null, newStatus);
        if (updated) {
            System.out.println("[Success] Floor status set to: " + (newStatus ? "ACTIVE" : "MAINTENANCE"));
        } else {
            System.out.println("[Failed] Could not change floor status.");
        }
    }

    private void handleDeleteFloor() {
        System.out.println("\n--- [ Delete Parking Floor ] ---");
        System.out.print("Enter Floor ID to delete: ");
        String floorId = scanner.nextLine().trim();

        Optional<ParkingFloor> floorOpt = floorService.getFloor(floorId);
        if (floorOpt.isEmpty()) {
            System.out.println("[Not Found] Floor does not exist.");
            return;
        }

        System.out.print("Are you sure you want to delete floor " + floorId.toUpperCase() + "? (Y/N): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("Y") || confirm.equalsIgnoreCase("YES")) {
            boolean deleted = floorService.deleteFloor(floorId);
            if (deleted) {
                System.out.println("[Success] Floor deleted successfully.");
            } else {
                System.out.println("[Failed] Cannot delete floor (verify no vehicles are parked on it).");
            }
        } else {
            System.out.println("[Cancelled] Deletion cancelled.");
        }
    }

    public static void main(String[] args) {
        new ParkingFloorMenu().displayMenu();
    }
}

