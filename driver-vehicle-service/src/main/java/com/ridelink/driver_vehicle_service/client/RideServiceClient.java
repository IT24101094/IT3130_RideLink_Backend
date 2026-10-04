package com.ridelink.driver_vehicle_service.client;

import com.ridelink.driver_vehicle_service.dto.DriverRideStatsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ride-management-service", url = "${ride-management-service.url:http://localhost:8083}")
public interface RideServiceClient {

    @GetMapping("/api/rides/driver/{driverId}/stats")
    DriverRideStatsDto getDriverStats(@PathVariable("driverId") String driverId);
}
