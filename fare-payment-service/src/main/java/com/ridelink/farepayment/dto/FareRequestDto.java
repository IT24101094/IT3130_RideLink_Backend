package com.ridelink.farepayment.dto;

public class FareRequestDto {

    private String rideId;
    private String userId;
    private double distanceKm;
    private int durationMinutes;
    private double baseFare;
    private double perKmRate;
    private double perMinuteRate;

    public FareRequestDto() {
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public double getBaseFare() {
        return baseFare;
    }

    public void setBaseFare(double baseFare) {
        this.baseFare = baseFare;
    }

    public double getPerKmRate() {
        return perKmRate;
    }

    public void setPerKmRate(double perKmRate) {
        this.perKmRate = perKmRate;
    }

    public double getPerMinuteRate() {
        return perMinuteRate;
    }

    public void setPerMinuteRate(double perMinuteRate) {
        this.perMinuteRate = perMinuteRate;
    }
}