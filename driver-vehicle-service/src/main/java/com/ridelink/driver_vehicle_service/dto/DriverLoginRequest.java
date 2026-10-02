package com.ridelink.driver_vehicle_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class DriverLoginRequest {

    @Schema(description = "Driver license number or email", example = "B1234567")
    private String licenseNumber;

    @Schema(description = "Driver email (optional alternative to license number)", example = "driver@example.com")
    private String email;

    @Schema(description = "Driver password", example = "Password123")
    private String password;

    public DriverLoginRequest() {
    }

    public DriverLoginRequest(String licenseNumber, String password) {
        this.licenseNumber = licenseNumber;
        this.password = password;
    }

    public DriverLoginRequest(String licenseNumber, String email, String password) {
        this.licenseNumber = licenseNumber;
        this.email = email;
        this.password = password;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
