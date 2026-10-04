package com.ridelink.driver_vehicle_service.dto;

public class DriverRideStatsDto {

    private String driverId;
    private long totalRides;

    public DriverRideStatsDto() {
    }

    public DriverRideStatsDto(long totalRides) {
        this.totalRides = totalRides;
    }

    public DriverRideStatsDto(String driverId, long totalRides) {
        this.driverId = driverId;
        this.totalRides = totalRides;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public long getTotalRides() {
        return totalRides;
    }

    public void setTotalRides(long totalRides) {
        this.totalRides = totalRides;
    }
}
