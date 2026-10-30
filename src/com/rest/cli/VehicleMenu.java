package com.rest.cli;

import com.rest.models.Vehicle;
import com.rest.services.VehicleService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * CLI User Interface for the Vehicle Management Module.
 * Provides interactive terminal menus using java.util.Scanner.
 * Designed to be run standalone or invoked from a parent application Main Menu.
 */
public class VehicleMenu {

    private final VehicleService vehicleService;
    private final Scanner scanner;
    private final boolean shouldCloseScannerOnExit;

    /**
     * Default constructor for standalone use.
     * Creates its own VehicleService and wraps System.in with a new Scanner.
     */
    public VehicleMenu() {
        this(new VehicleService(), new Scanner(System.in), false);
    }

    /**
     * Constructor allowing injection of a shared VehicleService.
     *
     * @param vehicleService The shared service instance.
     */
    public VehicleMenu(VehicleService vehicleService) {
        this(vehicleService, new Scanner(System.in), false);
    }

    /**
     * Full constructor allowing injection of both a shared VehicleService and a shared Scanner.
     * Prevents closing System.in prematurely when invoked from a team member's Main CLI.
     *
     * @param vehicleService           The vehicle service instance.
     * @param scanner                  The scanner instance for console input.
     * @param shouldCloseScannerOnExit Whether to close the scanner when exiting this menu.
     */
    public VehicleMenu(VehicleService vehicleService, Scanner scanner, boolean shouldCloseScannerOnExit) {
        this.vehicleService = (vehicleService != null) ? vehicleService : new VehicleService();
        this.scanner = (scanner != null) ? scanner : new Scanner(System.in);
        this.shouldCloseScannerOnExit = shouldCloseScannerOnExit;
    }

    /**
     * Starts and displays the Vehicle Management interactive CLI menu.
     */
    public void displayMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println();
            System.out.println("==================================================");
            System.out.println("       VEHICLE MANAGEMENT SYSTEM (CLI)           ");
            System.out.println("==================================================");
            System.out.println("  1. Add New Vehicle");
            System.out.println("  2. Search Vehicle by Registration Number");
            System.out.println("  3. Display All Registered Vehicles");
            System.out.println("  4. Update Vehicle Details");
            System.out.println("  5. Delete Vehicle Details");
            System.out.println("  6. Return to Main Menu / Exit");
            System.out.println("==================================================");
            System.out.print("Enter your choice (1-6): ");

            String choiceInput = scanner.nextLine().trim();

