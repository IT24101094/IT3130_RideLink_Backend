package com.ridelink.farepayment.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;

@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    @Field("order_id")
    private String orderId;

    @NotBlank(message = "Ride ID is required")
    private String rideId;

    private String passengerId;

    private String userId;

    private double fareAmount;

    private double amount;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;

    private String paymentStatus;

    private String transactionId;

    public Payment() {
    }

    public Payment(
            String orderId,
            String rideId,
            String passengerId,
            double fareAmount,
            String paymentMethod,
            String paymentStatus,
            String transactionId) {
        this.orderId = orderId;
        this.rideId = rideId;
        this.passengerId = passengerId;
        this.userId = passengerId;
        this.fareAmount = fareAmount;
        this.amount = fareAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.transactionId = transactionId;
    }

    public Payment(
            String orderId,
            String rideId,
            String passengerId,
            double fareAmount,
            String paymentMethod,
            String paymentStatus) {
        this(orderId, rideId, passengerId, fareAmount, paymentMethod, paymentStatus, null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}