package com.ridelink.driver_vehicle_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class DriverLoginResponse {

    @Schema(description = "JWT authentication token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;

    @Schema(description = "Token type", example = "Bearer")
    private String type = "Bearer";

    @Schema(description = "Driver ID", example = "6abe0936ec7ae3879b8adecb")
    private String driverId;

    @Schema(description = "Driver name", example = "Nimal Perera")
    private String name;

    @Schema(description = "Driver license number", example = "B1234567")
    private String licenseNumber;

    public DriverLoginResponse() {
    }

    public DriverLoginResponse(String token) {
        this.token = token;
    }

    public DriverLoginResponse(String token, String driverId, String name, String licenseNumber) {
        this.token = token;
        this.type = "Bearer";
        this.driverId = driverId;
        this.name = name;
        this.licenseNumber = licenseNumber;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
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
}
