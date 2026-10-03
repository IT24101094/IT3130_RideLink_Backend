package com.ridelink.driver_vehicle_service.dto;

import com.ridelink.driver_vehicle_service.model.Location;
import com.ridelink.driver_vehicle_service.model.Vehicle;

public class DriverProfileResponse {

    private String id;
    private String name;
    private String licenseNumber;
    private boolean available;
    private String serviceArea;
    private Location currentLocation;
    private Vehicle vehicle;

    public DriverProfileResponse() {
    }

    public DriverProfileResponse(
            String id,
            String licenseNumber,
            boolean available,
            String serviceArea,
            Location currentLocation,
            Vehicle vehicle) {

        this.id = id;
        this.licenseNumber = licenseNumber;
        this.available = available;
        this.serviceArea = serviceArea;
        this.currentLocation = currentLocation;
        this.vehicle = vehicle;
    }

    public DriverProfileResponse(
            String id,
            String name,
            String licenseNumber,
            boolean available,
            String serviceArea,
            Location currentLocation,
            Vehicle vehicle) {

        this.id = id;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.available = available;
        this.serviceArea = serviceArea;
        this.currentLocation = currentLocation;
        this.vehicle = vehicle;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}