package com.rest.models;

public class ParkingSlot {

    private int slotNumber;
    private String slotType;
    private String status;

    public ParkingSlot(int slotNumber, String slotType, String status) {
        this.slotNumber = slotNumber;
        this.slotType = slotType;
        this.status = status;
    }

    public int getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(int slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getSlotType() {
        return slotType;
    }

    public void setSlotType(String slotType) {
        this.slotType = slotType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ParkingSlot{" +
                "slotNumber=" + slotNumber +
                ", slotType='" + slotType + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}