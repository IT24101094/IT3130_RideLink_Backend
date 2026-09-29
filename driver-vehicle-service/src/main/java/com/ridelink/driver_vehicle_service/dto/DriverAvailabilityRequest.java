package com.ridelink.driver_vehicle_service.dto;

public class DriverAvailabilityRequest {

    private boolean available;

    public DriverAvailabilityRequest() {
    }

    public DriverAvailabilityRequest(boolean available) {
        this.available = available;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}