package com.rest.repository;

import com.rest.models.ParkingLot;

public class ParkingLotRepository {

    private ParkingLot parkingLot;

    public void saveParkingLot(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    public ParkingLot getParkingLot() {
        return parkingLot;
    }

    public boolean parkingLotExists() {
        return parkingLot != null;
    }
}