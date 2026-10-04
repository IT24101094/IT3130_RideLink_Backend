package com.ridelink.account_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentHistoryDto {
    private String id;
    private String orderId;
    private String rideId;
    private String passengerId;
    private String userId;
    private String driverId;
    private double fareAmount;
    private double amount;
    private String paymentMethod;
    private String paymentStatus;
    private String transactionId;
}
