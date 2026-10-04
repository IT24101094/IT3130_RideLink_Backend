package com.ridelink.account_service.client;

import com.ridelink.account_service.dto.RideHistoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "ride-management-service", url = "${ride-management-service.url:http://localhost:8083}")
public interface RideServiceClient {

    @GetMapping("/api/rides/passenger/{passengerId}/history")
    List<RideHistoryDto> getPassengerRideHistory(@PathVariable("passengerId") String passengerId);
}
