package com.ridelink.driver_vehicle_service.dto;

import com.ridelink.driver_vehicle_service.model.Location;
import com.ridelink.driver_vehicle_service.model.Vehicle;
import jakarta.validation.constraints.NotBlank;

public class DriverRequest {

    @NotBlank(message = "Driver name is required")
    private String name;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    private String email;

    private String password;

    private boolean available;

    @NotBlank(message = "Service area is required")
    private String serviceArea;

    private Location currentLocation;

    private Vehicle vehicle;

    // Default constructor
    public DriverRequest() {
    }

    // Constructor without email/password (backward compatibility)
    public DriverRequest(
            String name,
            String licenseNumber,
            boolean available,
            String serviceArea,
            Location currentLocation,
            Vehicle vehicle) {

        this.name = name;
        this.licenseNumber = licenseNumber;
        this.available = available;
        this.serviceArea = serviceArea;
        this.currentLocation = currentLocation;
        this.vehicle = vehicle;
    }

    // Full constructor including email and password
    public DriverRequest(
            String name,
            String licenseNumber,
            String email,
            String password,
            boolean available,
            String serviceArea,
            Location currentLocation,
            Vehicle vehicle) {

        this.name = name;
        this.licenseNumber = licenseNumber;
        this.email = email;
        this.password = password;
        this.available = available;
        this.serviceArea = serviceArea;
        this.currentLocation = currentLocation;
        this.vehicle = vehicle;
    }

    // Name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // License Number
    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    // Email
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Password
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Availability
    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    // Service Area
    public String getServiceArea() {
        return serviceArea;
    }

    public void setServiceArea(String serviceArea) {
        this.serviceArea = serviceArea;
    }

    // Current Location
    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    // Vehicle
    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
}