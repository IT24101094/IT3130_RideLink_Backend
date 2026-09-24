package com.ridelink.ridelinkmanagementservice.client;

import com.ridelink.ridelinkmanagementservice.dto.DriverDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "driver-vehicle-service", url = "http://localhost:8082", fallback = DriverServiceClientFallback.class)
public interface DriverServiceClient {

    @GetMapping("/api/drivers/{id}")
    DriverDto getDriverDetails(@PathVariable("id") String id);
}
