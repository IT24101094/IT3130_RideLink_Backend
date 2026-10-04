package com.ridelink.account_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RideHistoryDto {
    private String id;
    private String passengerId;
    private String driverId;
    private String pickupLocation;
    private String destination;
    private String status;
    private Double estimatedFare;
    private Double finalFare;
    private String paymentMethod;
    private LocalDateTime requestedTime;
    private LocalDateTime startTime;
    private LocalDateTime completedTime;
}
