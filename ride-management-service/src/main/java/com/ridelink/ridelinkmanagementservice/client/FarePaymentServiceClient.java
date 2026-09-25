package com.ridelink.ridelinkmanagementservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "fare-payment-service", url = "http://localhost:8084", fallback = FarePaymentServiceClientFallback.class)
public interface FarePaymentServiceClient {

    @PostMapping("/api/fares/calculate")
    String calculateFare(@RequestParam("rideId") String rideId);
}
