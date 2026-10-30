package com.rest.cli;

import com.rest.models.ParkingRate;
import com.rest.services.ParkingRateService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * CLI interface for configuring and testing Parking Rates and Tariff calculations.
 */
public class ParkingRateMenu {

    private final ParkingRateService rateService;
    private final Scanner scanner;
    private final boolean shouldCloseScannerOnExit;

    public ParkingRateMenu() {
        this(new ParkingRateService(), new Scanner(System.in), false);
    }

    public ParkingRateMenu(ParkingRateService rateService, Scanner scanner, boolean shouldCloseScannerOnExit) {
        this.rateService = (rateService != null) ? rateService : new ParkingRateService();
        this.scanner = (scanner != null) ? scanner : new Scanner(System.in);
        this.shouldCloseScannerOnExit = shouldCloseScannerOnExit;
    }

    public void displayMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println();
            System.out.println("==================================================");
            System.out.println("        PARKING RATE & TARIFF MANAGEMENT          ");
            System.out.println("==================================================");
            System.out.println("  1. Configure / Add Parking Rate");
            System.out.println("  2. Search Rate by Vehicle Type");
            System.out.println("  3. Display All Configured Parking Rates");
            System.out.println("  4. Calculate & Simulate Fee for Duration");
            System.out.println("  5. Delete a Parking Rate");
            System.out.println("  6. Return to Main Menu");
            System.out.println("==================================================");
            System.out.print("Enter your choice (1-6): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleConfigureRate();
                case "2" -> handleSearchRate();
                case "3" -> handleDisplayAllRates();
                case "4" -> handleSimulateFee();
                case "5" -> handleDeleteRate();
                case "6" -> {
                    System.out.println("Returning to Main Menu...");
                    exit = true;
                }
                default -> System.out.println("[Error] Invalid selection. Please enter 1-6.");
            }
        }

        if (shouldCloseScannerOnExit) {
            scanner.close();
        }
    }

    private void handleConfigureRate() {
        System.out.println("\n--- [ Configure / Add Parking Rate ] ---");

        System.out.print("Enter Rate ID (e.g. RATE-CAR, RATE-EV): ");
        String rateId = scanner.nextLine().trim();

        System.out.print("Enter Vehicle Type (e.g. CAR, BIKE, SUV, TRUCK, ELECTRIC): ");
        String vType = scanner.nextLine().trim();

        System.out.print("Enter Base Fee for 1st hour ($): ");
        double baseFee;
        try {
            baseFee = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[Error] Invalid base fee format.");
            return;
        }

        System.out.print("Enter Hourly Rate for subsequent hours ($/hr): ");
        double hourlyRate;
        try {
            hourlyRate = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[Error] Invalid hourly rate format.");
            return;
        }

        System.out.print("Enter Penalty Rate ($/hr): ");
        double penaltyRate;
        try {
            penaltyRate = Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[Error] Invalid penalty rate format.");
            return;
        }

        boolean saved = rateService.saveRate(rateId, vType, baseFee, hourlyRate, penaltyRate);
        if (saved) {
            System.out.println("[Success] Parking rate configured successfully!");
        } else {
            System.out.println("[Failed] Could not save parking rate.");
        }
    }

    private void handleSearchRate() {
        System.out.println("\n--- [ Search Rate by Vehicle Type ] ---");
        System.out.print("Enter Vehicle Type (e.g. CAR, BIKE): ");
        String vType = scanner.nextLine().trim();

        Optional<ParkingRate> rateOpt = rateService.getRateByVehicleType(vType);
        if (rateOpt.isPresent()) {
            ParkingRate r = rateOpt.get();
            System.out.println("\n+--------------------------------------------------+");
            System.out.printf("| Rate ID         : %-30s |\n", r.getRateId());
            System.out.printf("| Vehicle Type    : %-30s |\n", r.getVehicleType());
            System.out.printf("| Base Fee (1st hr): $%-29.2f |\n", r.getBaseFee());
            System.out.printf("| Subsequent Hourly: $%-29.2f |\n", r.getHourlyRate());
            System.out.printf("| Penalty Hourly   : $%-29.2f |\n", r.getPenaltyPerHour());
            System.out.println("+--------------------------------------------------+");
        } else {
            System.out.println("[Not Found] No rate configured for: " + vType);
        }
    }

    private void handleDisplayAllRates() {
        System.out.println("\n--- [ All Active Parking Rates ] ---");
        List<ParkingRate> rates = rateService.getAllRates();

        if (rates.isEmpty()) {
            System.out.println("[Info] No rates configured.");
            return;
        }

        System.out.println("+---------------+--------------+------------+-------------+--------------+");
        System.out.printf("| %-13s | %-12s | %-10s | %-11s | %-12s |\n",
                "Rate ID", "Vehicle Type", "Base Fee", "Hourly Rate", "Penalty Rate");
        System.out.println("+---------------+--------------+------------+-------------+--------------+");

        for (ParkingRate r : rates) {
            System.out.printf("| %-13s | %-12s | $%-9.2f | $%-10.2f | $%-11.2f |\n",
                    r.getRateId(),
                    r.getVehicleType(),
                    r.getBaseFee(),
                    r.getHourlyRate(),
                    r.getPenaltyPerHour());
        }
        System.out.println("+---------------+--------------+------------+-------------+--------------+");
    }

    private void handleSimulateFee() {
        System.out.println("\n--- [ Calculate & Simulate Parking Fee ] ---");
        System.out.print("Enter Vehicle Type (e.g. CAR, BIKE): ");
        String vType = scanner.nextLine().trim();

        System.out.print("Enter duration parked in minutes (e.g. 15, 90, 180): ");
        long minutes;
        try {
            minutes = Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("[Error] Invalid minutes format.");
            return;
        }

        LocalDateTime entryTime = LocalDateTime.now().minusMinutes(minutes);
        LocalDateTime exitTime = LocalDateTime.now();

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        double totalFee = rateService.calculateFee(vType, entryTime, exitTime);

        System.out.println("\n+--------------------------------------------------+");
        System.out.println("|               BILLING SIMULATION                 |");
        System.out.println("+--------------------------------------------------+");
        System.out.printf("| Vehicle Type : %-33s |\n", vType.toUpperCase());
        System.out.printf("| Entry Time   : %-33s |\n", entryTime.format(dtf));
        System.out.printf("| Exit Time    : %-33s |\n", exitTime.format(dtf));
        System.out.printf("| Duration     : %-33s |\n", minutes + " minutes (~" + String.format("%.1f", minutes / 60.0) + " hrs)");
        System.out.printf("| Total Fee    : $%-32.2f |\n", totalFee);
        System.out.println("+--------------------------------------------------+");
    }

    private void handleDeleteRate() {
        System.out.println("\n--- [ Delete a Parking Rate ] ---");
        System.out.print("Enter Rate ID to delete: ");
        String rateId = scanner.nextLine().trim();

        boolean deleted = rateService.deleteRate(rateId);
        if (deleted) {
            System.out.println("[Success] Rate deleted successfully.");
        } else {
            System.out.println("[Failed] Rate ID not found.");
        }
    }

    public static void main(String[] args) {
        new ParkingRateMenu().displayMenu();
    }
}

