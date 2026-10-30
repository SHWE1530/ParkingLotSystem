package com.rest.cli;

import com.rest.models.ParkingTicket;
import com.rest.services.ParkingTicketService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * CLI interface for Vehicle Entry, Exit, Ticket Generation, Payment Processing,
 * Lost Ticket Resolution, Monthly Pass Handling, and System Analytics.
 */
public class ParkingTicketMenu {

    private final ParkingTicketService ticketService;
    private final Scanner scanner;
    private final boolean shouldCloseScannerOnExit;

    public ParkingTicketMenu(ParkingTicketService ticketService, Scanner scanner, boolean shouldCloseScannerOnExit) {
        this.ticketService = ticketService;
        this.scanner = (scanner != null) ? scanner : new Scanner(System.in);
        this.shouldCloseScannerOnExit = shouldCloseScannerOnExit;
    }

    public void displayMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println();
            System.out.println("==================================================");
            System.out.println("     TICKET, ENTRY & PAYMENT MANAGEMENT (CLI)     ");
            System.out.println("==================================================");
            System.out.println("  1. Vehicle Entry & Auto Slot Allocation");
            System.out.println("  2. Vehicle Exit & Process Payment");
            System.out.println("  3. Validate / Register Monthly Pass");
            System.out.println("  4. Handle Lost Ticket");
            System.out.println("  5. Search Ticket by ID");
            System.out.println("  6. Display All Active (Parked) Vehicles");
            System.out.println("  7. View Parking Reports");
            System.out.println("  8. View Parking Alerts");
            System.out.println("  0. Return to Main Menu");
            System.out.println("==================================================");
            System.out.print("Enter your choice (0-8): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleVehicleEntry();
                case "2" -> handleVehicleExit();
                case "3" -> handleMonthlyPass();
                case "4" -> handleLostTicket();
                case "5" -> handleSearchTicket();
                case "6" -> handleDisplayActiveTickets();
                case "7" -> handleViewReports();
                case "8" -> handleViewAlerts();
                case "0" -> {
                    System.out.println("Returning to Main Menu...");
                    exit = true;
                }
                default -> System.out.println("[Error] Invalid selection. Please enter 0-8.");
            }
        }

        if (shouldCloseScannerOnExit) {
            scanner.close();
        }
    }

    private void handleVehicleEntry() {
        System.out.println("\n--- [ Vehicle Entry & Automatic Slot Allocation ] ---");

        System.out.print("Enter Vehicle Registration Number: ");
        String regNo = scanner.nextLine().trim();
        if (regNo.isEmpty()) {
            System.out.println("[Error] Vehicle number cannot be blank.");
            return;
        }

        System.out.print("Enter Vehicle Type (Bike/Car/Truck): ");
        String vType = scanner.nextLine().trim();
        if (vType.isEmpty()) {
            System.out.println("[Error] Vehicle type cannot be blank.");
            return;
        }

        System.out.print("Enter Owner Name (or press [ENTER] for Guest): ");
        String owner = scanner.nextLine().trim();

        ParkingTicket ticket = ticketService.parkVehicle(regNo, vType, owner);

        if (ticket != null) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println("\n+--------------------------------------------------+");
            System.out.println("|              ENTRY TICKET ISSUED                 |");
            System.out.println("+--------------------------------------------------+");
            System.out.printf("| Ticket ID    : %-33s |\n", ticket.getTicketId());
            System.out.printf("| Vehicle No   : %-33s |\n", ticket.getVehicleNumber());
            System.out.printf("| Vehicle Type : %-33s |\n", ticket.getVehicleType());
            System.out.printf("| Floor Number : Floor %-27d |\n", ticket.getFloorNumber());
            System.out.printf("| Slot Number  : Slot %-28d |\n", ticket.getSlotNumber());
            System.out.printf("| Entry Time   : %-33s |\n", ticket.getEntryTime().format(dtf));
            System.out.printf("| Pass Status  : %-33s |\n", ticket.isMonthlyPass() ? "MONTHLY PASS HOLDER" : "STANDARD");
            System.out.println("+--------------------------------------------------+");
            System.out.println("-> Please proceed to Floor " + ticket.getFloorNumber() + ", Slot " + ticket.getSlotNumber() + ".");
        } else {
            System.out.println("[Failed] Could not issue ticket. Please verify floor/slot availability.");
        }
    }

    private void handleVehicleExit() {
        System.out.println("\n--- [ Vehicle Exit & Payment Processing ] ---");

        System.out.print("Enter Ticket ID to settle (e.g. TKT-1001): ");
        String ticketId = scanner.nextLine().trim();

        Optional<ParkingTicket> ticketOpt = ticketService.getTicketById(ticketId);
        if (ticketOpt.isEmpty()) {
            System.out.println("[Error] Ticket not found: " + ticketId);
            return;
        }

        ParkingTicket ticket = ticketOpt.get();
        if (!"ACTIVE".equalsIgnoreCase(ticket.getStatus()) && !"PASS_HOLDER".equalsIgnoreCase(ticket.getStatus())) {
            System.out.println("[Info] Ticket is already settled. Status: " + ticket.getStatus());
            return;
        }

        double fee = ticketService.calculateFee(ticket, LocalDateTime.now());
        System.out.println("\nVehicle Number : " + ticket.getVehicleNumber());
        System.out.println("Slot Location  : Floor " + ticket.getFloorNumber() + ", Slot " + ticket.getSlotNumber());
        System.out.printf("Total Fee Due  : $%.2f%s\n", fee, ticket.isMonthlyPass() ? " (Waived - Monthly Pass)" : "");

        System.out.print("Select Payment Method (1: Cash, 2: Card, 3: UPI): ");
        String pChoice = scanner.nextLine().trim();
        String method = switch (pChoice) {
            case "2" -> "CARD";
            case "3" -> "UPI";
            default -> "CASH";
        };

        boolean success = ticketService.processPaymentAndExit(ticketId, method);
        if (success) {
            System.out.println("\n[Success] Payment settled! Slot has been released.");
            System.out.println(ticketService.generateReceipt(ticketId));
        } else {
            System.out.println("[Failed] Could not process exit.");
        }
    }

    private void handleMonthlyPass() {
        System.out.println("\n--- [ Monthly Pass Management ] ---");
        System.out.println("1. Check Monthly Pass Status");
        System.out.println("2. Register New Monthly Pass");
        System.out.print("Select option (1-2): ");

        String opt = scanner.nextLine().trim();
        if ("1".equals(opt)) {
            System.out.print("Enter Vehicle Registration Number: ");
            String vNo = scanner.nextLine().trim();
            Optional<LocalDateTime> expOpt = ticketService.getMonthlyPassExpiry(vNo);
            if (expOpt.isPresent() && expOpt.get().isAfter(LocalDateTime.now())) {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                System.out.println("[Active Pass] Valid until: " + expOpt.get().format(dtf));
            } else {
                System.out.println("[No Active Pass] No valid monthly pass found for " + vNo);
            }
        } else if ("2".equals(opt)) {
            System.out.print("Enter Vehicle Registration Number: ");
            String vNo = scanner.nextLine().trim();
            System.out.print("Enter Validity in Days (e.g. 30): ");
            try {
                int days = Integer.parseInt(scanner.nextLine().trim());
                boolean registered = ticketService.registerMonthlyPass(vNo, days);
                if (registered) {
                    System.out.println("[Success] Monthly pass activated for " + vNo.toUpperCase() + " (" + days + " days)!");
                }
            } catch (NumberFormatException e) {
                System.out.println("[Error] Invalid number of days.");
            }
        }
    }

    private void handleLostTicket() {
        System.out.println("\n--- [ Lost Ticket Handling ] ---");
        System.out.print("Enter Vehicle Registration Number: ");
        String vNo = scanner.nextLine().trim();

        System.out.print("Payment Method for Lost Penalty (1: Cash, 2: Card, 3: UPI): ");
        String pChoice = scanner.nextLine().trim();
        String method = switch (pChoice) {
            case "2" -> "CARD";
            case "3" -> "UPI";
            default -> "CASH";
        };

        ParkingTicket settled = ticketService.handleLostTicket(vNo, method);
        if (settled != null) {
            System.out.println("\n[Lost Ticket Resolved] Fixed Penalty applied: $50.00.");
            System.out.println(ticketService.generateReceipt(settled.getTicketId()));
        } else {
            System.out.println("[Failed] Could not resolve lost ticket.");
        }
    }

    private void handleSearchTicket() {
        System.out.println("\n--- [ Search Ticket by ID ] ---");
        System.out.print("Enter Ticket ID: ");
        String tId = scanner.nextLine().trim();

        Optional<ParkingTicket> tOpt = ticketService.getTicketById(tId);
        if (tOpt.isPresent()) {
            ParkingTicket t = tOpt.get();
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            System.out.println("\n+--------------------------------------------------+");
            System.out.printf("| Ticket ID    : %-33s |\n", t.getTicketId());
            System.out.printf("| Vehicle No   : %-33s |\n", t.getVehicleNumber());
            System.out.printf("| Vehicle Type : %-33s |\n", t.getVehicleType());
            System.out.printf("| Location     : Floor %-2, Slot %-18d |\n", t.getFloorNumber(), t.getSlotNumber());
            System.out.printf("| Entry Time   : %-33s |\n", t.getEntryTime().format(dtf));
            System.out.printf("| Status       : %-33s |\n", t.getStatus());
            System.out.printf("| Fee Paid     : $%-32.2f |\n", t.getFee());
            System.out.println("+--------------------------------------------------+");
        } else {
            System.out.println("[Not Found] No ticket found with ID: " + tId);
        }
    }

    private void handleDisplayActiveTickets() {
        System.out.println("\n--- [ All Currently Parked Vehicles ] ---");
        List<ParkingTicket> active = ticketService.getAllActiveTickets();

        if (active.isEmpty()) {
            System.out.println("[Info] No vehicles are currently parked.");
            return;
        }

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        System.out.println("+----------+---------------+------------+-------+------+------------------+-------------+");
        System.out.printf("| %-8s | %-13s | %-10s | %-5s | %-4s | %-16s | %-11s |\n",
                "TicketID", "Vehicle No", "Type", "Floor", "Slot", "Entry Time", "Status");
        System.out.println("+----------+---------------+------------+-------+------+------------------+-------------+");

        for (ParkingTicket t : active) {
            System.out.printf("| %-8s | %-13s | %-10s | %-5d | %-4d | %-16s | %-11s |\n",
                    t.getTicketId(),
                    t.getVehicleNumber(),
                    t.getVehicleType(),
                    t.getFloorNumber(),
                    t.getSlotNumber(),
                    t.getEntryTime().format(dtf),
                    t.getStatus());
        }
        System.out.println("+----------+---------------+------------+-------+------+------------------+-------------+");
        System.out.println("Total Parked Vehicles: " + active.size());
    }

    private void handleViewReports() {
        System.out.println(ticketService.generateParkingReport());
    }

    private void handleViewAlerts() {
        System.out.println("\n==================================================");
        System.out.println("              PARKING SYSTEM ALERTS               ");
        System.out.println("==================================================");
        List<String> alerts = ticketService.checkParkingAlerts();

        if (alerts.isEmpty()) {
            System.out.println("[All Clear] No system alerts. Capacity and parking times within safe limits.");
        } else {
            for (String alert : alerts) {
                System.out.println(" " + alert);
            }
        }
        System.out.println("==================================================");
    }
}

