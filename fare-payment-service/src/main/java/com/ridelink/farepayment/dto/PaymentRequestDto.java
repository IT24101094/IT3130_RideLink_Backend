package com.ridelink.farepayment.dto;

import jakarta.validation.constraints.NotBlank;

public class PaymentRequestDto {

    private String orderId;

    @NotBlank(message = "Ride ID is required")
    private String rideId;

    private String passengerId;

    private String userId;

    private String driverId;

    private double fareAmount;

    private double amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String paymentStatus;

    public PaymentRequestDto() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRideId() {
        return rideId;
    }

    public void setRideId(String rideId) {
        this.rideId = rideId;
    }

    public String getPassengerId() {
        return passengerId != null ? passengerId : userId;
    }

    public void setPassengerId(String passengerId) {
        this.passengerId = passengerId;
        this.userId = passengerId;
    }

    public String getUserId() {
        return getPassengerId();
    }

    public void setUserId(String userId) {
        setPassengerId(userId);
    }

    public double getFareAmount() {
        return fareAmount > 0 ? fareAmount : amount;
    }

    public void setFareAmount(double fareAmount) {
        this.fareAmount = fareAmount;
        this.amount = fareAmount;
    }

    public double getAmount() {
        return getFareAmount();
    }

    public void setAmount(double amount) {
        setFareAmount(amount);
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }
}