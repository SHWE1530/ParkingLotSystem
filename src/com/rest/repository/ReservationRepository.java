
package com.rest.repository;

import com.rest.models.Reservation;
import java.util.ArrayList;
import java.util.List;

public class ReservationRepository {

    private List<Reservation> reservations = new ArrayList<>();

    // Save reservation
    public void add(Reservation reservation) {
        reservations.add(reservation);
    }

    // Find reservation using ID
    public Reservation findById(String reservationId) {

        for (Reservation reservation : reservations) {

            if (reservation.getReservationId()
                    .equalsIgnoreCase(reservationId)) {
                return reservation;
            }
        }

        return null;
    }

    // Find reservations using vehicle number
    public List<Reservation> findByVehicleNumber(String vehicleNumber) {

        List<Reservation> result = new ArrayList<>();

        for (Reservation reservation : reservations) {

            if (reservation.getVehicleNumber()
                    .equalsIgnoreCase(vehicleNumber)) {
                result.add(reservation);
            }
        }

        return result;
    }

    // Return all reservations
    public List<Reservation> findAll() {
        return new ArrayList<>(reservations);
    }

    // Return active reservations
    public List<Reservation> findActive() {

        List<Reservation> result = new ArrayList<>();

        for (Reservation reservation : reservations) {

            if ("CONFIRMED".equals(reservation.getReservationStatus())) {
                result.add(reservation);
            }
        }

        return result;
    }

    // Return cancelled reservations
    public List<Reservation> findCancelled() {

        List<Reservation> result = new ArrayList<>();

        for (Reservation reservation : reservations) {

            if ("CANCELLED".equals(reservation.getReservationStatus())) {
                result.add(reservation);
            }
        }

        return result;
    }

    // Return completed reservations
    public List<Reservation> findCompleted() {

        List<Reservation> result = new ArrayList<>();

        for (Reservation reservation : reservations) {

            if ("COMPLETED".equals(reservation.getReservationStatus())) {
                result.add(reservation);
            }
        }

        return result;
    }
}
