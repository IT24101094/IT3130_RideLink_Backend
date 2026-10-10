package com.ridelink.ridelinkmanagementservice.client;

import org.springframework.stereotype.Component;

@Component
public class FarePaymentServiceClientFallback implements FarePaymentServiceClient {

    @Override
    public Object calculateFare(String rideId, String paymentMethod) {
        return "Mock Fare Calculation Triggered";
    }

    @Override
    public Object getLatestEstimate(String userId) {
        return null;
    }
}
