package com.ridelink.driver_vehicle_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class DriverLoginRequest {

    @Schema(description = "Driver license number", example = "B1234567")
    private String licenseNumber;

    public DriverLoginRequest() {
    }

    public DriverLoginRequest(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }
}

