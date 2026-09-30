package com.ridelink.farepayment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class PaymentRequestDto {

    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Ride ID is required")
    private String rideId;

    @NotBlank(message = "User ID is required")
    private String userId;

    @Positive(message = "Amount must be greater than 0")
    private double amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    @NotBlank(message = "Payment status is required")
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
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
}