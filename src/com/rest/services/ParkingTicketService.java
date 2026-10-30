package com.rest.services;

import com.rest.models.ParkingLot;
import com.rest.models.ParkingLot.Floor;
import com.rest.models.ParkingLot.Slot;
import com.rest.models.ParkingTicket;
import com.rest.models.Vehicle;
import com.rest.repository.ParkingLotRepository;
import com.rest.repository.ParkingTicketRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service layer coordinating vehicle entry, automatic slot allocation,
 * ticket generation, fee calculation, payment processing, lost ticket resolution,
 * monthly pass verification, and analytics reporting.
 */
public class ParkingTicketService {

    private final ParkingTicketRepository ticketRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final VehicleService vehicleService;
    private final ParkingRateService parkingRateService;

    private int nextTicketSequence = 1001;

    public ParkingTicketService(ParkingTicketRepository ticketRepository,
                                ParkingLotRepository parkingLotRepository,
                                VehicleService vehicleService,
                                ParkingRateService parkingRateService) {
        this.ticketRepository = (ticketRepository != null) ? ticketRepository : new ParkingTicketRepository();
        this.parkingLotRepository = parkingLotRepository;
        this.vehicleService = vehicleService;
        this.parkingRateService = (parkingRateService != null) ? parkingRateService : new ParkingRateService();
    }

    /**
     * 4. Vehicle Entry and Automatic Slot Allocation
     * 5. Ticket Generation
     *
     * Finds the nearest/first available slot matching vehicle type on an open floor,
     * marks it Occupied, auto-registers vehicle if new, and issues a ticket.
     */
    public synchronized ParkingTicket parkVehicle(String vehicleNumber, String vehicleType, String ownerName) {
        if (!isValidString(vehicleNumber) || !isValidString(vehicleType)) {
            System.err.println("[Validation Error] Vehicle registration number and type are required.");
            return null;
        }

        String normalizedNumber = vehicleNumber.trim().toUpperCase();
        String normalizedType = vehicleType.trim();

        // Check if vehicle is already parked inside
        Optional<ParkingTicket> activeTicket = ticketRepository.findActiveByVehicleNumber(normalizedNumber);
        if (activeTicket.isPresent()) {
            System.err.println("[Conflict Error] Vehicle " + normalizedNumber + " is already parked with active Ticket "
                    + activeTicket.get().getTicketId() + " (Floor " + activeTicket.get().getFloorNumber()
                    + ", Slot " + activeTicket.get().getSlotNumber() + ").");
            return null;
        }

        // Auto-register vehicle in VehicleService if not already registered
        if (vehicleService != null && !vehicleService.isVehicleRegistered(normalizedNumber)) {
            String name = (isValidString(ownerName)) ? ownerName.trim() : "Guest Driver";
            vehicleService.addVehicle(normalizedNumber, name, normalizedType);
        }

        // Automatic Slot Allocation
        ParkingLot lot = parkingLotRepository.getParkingLot();
        if (lot == null) {
            System.err.println("[Error] Parking Lot is not configured yet. Please run Parking Lot Setup first.");
            return null;
        }

        Floor allocatedFloor = null;
        Slot allocatedSlot = null;

        for (Floor floor : lot.getFloors()) {
            if (floor.isClosed()) {
                continue; // Skip closed floors
            }
            for (Slot slot : floor.getSlots()) {
                if ("Free".equalsIgnoreCase(slot.getStatus())
                        && slot.getType().equalsIgnoreCase(normalizedType)) {
                    allocatedFloor = floor;
                    allocatedSlot = slot;
                    break;
                }
            }
            if (allocatedSlot != null) {
                break;
            }
        }

        if (allocatedSlot == null) {
            System.err.println("[No Slot Available] Sorry, all " + normalizedType + " slots are currently occupied or closed!");
            return null;
        }

        // Mark slot as Occupied
        allocatedSlot.setStatus("Occupied");

        // Generate Ticket
        String ticketId = "TKT-" + (nextTicketSequence++);
        ParkingTicket ticket = new ParkingTicket(ticketId, normalizedNumber, normalizedType,
                allocatedFloor.getFloorNumber(), allocatedSlot.getSlotNumber());

        // Check if vehicle holds an active monthly pass
        if (isMonthlyPassValid(normalizedNumber)) {
            ticket.setMonthlyPass(true);
            ticket.setStatus("PASS_HOLDER");
        }

        ticketRepository.save(ticket);
        return ticket;
    }

    /**
     * 6. Parking Fee Calculation
     */
    public double calculateFee(ParkingTicket ticket, LocalDateTime exitTime) {
        if (ticket == null) return 0.0;

        if (ticket.isMonthlyPass() || "PASS_HOLDER".equalsIgnoreCase(ticket.getStatus())) {
            return 0.0;
        }

        LocalDateTime end = (exitTime != null) ? exitTime : LocalDateTime.now();
        if (parkingRateService != null) {
            try {
                return parkingRateService.calculateFee(ticket.getVehicleType(), ticket.getEntryTime(), end);
            } catch (Exception e) {
                // Fallback standard rate
            }
        }

        // Fallback default calculation: $20 first hour, $10 subsequent hours
        Duration duration = Duration.between(ticket.getEntryTime(), end);
        long minutes = duration.toMinutes();
        if (minutes <= 15) return 0.0;
        double fee = 20.0;
        if (minutes > 60) {
            long extraHours = (long) Math.ceil((minutes - 60) / 60.0);
            fee += (extraHours * 10.0);
        }
        return fee;
    }