            switch (choiceInput) {
                case "1" -> handleAddVehicle();
                case "2" -> handleSearchVehicle();
                case "3" -> handleDisplayAllVehicles();
                case "4" -> handleUpdateVehicle();
                case "5" -> handleDeleteVehicle();
                case "6" -> {
                    System.out.println("\nExiting Vehicle Management Module. Goodbye!");
                    exit = true;
                }
                default -> System.out.println("[Error] Invalid selection. Please enter a number between 1 and 6.");
            }
        }

        if (shouldCloseScannerOnExit) {
            scanner.close();
        }
    }

    /**
     * CLI handler for adding a new vehicle.
     */
    private void handleAddVehicle() {
        System.out.println("\n--- [ Add New Vehicle ] ---");

        String regNumber = readRequiredString("Enter Vehicle Registration Number (e.g. KA-01-AB-1234): ");
        if (regNumber == null) return;

        // Early check before asking for remaining fields
        if (vehicleService.isVehicleRegistered(regNumber)) {
            System.out.println("[Error] A vehicle with registration number '" + regNumber.toUpperCase() + "' is already registered!");
            return;
        }

        String ownerName = readRequiredString("Enter Owner's Full Name: ");
        if (ownerName == null) return;

        String vehicleType = readRequiredString("Enter Vehicle Type (e.g. Car, Bike, SUV, Truck): ");
        if (vehicleType == null) return;

        boolean success = vehicleService.addVehicle(regNumber, ownerName, vehicleType);
        if (success) {
            System.out.println("[Success] Vehicle " + regNumber.toUpperCase() + " successfully registered!");
        } else {
            System.out.println("[Failed] Could not register vehicle. Please verify input data.");
        }
    }

    /**
     * CLI handler for searching a vehicle by registration number.
     */
    private void handleSearchVehicle() {
        System.out.println("\n--- [ Search Vehicle by Registration Number ] ---");

        String regNumber = readRequiredString("Enter Vehicle Registration Number to search: ");
        if (regNumber == null) return;

        Optional<Vehicle> result = vehicleService.searchVehicle(regNumber);

        if (result.isPresent()) {
            Vehicle v = result.get();
            System.out.println("\n+--------------------------------------------------+");
            System.out.println("|                 VEHICLE FOUND                    |");
            System.out.println("+--------------------------------------------------+");
            System.out.printf("| Registration No: %-31s |\n", v.getVehicleNumber());
            System.out.printf("| Owner Name     : %-31s |\n", v.getOwnerName());
            System.out.printf("| Vehicle Type   : %-31s |\n", v.getVehicleType());
            System.out.println("+--------------------------------------------------+");
        } else {
            System.out.println("[Not Found] No vehicle registered with registration number: " + regNumber.toUpperCase());
        }
    }

    /**
     * CLI handler for displaying all registered vehicles in tabular format.
     */
    private void handleDisplayAllVehicles() {
        System.out.println("\n--- [ All Registered Vehicles ] ---");

        List<Vehicle> vehicles = vehicleService.getAllVehicles();

        if (vehicles.isEmpty()) {
            System.out.println("[Info] No vehicles currently registered in the system.");
            return;
        }

        System.out.println("+-----+----------------------+----------------------+-----------------+");
        System.out.printf("| %-3s | %-20s | %-20s | %-15s |\n", "#", "Registration No", "Owner Name", "Vehicle Type");
        System.out.println("+-----+----------------------+----------------------+-----------------+");

        int index = 1;
        for (Vehicle v : vehicles) {
            System.out.printf("| %-3d | %-20s | %-20s | %-15s |\n",
                    index++,
                    truncate(v.getVehicleNumber(), 20),
                    truncate(v.getOwnerName(), 20),
                    truncate(v.getVehicleType(), 15));
        }
        System.out.println("+-----+----------------------+----------------------+-----------------+");
        System.out.println("Total Registered Vehicles: " + vehicles.size());
    }

    /**
     * CLI handler for updating vehicle details.
     */
    private void handleUpdateVehicle() {
        System.out.println("\n--- [ Update Vehicle Details ] ---");

        String regNumber = readRequiredString("Enter Vehicle Registration Number to update: ");
        if (regNumber == null) return;

        Optional<Vehicle> result = vehicleService.searchVehicle(regNumber);
        if (result.isEmpty()) {
            System.out.println("[Not Found] No vehicle found with registration number: " + regNumber.toUpperCase());
            return;
        }

        Vehicle vehicle = result.get();
        System.out.println("Current Details: " + vehicle);
        System.out.println("(Press [ENTER] to leave a field unchanged)");

        System.out.print("Enter New Owner Name [" + vehicle.getOwnerName() + "]: ");
        String newOwner = scanner.nextLine().trim();

        System.out.print("Enter New Vehicle Type [" + vehicle.getVehicleType() + "]: ");
        String newType = scanner.nextLine().trim();

        if (newOwner.isEmpty() && newType.isEmpty()) {
            System.out.println("[Info] No changes entered. Vehicle details remain unchanged.");
            return;
        }

        boolean updated = vehicleService.updateVehicle(regNumber, newOwner, newType);
        if (updated) {
            System.out.println("[Success] Vehicle details updated successfully!");
            vehicleService.searchVehicle(regNumber).ifPresent(v -> System.out.println("Updated: " + v));
        } else {
            System.out.println("[Failed] Could not update vehicle details.");
        }
    }

    /**
     * CLI handler for deleting a vehicle.
     */
    private void handleDeleteVehicle() {
        System.out.println("\n--- [ Delete Vehicle Details ] ---");

        String regNumber = readRequiredString("Enter Vehicle Registration Number to delete: ");
        if (regNumber == null) return;

        Optional<Vehicle> vehicleOpt = vehicleService.searchVehicle(regNumber);
        if (vehicleOpt.isEmpty()) {
            System.out.println("[Not Found] No vehicle found with registration number: " + regNumber.toUpperCase());
            return;
        }

        Vehicle v = vehicleOpt.get();
        System.out.println("Found: " + v);
        System.out.print("Are you sure you want to delete this vehicle? (Y/N): ");
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("Y") || confirm.equalsIgnoreCase("YES")) {
            boolean deleted = vehicleService.deleteVehicle(regNumber);
            if (deleted) {
                System.out.println("[Success] Vehicle " + regNumber.toUpperCase() + " successfully deleted.");
            } else {
                System.out.println("[Failed] Could not delete vehicle.");
            }
        } else {
            System.out.println("[Cancelled] Deletion cancelled by user.");
        }
    }

    /**
     * Helper method to read a non-empty string prompt from the user.
     *
     * @param prompt The message to display.
     * @return The trimmed input, or null if aborted.
     */
    private String readRequiredString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            }
            System.out.println("[Warning] This field is required and cannot be blank. (Type 'CANCEL' to return)");
            if (input.equalsIgnoreCase("CANCEL")) {
                return null;
            }
        }
    }

    /**
     * Helper method to safely truncate strings for clean table formatting.
     */
    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    /**
     * Standalone main method allowing direct execution and testing of the Vehicle Management module.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {
        VehicleMenu menu = new VehicleMenu();
        menu.displayMenu();
    }
}

