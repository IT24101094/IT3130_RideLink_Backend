package com.ridelink.driver_vehicle_service.dto;

public class DriverResponse {

    private String id;
    private String name;
    private String licenseNumber;
    private String vehicleRegistrationNumber;

    public DriverResponse() {
    }

    public DriverResponse(String id,
                          String name,
                          String licenseNumber,
                          String vehicleRegistrationNumber) {
        this.id = id;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
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

    public String getVehicleRegistrationNumber() {
        return vehicleRegistrationNumber;
    }

    public void setVehicleRegistrationNumber(String vehicleRegistrationNumber) {
        this.vehicleRegistrationNumber = vehicleRegistrationNumber;
    }
}