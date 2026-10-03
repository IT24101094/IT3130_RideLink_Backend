package com.ridelink.driver_vehicle_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.ridelink.driver_vehicle_service.model.Location;
import com.ridelink.driver_vehicle_service.model.Vehicle;
import jakarta.validation.constraints.NotBlank;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DriverRequest {

    private String id;

    private String name;

    @NotBlank(message = "License number is required")
    private String licenseNumber;

    private boolean available;

    @NotBlank(message = "Service area is required")
    private String serviceArea;

    private Location currentLocation;

    private Vehicle vehicle;

    // Default constructor
    public DriverRequest() {
    }

    // Constructor with external id and operational details
    public DriverRequest(
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

    // Constructor with operational details (without id)
    public DriverRequest(
            String licenseNumber,
            boolean available,
            String serviceArea,
            Location currentLocation,
            Vehicle vehicle) {

        this.licenseNumber = licenseNumber;
        this.available = available;
        this.serviceArea = serviceArea;
        this.currentLocation = currentLocation;
        this.vehicle = vehicle;
    }

    // Full constructor with name for backward compatibility
    public DriverRequest(
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

    // ID
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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