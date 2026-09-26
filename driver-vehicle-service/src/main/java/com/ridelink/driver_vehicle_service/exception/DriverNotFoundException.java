package com.ridelink.driver_vehicle_service.exception;

public class DriverNotFoundException extends RuntimeException {

    public DriverNotFoundException(String id) {
        super("Driver not found with id: " + id);
    }
}