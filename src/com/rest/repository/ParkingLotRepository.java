package com.rest.repository;

import com.rest.models.ParkingLot;
import com.rest.models.ParkingLot.Floor;
import com.rest.models.ParkingLot.Slot;

public class ParkingLotRepository {

    private ParkingLot parkingLot;

    public void saveParkingLot(ParkingLot parkingLot) {
        this.parkingLot = parkingLot;
    }

    public ParkingLot getParkingLot() {
        return parkingLot;
    }

    public void addFloor(Floor floor) {
        parkingLot.addFloor(floor);
    }

    public Floor getFloor(int floorNumber) {

        for (Floor floor : parkingLot.getFloors()) {
            if (floor.getFloorNumber() == floorNumber) {
                return floor;
            }
        }

        return null;
    }

    public void addSlot(int floorNumber, Slot slot) {

        Floor floor = getFloor(floorNumber);

        if (floor != null) {
            floor.addSlot(slot);
        }
    }

    public boolean isSlotNumberExists(int floorNumber, int slotNumber) {

        Floor floor = getFloor(floorNumber);

        if (floor == null) {
            return false;
        }

        for (Slot slot : floor.getSlots()) {
            if (slot.getSlotNumber() == slotNumber) {
                return true;
            }
        }

        return false;
    }
}