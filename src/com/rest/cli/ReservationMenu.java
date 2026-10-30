package com.rest.cli;

import com.rest.models.Reservation;
import com.rest.services.ReservationService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * CLI interface for advance slot reservation and scheduled bookings.
 */
public class ReservationMenu {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final ReservationService reservationService;
    private final Scanner scanner;
    private final boolean shouldCloseScannerOnExit;

    public ReservationMenu() {
        this(new ReservationService(), new Scanner(System.in), false);
    }

    public ReservationMenu(ReservationService reservationService, Scanner scanner, boolean shouldCloseScannerOnExit) {
        this.reservationService = (reservationService != null) ? reservationService : new ReservationService();
        this.scanner = (scanner != null) ? scanner : new Scanner(System.in);
        this.shouldCloseScannerOnExit = shouldCloseScannerOnExit;
    }

    public void displayMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println();
            System.out.println("==================================================");
            System.out.println("        PARKING SLOT RESERVATION (CLI)            ");
            System.out.println("==================================================");
            System.out.println("  1. Book / Create New Reservation");
            System.out.println("  2. Search Reservation by ID");
            System.out.println("  3. View Reservations by Vehicle Number");
            System.out.println("  4. Display All Reservations");
            System.out.println("  5. Check-In Reserved Vehicle (Arrival)");
            System.out.println("  6. Cancel a Reservation");
            System.out.println("  7. Return to Main Menu");
            System.out.println("==================================================");
            System.out.print("Enter your choice (1-7): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleCreateReservation();
                case "2" -> handleSearchById();
                case "3" -> handleSearchByVehicle();
                case "4" -> handleDisplayAll();
                case "5" -> handleCheckIn();
                case "6" -> handleCancel();
                case "7" -> {
                    System.out.println("Returning to Main Menu...");
                    exit = true;
                }
                default -> System.out.println("[Error] Invalid selection. Please enter 1-7.");
            }
        }

        if (shouldCloseScannerOnExit) {
            scanner.close();
        }
    }

    private void handleCreateReservation() {
        System.out.println("\n--- [ Book / Create New Reservation ] ---");

        System.out.print("Enter Unique Reservation ID (e.g. RES-101): ");
        String resId = scanner.nextLine().trim();

        System.out.print("Enter Vehicle Registration Number: ");
        String vehicleNumber = scanner.nextLine().trim();

        System.out.print("Enter Parking Slot ID (e.g. SLOT-G1): ");
        String slotId = scanner.nextLine().trim();

        System.out.print("Enter Customer Full Name: ");
        String customerName = scanner.nextLine().trim();

        System.out.print("Enter Contact Phone Number: ");
        String contact = scanner.nextLine().trim();

        System.out.println("Booking Timestamps format: yyyy-MM-dd HH:mm (e.g. 2026-10-30 16:00)");

        System.out.print("Enter Reservation Start Time (or press [ENTER] for NOW): ");
        String startStr = scanner.nextLine().trim();
        LocalDateTime startTime;
        if (startStr.isEmpty()) {
            startTime = LocalDateTime.now();
        } else {
            try {
                startTime = LocalDateTime.parse(startStr, FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("[Error] Invalid date format. Please use yyyy-MM-dd HH:mm.");
                return;
            }
        }

        System.out.print("Enter Duration in Hours (e.g. 2, 4, 8): ");
        int durationHours;
        try {
            durationHours = Integer.parseInt(scanner.nextLine().trim());
            if (durationHours <= 0) {
                System.out.println("[Error] Duration must be at least 1 hour.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("[Error] Invalid duration number.");
            return;
        }

        LocalDateTime endTime = startTime.plusHours(durationHours);

        boolean created = reservationService.createReservation(
                resId, vehicleNumber, slotId, customerName, contact, startTime, endTime
        );

        if (created) {
            System.out.println("\n[Success] Reservation Confirmed!");
            System.out.println("  Reservation ID : " + resId.toUpperCase());
            System.out.println("  Slot Assigned  : " + slotId.toUpperCase());
            System.out.println("  Valid From     : " + startTime.format(FORMATTER));
            System.out.println("  Valid Until    : " + endTime.format(FORMATTER));
        } else {
            System.out.println("[Failed] Could not complete reservation. Check for slot conflict or invalid data.");
        }
    }

    private void handleSearchById() {
        System.out.println("\n--- [ Search Reservation by ID ] ---");
        System.out.print("Enter Reservation ID: ");
        String resId = scanner.nextLine().trim();

        Optional<Reservation> resOpt = reservationService.getReservation(resId);
        if (resOpt.isPresent()) {
            displayReservationCard(resOpt.get());
        } else {
            System.out.println("[Not Found] No reservation found with ID: " + resId);
        }
    }

    private void handleSearchByVehicle() {
        System.out.println("\n--- [ Search Reservations by Vehicle Number ] ---");
        System.out.print("Enter Vehicle Registration Number: ");
        String vehicleNumber = scanner.nextLine().trim();

        List<Reservation> list = reservationService.getReservationsByVehicle(vehicleNumber);
        if (list.isEmpty()) {
            System.out.println("[Not Found] No reservations found for vehicle: " + vehicleNumber);
            return;
        }

        for (Reservation r : list) {
            displayReservationCard(r);
        }
    }

    private void handleDisplayAll() {
        System.out.println("\n--- [ All Active & Scheduled Reservations ] ---");
        List<Reservation> list = reservationService.getAllReservations();

        if (list.isEmpty()) {
            System.out.println("[Info] No reservations currently recorded in the system.");
            return;
        }

        System.out.println("+----------+---------------+----------+----------------------+------------------+------------------+------------+");
        System.out.printf("| %-8s | %-13s | %-8s | %-20s | %-16s | %-16s | %-10s |\n",
                "Res ID", "Vehicle No", "Slot ID", "Customer", "Start Time", "End Time", "Status");
        System.out.println("+----------+---------------+----------+----------------------+------------------+------------------+------------+");

        for (Reservation r : list) {
            System.out.printf("| %-8s | %-13s | %-8s | %-20s | %-16s | %-16s | %-10s |\n",
                    r.getReservationId(),
                    r.getVehicleNumber(),
                    r.getSlotId(),
                    truncate(r.getCustomerName(), 20),
                    r.getStartTime().format(FORMATTER),
                    r.getEndTime().format(FORMATTER),
                    r.getStatus());
        }
        System.out.println("+----------+---------------+----------+----------------------+------------------+------------------+------------+");
        System.out.println("Total Reservations: " + list.size());
    }

    private void handleCheckIn() {
        System.out.println("\n--- [ Check-In Reserved Vehicle ] ---");
        System.out.print("Enter Reservation ID: ");
        String resId = scanner.nextLine().trim();

        boolean checkedIn = reservationService.checkInReservation(resId);
        if (checkedIn) {
            System.out.println("[Success] Reservation " + resId.toUpperCase() + " successfully checked in! Vehicle may proceed to slot.");
        } else {
            System.out.println("[Failed] Could not check-in. Verify reservation ID and status.");
        }
    }

    private void handleCancel() {
        System.out.println("\n--- [ Cancel Reservation ] ---");
        System.out.print("Enter Reservation ID to cancel: ");
        String resId = scanner.nextLine().trim();

        System.out.print("Are you sure you want to cancel reservation " + resId.toUpperCase() + "? (Y/N): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("Y") || confirm.equalsIgnoreCase("YES")) {
            boolean cancelled = reservationService.cancelReservation(resId);
            if (cancelled) {
                System.out.println("[Success] Reservation cancelled.");
            } else {
                System.out.println("[Failed] Could not cancel reservation.");
            }
        } else {
            System.out.println("[Aborted] Cancellation cancelled.");
        }
    }

    private void displayReservationCard(Reservation r) {
        System.out.println("\n+------------------------------------------------------+");
        System.out.println("|                 RESERVATION DETAILS                  |");
        System.out.println("+------------------------------------------------------+");
        System.out.printf("| Reservation ID : %-35s |\n", r.getReservationId());
        System.out.printf("| Vehicle Number : %-35s |\n", r.getVehicleNumber());
        System.out.printf("| Assigned Slot  : %-35s |\n", r.getSlotId());
        System.out.printf("| Customer Name  : %-35s |\n", r.getCustomerName());
        System.out.printf("| Phone Number   : %-35s |\n", r.getContactNumber());
        System.out.printf("| Start Time     : %-35s |\n", r.getStartTime().format(FORMATTER));
        System.out.printf("| End Time       : %-35s |\n", r.getEndTime().format(FORMATTER));
        System.out.printf("| Booking Status : %-35s |\n", r.getStatus());
        System.out.println("+------------------------------------------------------+");
    }

    private String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }

    public static void main(String[] args) {
        new ReservationMenu().displayMenu();
    }
}

