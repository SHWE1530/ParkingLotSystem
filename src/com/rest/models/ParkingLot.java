package com.rest.models;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {

    private String parkingLotName;
    private List<ParkingSlot> parkingSlots;

    public ParkingLot(String parkingLotName) {
        this.parkingLotName = parkingLotName;
        this.parkingSlots = new ArrayList<>();
    }

    public String getParkingLotName() {
        return parkingLotName;
    }

    public void setParkingLotName(String parkingLotName) {
        this.parkingLotName = parkingLotName;
    }

    public List<ParkingSlot> getParkingSlots() {
        return parkingSlots;
    }

    public void setParkingSlots(List<ParkingSlot> parkingSlots) {
        this.parkingSlots = parkingSlots;
    }

    @Override
    public String toString() {
        return "ParkingLot{" +
                "parkingLotName='" + parkingLotName + '\'' +
                ", parkingSlots=" + parkingSlots +
                '}';
    }
}