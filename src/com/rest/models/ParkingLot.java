package com.rest.models;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {

    private String name;
    private String address;
    private List<Floor> floors;

    public ParkingLot(String name, String address) {
        this.name = name;
        this.address = address;
        this.floors = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public List<Floor> getFloors() {
        return floors;
    }

    public void addFloor(Floor floor) {
        floors.add(floor);
    }

    public static class Floor {

        private int floorNumber;
        private boolean closed;
        private List<Slot> slots;

        public Floor(int floorNumber) {
            this.floorNumber = floorNumber;
            this.closed = false;
            this.slots = new ArrayList<>();
        }

        public int getFloorNumber() {
            return floorNumber;
        }

        public boolean isClosed() {
            return closed;
        }

        public void setClosed(boolean closed) {
            this.closed = closed;
        }

        public List<Slot> getSlots() {
            return slots;
        }

        public void addSlot(Slot slot) {
            slots.add(slot);
        }
    }

    public static class Slot {

        private int slotNumber;
        private String type;
        private String status;

        public Slot(int slotNumber, String type) {
            this.slotNumber = slotNumber;
            this.type = type;
            this.status = "Free";
        }

        public int getSlotNumber() {
            return slotNumber;
        }

        public String getType() {
            return type;
        }

        public String getStatus() {
            return status;
        }

        public void setType(String type) {
            if (!status.equals("Occupied")) {
                this.type = type;
            } else {
                throw new IllegalStateException(
                    "Cannot change type of an occupied slot."
                );
            }
        }

        public void setStatus(String status) {
            if (status.equals("Under Maintenance")
                    && !this.status.equals("Free")) {
                throw new IllegalStateException(
                    "Only a Free slot can be marked Under Maintenance."
                );
            }

            if (this.status.equals("Under Maintenance")
                    && status.equals("Occupied")) {
                throw new IllegalStateException(
                    "Maintenance slot cannot be occupied."
                );
            }

            this.status = status;
        }
    }
}