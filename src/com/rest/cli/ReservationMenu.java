
package com.rest.cli;

import com.rest.models.Reservation;
import com.rest.services.ReservationService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ReservationMenu {

    private Scanner scanner;
    private ReservationService service;

    public ReservationMenu(ReservationService service) {

        this.service = service;
        this.scanner = new Scanner(System.in);
    }

    public void start() {

        int choice = -1;

        do {

            service.updateCompletedReservations();

            System.out.println("\n===== RESERVATION MANAGEMENT =====");
            System.out.println("1. Create Reservation");
            System.out.println("2. Search Reservation by ID");
            System.out.println("3. Search Reservations by Vehicle Number");
            System.out.println("4. Display All Reservations");
            System.out.println("5. Cancel Reservation");
            System.out.println("6. Display Active Reservations");
            System.out.println("7. Display Cancelled Reservations");
            System.out.println("8. Display Completed Reservations");
            System.out.println("9. Complete Reservation Manually");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                continue;
            }

            switch (choice) {

                case 1:
                    createReservation();
                    break;

                case 2:
                    searchById();
                    break;

                case 3:
                    searchByVehicle();
                    break;

                case 4:
                    displayAll();
                    break;

                case 5:
                    cancelReservation();
                    break;

                case 6:
                    displayActive();
                    break;

                case 7:
                    displayCancelled();
                    break;

                case 8:
                    displayCompleted();
                    break;

                case 9:
                    completeReservation();
                    break;

                case 0:
                    System.out.println("Exiting Reservation Management.");
                    break;

                default:
                    System.out.println("Invalid choice.");
            }

        } while (choice != 0);
    }

    // Create reservation
    private void createReservation() {

        try {

            System.out.print("Enter vehicle registration number: ");
            String vehicleNumber = scanner.nextLine().trim();

            System.out.print("Enter vehicle type (Bike/Car/Truck): ");
            String vehicleType = scanner.nextLine().trim();

            System.out.print("Enter parking slot ID: ");
            String reservedSlot = scanner.nextLine().trim();

            System.out.print("Enter reservation date (yyyy-MM-dd): ");
            LocalDate date = LocalDate.parse(scanner.nextLine().trim());

            System.out.print("Enter start time (HH:mm): ");
            LocalTime startTime = LocalTime.parse(scanner.nextLine().trim());

            System.out.print("Enter end time (HH:mm): ");
            LocalTime endTime = LocalTime.parse(scanner.nextLine().trim());

            service.createReservation(
                    vehicleNumber,
                    vehicleType,
                    reservedSlot,
                    date,
                    startTime,
                    endTime
            );

        } catch (DateTimeParseException e) {

            System.out.println(
                    "Invalid date or time format. Please try again.");
        }
    }

    // Search by reservation ID
    private void searchById() {

        System.out.print("Enter reservation ID: ");
        String id = scanner.nextLine().trim();

        service.updateCompletedReservations();

        Reservation reservation = service.searchById(id);

        if (reservation == null) {
            System.out.println("Reservation not found.");
        } else {
            System.out.println(reservation);
        }
    }

    // Search by vehicle number
    private void searchByVehicle() {

        System.out.print("Enter vehicle registration number: ");
        String vehicleNumber = scanner.nextLine().trim();

        service.updateCompletedReservations();

        List<Reservation> reservations =
                service.searchByVehicle(vehicleNumber);

        displayReservations(reservations);
    }

    // Display all reservations
    private void displayAll() {

        service.updateCompletedReservations();

        displayReservations(service.getAllReservations());
    }

    // Display active reservations
    private void displayActive() {

        service.updateCompletedReservations();

        displayReservations(service.getActiveReservations());
    }

    // Display cancelled reservations
    private void displayCancelled() {

        displayReservations(service.getCancelledReservations());
    }

    // Display completed reservations
    private void displayCompleted() {

        service.updateCompletedReservations();

        displayReservations(service.getCompletedReservations());
    }

    // Cancel reservation
    private void cancelReservation() {

        System.out.print("Enter reservation ID to cancel: ");
        String id = scanner.nextLine().trim();

        service.cancelReservation(id);
    }

    // Complete reservation manually
    private void completeReservation() {

        System.out.print("Enter reservation ID: ");
        String id = scanner.nextLine().trim();

        service.completeReservation(id);
    }

    // Common display method
    private void displayReservations(List<Reservation> reservations) {

        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
            return;
        }

        for (Reservation reservation : reservations) {
            System.out.println(reservation);
        }
    }
}
