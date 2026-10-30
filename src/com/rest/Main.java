package com.rest;

import com.rest.cli.ParkingFloorMenu;
import com.rest.cli.ParkingLotMenu;
import com.rest.cli.ParkingRateMenu;
import com.rest.cli.ParkingTicketMenu;
import com.rest.cli.ReservationMenu;
import com.rest.cli.VehicleMenu;
import com.rest.repository.ParkingFloorRepository;
import com.rest.repository.ParkingLotRepository;
import com.rest.repository.ParkingRateRepository;
import com.rest.repository.ParkingTicketRepository;
import com.rest.repository.ReservationRepository;
import com.rest.repository.VehicleRepository;
import com.rest.services.ParkingFloorService;
import com.rest.services.ParkingLotService;
import com.rest.services.ParkingRateService;
import com.rest.services.ParkingTicketService;
import com.rest.services.ReservationService;
import com.rest.services.VehicleService;

import java.time.LocalDateTime;
import java.util.Scanner;

/**
 * Main application entry point for the integrated Parking Lot Management System.
 *
 * Responsibilities:
 * 1. Initializes all repositories and in-memory stores.
 * 2. Injects repositories into service layers.
 * 3. Initializes the parking facility, floors, and slots with sample data.
 * 4. Injects services into respective CLI menus.
 * 5. Provides the unified main navigation console loop.
 */
public class Main {

    public static void main(String[] args) {
        // Single shared console scanner instance
        Scanner scanner = new Scanner(System.in);

        // ==========================================
        // 1. Initialize Repositories (Data Storage)
        // ==========================================
        ParkingLotRepository parkingLotRepository = new ParkingLotRepository();
        VehicleRepository vehicleRepository = new VehicleRepository();
        ParkingFloorRepository floorRepository = new ParkingFloorRepository();
        ParkingRateRepository rateRepository = new ParkingRateRepository();
        ParkingTicketRepository ticketRepository = new ParkingTicketRepository();
        ReservationRepository reservationRepository = new ReservationRepository();

        // ==========================================
        // 2. Initialize Services (Business Logic)
        // ==========================================
        ParkingLotService parkingLotService = new ParkingLotService(parkingLotRepository);
        VehicleService vehicleService = new VehicleService(vehicleRepository);
        ParkingFloorService floorService = new ParkingFloorService(floorRepository);
        ParkingRateService rateService = new ParkingRateService(rateRepository);
        ReservationService reservationService = new ReservationService(reservationRepository);

        // Core orchestrating service connecting tickets, lots, rates, and vehicles
        ParkingTicketService ticketService = new ParkingTicketService(
                ticketRepository,
                parkingLotRepository,
                vehicleService,
                rateService
        );

        // ==========================================
        // 3. Initialize Parking Lot & Sample Data
        // ==========================================
        bootstrapSystemData(parkingLotService, floorService, vehicleService, ticketService, reservationService);

        // ==========================================
        // 4. Initialize CLI Menus (View Layer)
        // ==========================================
        ParkingLotMenu parkingLotMenu = new ParkingLotMenu(parkingLotService, scanner);
        VehicleMenu vehicleMenu = new VehicleMenu(vehicleService, scanner, false);
        ParkingFloorMenu floorMenu = new ParkingFloorMenu(floorService, scanner, false);
        ParkingRateMenu rateMenu = new ParkingRateMenu(rateService, scanner, false);
        ParkingTicketMenu ticketMenu = new ParkingTicketMenu(ticketService, scanner, false);
        ReservationMenu reservationMenu = new ReservationMenu(reservationService, scanner, false);

        // ==========================================
        // 5. Main Application Loop
        // ==========================================
        boolean running = true;

        System.out.println("\n*******************************************************");
        System.out.println("   WELCOME TO THE PARKING LOT MANAGEMENT SYSTEM (CLI)  ");
        System.out.println("*******************************************************");
        System.out.println(" Facility: Grand Central Parking Complex (Online)");

        while (running) {
            System.out.println();
            System.out.println("=======================================================");
            System.out.println("                     MAIN MENU                         ");
            System.out.println("=======================================================");
            System.out.println("  1. Admin / Parking Lot Setup (Floors & Slots)");
            System.out.println("  2. Vehicle Management (Search, Register, Update)");
            System.out.println("  3. Parking Floor Management (Multi-Level)");
            System.out.println("  4. Vehicle Entry & Automatic Slot Allocation");
            System.out.println("  5. Vehicle Exit & Payment Processing");
            System.out.println("  6. Ticket & Operations Management (Pass, Lost Ticket)");
            System.out.println("  7. Parking Rate & Tariff Configuration");
            System.out.println("  8. Advance Slot Reservation");
            System.out.println("  9. Parking Reports & Financial Summary");
            System.out.println(" 10. Live Capacity & Overstay Alerts");
            System.out.println("  0. Exit Application");
            System.out.println("=======================================================");
            System.out.print("Enter your selection (0-10): ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> parkingLotMenu.start();
                case "2" -> vehicleMenu.displayMenu();
                case "3" -> floorMenu.displayMenu();
                case "4" -> ticketMenu.displayMenu();
                case "5" -> ticketMenu.displayMenu();
                case "6" -> ticketMenu.displayMenu();
                case "7" -> rateMenu.displayMenu();
                case "8" -> reservationMenu.displayMenu();
                case "9" -> System.out.println(ticketService.generateParkingReport());
                case "10" -> displayAlerts(ticketService);
                case "0" -> {
                    System.out.println("\nShutting down Parking Lot Management System. Goodbye!");
                    running = false;
                }
                default -> System.out.println("[Error] Invalid selection. Please choose an option between 0 and 10.");
            }
        }

        scanner.close();
    }

