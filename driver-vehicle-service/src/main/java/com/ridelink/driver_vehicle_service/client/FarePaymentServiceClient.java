package com.ridelink.driver_vehicle_service.client;

import com.ridelink.driver_vehicle_service.dto.DriverPaymentStatsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "fare-payment-service", url = "${fare-payment-service.url:http://localhost:8084}")
public interface FarePaymentServiceClient {

    @GetMapping("/api/payments/driver/{driverId}/stats")
    DriverPaymentStatsDto getDriverStats(@PathVariable("driverId") String driverId);
}
