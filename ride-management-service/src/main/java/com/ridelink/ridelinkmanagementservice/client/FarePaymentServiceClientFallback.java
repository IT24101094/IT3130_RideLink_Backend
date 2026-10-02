package com.ridelink.ridelinkmanagementservice.client;

import org.springframework.stereotype.Component;

@Component
public class FarePaymentServiceClientFallback implements FarePaymentServiceClient {

    @Override
    public String calculateFare(String rideId, String paymentMethod) {
        return "Mock Fare Calculation Triggered";
    }
}