    /**
     * Seeds the system with initial parking lot configuration, floors, slots, and sample data.
     */
    private static void bootstrapSystemData(ParkingLotService parkingLotService,
                                            ParkingFloorService floorService,
                                            VehicleService vehicleService,
                                            ParkingTicketService ticketService,
                                            ReservationService reservationService) {
        try {
            // 1. Create Parking Lot
            parkingLotService.createParkingLot("Grand Central Parking", "100 Downtown Blvd, Metropolis");

            // 2. Add Floors
            parkingLotService.addFloor(1);
            parkingLotService.addFloor(2);

            floorService.addFloor("FL-1", 1, "Ground Level", 10);
            floorService.addFloor("FL-2", 2, "Upper Level", 10);

            // 3. Add Slots on Floor 1
            parkingLotService.addSlot(1, 101, "Bike");
            parkingLotService.addSlot(1, 102, "Bike");
            parkingLotService.addSlot(1, 103, "Car");
            parkingLotService.addSlot(1, 104, "Car");
            parkingLotService.addSlot(1, 105, "Truck");

            // 4. Add Slots on Floor 2
            parkingLotService.addSlot(2, 201, "Car");
            parkingLotService.addSlot(2, 202, "Car");
            parkingLotService.addSlot(2, 203, "Car");
            parkingLotService.addSlot(2, 204, "Bike");
            parkingLotService.addSlot(2, 205, "Truck");

            // 5. Seed Registered Vehicles
            vehicleService.addVehicle("KA-01-AB-1234", "Alice Smith", "Car");
            vehicleService.addVehicle("MH-12-CD-5678", "Bob Jones", "Bike");
            vehicleService.addVehicle("DL-05-EF-9012", "Charlie Brown", "Truck");

            // 6. Seed a Monthly Pass for Alice
            ticketService.registerMonthlyPass("KA-01-AB-1234", 30);

            // 7. Seed an Active Parked Vehicle (Bob's Bike)
            ticketService.parkVehicle("MH-12-CD-5678", "Bike", "Bob Jones");

            // 8. Seed a Sample Reservation
            if (reservationService != null) {
                reservationService.createReservation(
                        "RES-101",
                        "DL-05-EF-9012",
                        "SLOT-G5",
                        "Charlie Brown",
                        "9876543210",
                        LocalDateTime.now(),
                        LocalDateTime.now().plusHours(4)
                );
            }

        } catch (Exception e) {
            System.err.println("[Bootstrap Warning] Error during sample data seeding: " + e.getMessage());
        }
    }

    private static void displayAlerts(ParkingTicketService ticketService) {
        System.out.println("\n=======================================================");
        System.out.println("               SYSTEM ALERTS & NOTIFICATIONS           ");
        System.out.println("=======================================================");
        var alerts = ticketService.checkParkingAlerts();
        if (alerts.isEmpty()) {
            System.out.println("[All Clear] No system alerts. Occupancy and parking durations within normal limits.");
        } else {
            for (String alert : alerts) {
                System.out.println(" " + alert);
            }
        }
        System.out.println("=======================================================");
    }
}