    /**
     * 8. Payment Processing
     * 10. Vehicle Exit
     */
    public synchronized boolean processPaymentAndExit(String ticketId, String paymentMethod) {
        Optional<ParkingTicket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isEmpty()) {
            System.err.println("[Error] Ticket not found: " + ticketId);
            return false;
        }

        ParkingTicket ticket = ticketOpt.get();
        if (!"ACTIVE".equalsIgnoreCase(ticket.getStatus()) && !"PASS_HOLDER".equalsIgnoreCase(ticket.getStatus())) {
            System.err.println("[Error] Ticket is already processed with status: " + ticket.getStatus());
            return false;
        }

        LocalDateTime exitTime = LocalDateTime.now();
        double fee = calculateFee(ticket, exitTime);

        ticket.setExitTime(exitTime);
        ticket.setFee(fee);
        ticket.setPaymentMethod(isValidString(paymentMethod) ? paymentMethod.toUpperCase() : "CASH");
        ticket.setStatus("PAID");

        // Free the parking slot in ParkingLot
        freeSlot(ticket.getFloorNumber(), ticket.getSlotNumber());

        ticketRepository.update(ticket);
        return true;
    }

    /**
     * 11. Lost Ticket Handling
     * Charges fixed penalty ($50) or calculates maximum rate and frees the slot.
     */
    public synchronized ParkingTicket handleLostTicket(String vehicleNumber, String paymentMethod) {
        if (!isValidString(vehicleNumber)) {
            System.err.println("[Validation Error] Vehicle registration number is required.");
            return null;
        }

        String normalizedNumber = vehicleNumber.trim().toUpperCase();
        Optional<ParkingTicket> activeOpt = ticketRepository.findActiveByVehicleNumber(normalizedNumber);

        ParkingTicket ticket;
        if (activeOpt.isPresent()) {
            ticket = activeOpt.get();
            freeSlot(ticket.getFloorNumber(), ticket.getSlotNumber());
        } else {
            // Unregistered active ticket: generate a lost ticket record
            String ticketId = "LOST-" + (nextTicketSequence++);
            ticket = new ParkingTicket(ticketId, normalizedNumber, "Car", 1, 0);
        }

        double penaltyFee = 50.0; // Standard lost ticket fee
        ticket.setExitTime(LocalDateTime.now());
        ticket.setFee(penaltyFee);
        ticket.setStatus("LOST");
        ticket.setPaymentMethod(isValidString(paymentMethod) ? paymentMethod.toUpperCase() : "CASH");

        ticketRepository.save(ticket);
        return ticket;
    }

    /**
     * Helper to free a slot in the parking lot.
     */
    private void freeSlot(int floorNumber, int slotNumber) {
        if (parkingLotRepository == null) return;
        Floor floor = parkingLotRepository.getFloor(floorNumber);
        if (floor != null) {
            for (Slot slot : floor.getSlots()) {
                if (slot.getSlotNumber() == slotNumber) {
                    slot.setStatus("Free");
                    break;
                }
            }
        }
    }

    /**
     * 7. Monthly Pass Validation & Registration
     */
    public boolean registerMonthlyPass(String vehicleNumber, int validityDays) {
        if (!isValidString(vehicleNumber) || validityDays <= 0) {
            System.err.println("[Validation Error] Valid vehicle number and positive duration required.");
            return false;
        }
        String normalizedNumber = vehicleNumber.trim().toUpperCase();
        LocalDateTime expiry = LocalDateTime.now().plusDays(validityDays);
        ticketRepository.saveMonthlyPass(normalizedNumber, expiry);
        return true;
    }

    public boolean isMonthlyPassValid(String vehicleNumber) {
        if (!isValidString(vehicleNumber)) return false;
        Optional<LocalDateTime> expiryOpt = ticketRepository.getMonthlyPassExpiry(vehicleNumber.trim().toUpperCase());
        return expiryOpt.isPresent() && expiryOpt.get().isAfter(LocalDateTime.now());
    }

    public Optional<LocalDateTime> getMonthlyPassExpiry(String vehicleNumber) {
        if (!isValidString(vehicleNumber)) return Optional.empty();
        return ticketRepository.getMonthlyPassExpiry(vehicleNumber.trim().toUpperCase());
    }

    /**
     * 9. Receipt Generation
     */
    public String generateReceipt(String ticketId) {
        Optional<ParkingTicket> ticketOpt = ticketRepository.findById(ticketId);
        if (ticketOpt.isEmpty()) return "[Receipt Error] Ticket ID not found.";

        ParkingTicket t = ticketOpt.get();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Duration dur = Duration.between(t.getEntryTime(), (t.getExitTime() != null ? t.getExitTime() : LocalDateTime.now()));
        long minutes = dur.toMinutes();

        StringBuilder sb = new StringBuilder();
        sb.append("\n======================================================\n");
        sb.append("               PARKING PAYMENT RECEIPT                \n");
        sb.append("======================================================\n");
        sb.append(String.format(" Receipt / Ticket ID : %s\n", t.getTicketId()));
        sb.append(String.format(" Vehicle Number      : %s (%s)\n", t.getVehicleNumber(), t.getVehicleType()));
        sb.append(String.format(" Allocated Space     : Floor %d, Slot %d\n", t.getFloorNumber(), t.getSlotNumber()));
        sb.append(String.format(" Entry Time          : %s\n", t.getEntryTime().format(dtf)));
        sb.append(String.format(" Exit Time           : %s\n", t.getExitTime() != null ? t.getExitTime().format(dtf) : "N/A"));
        sb.append(String.format(" Total Duration      : %d mins (~%.1f hrs)\n", minutes, minutes / 60.0));
        sb.append("------------------------------------------------------\n");
        sb.append(String.format(" Total Fee Paid      : $%.2f\n", t.getFee()));
        sb.append(String.format(" Payment Method      : %s\n", t.getPaymentMethod()));
        sb.append(String.format(" Status              : %s\n", t.getStatus()));
        sb.append("======================================================\n");
        sb.append("          Thank you for parking with us!             \n");
        sb.append("======================================================\n");
        return sb.toString();
    }

    /**
     * 12. Parking Reports
     */
    public String generateParkingReport() {
        List<ParkingTicket> allTickets = ticketRepository.findAll();
        List<ParkingTicket> activeTickets = ticketRepository.findAllActive();
        List<ParkingTicket> paidTickets = ticketRepository.findAllPaid();

        double totalRevenue = paidTickets.stream().mapToDouble(ParkingTicket::getFee).sum();

        StringBuilder sb = new StringBuilder();
        sb.append("\n======================================================\n");
        sb.append("              PARKING ANALYTICS REPORT                \n");
        sb.append("======================================================\n");
        sb.append(String.format(" Total Lifetime Tickets Issued : %d\n", allTickets.size()));
        sb.append(String.format(" Currently Active Vehicles     : %d\n", activeTickets.size()));
        sb.append(String.format(" Completed / Settled Sessions  : %d\n", paidTickets.size()));
        sb.append(String.format(" Total Revenue Collected       : $%.2f\n", totalRevenue));
        sb.append("------------------------------------------------------\n");
        sb.append(" FLOOR-WISE LIVE OCCUPANCY:\n");

        ParkingLot lot = parkingLotRepository.getParkingLot();
        if (lot != null) {
            for (Floor floor : lot.getFloors()) {
                int total = floor.getSlots().size();
                long occupied = floor.getSlots().stream().filter(s -> "Occupied".equalsIgnoreCase(s.getStatus())).count();
                double pct = total > 0 ? (occupied * 100.0 / total) : 0.0;
                sb.append(String.format("  - Floor %d: %d/%d occupied (%.1f%%) [%s]\n",
                        floor.getFloorNumber(), occupied, total, pct, floor.isClosed() ? "CLOSED" : "OPEN"));
            }
        }
        sb.append("======================================================\n");
        return sb.toString();
    }

    /**
     * 13. Parking Alerts
     */
    public List<String> checkParkingAlerts() {
        List<String> alerts = new ArrayList<>();

        ParkingLot lot = parkingLotRepository.getParkingLot();
        if (lot != null) {
            for (Floor floor : lot.getFloors()) {
                int total = floor.getSlots().size();
                long occupied = floor.getSlots().stream().filter(s -> "Occupied".equalsIgnoreCase(s.getStatus())).count();
                if (total > 0 && (occupied * 100.0 / total) >= 90.0) {
                    alerts.add(String.format("[CAPACITY ALERT] Floor %d has reached %.1f%% capacity (%d/%d slots full)!",
                            floor.getFloorNumber(), (occupied * 100.0 / total), occupied, total));
                }
            }
        }

        // Overstay Alerts: Active vehicles parked over 24 hours
        LocalDateTime now = LocalDateTime.now();
        for (ParkingTicket t : ticketRepository.findAllActive()) {
            if (Duration.between(t.getEntryTime(), now).toHours() >= 24) {
                alerts.add(String.format("[OVERSTAY ALERT] Vehicle %s (Ticket %s) has been parked for over 24 hours on Floor %d, Slot %d!",
                        t.getVehicleNumber(), t.getTicketId(), t.getFloorNumber(), t.getSlotNumber()));
            }
        }

        return alerts;
    }

    public Optional<ParkingTicket> getTicketById(String ticketId) {
        if (!isValidString(ticketId)) return Optional.empty();
        return ticketRepository.findById(ticketId.trim().toUpperCase());
    }

    public List<ParkingTicket> getAllActiveTickets() {
        return ticketRepository.findAllActive();
    }

    public ParkingTicketRepository getTicketRepository() {
        return ticketRepository;
    }

    private boolean isValidString(String str) {
        return str != null && !str.trim().isEmpty();
    }
}

