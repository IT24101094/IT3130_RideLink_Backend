package com.ridelink.ridelinkmanagementservice.client;

import com.ridelink.ridelinkmanagementservice.dto.PassengerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "account-service", url = "http://localhost:8081", fallback = AccountServiceClientFallback.class)
public interface AccountServiceClient {

    @GetMapping("/api/accounts/passengers/{id}")
    PassengerDto getPassengerDetails(@PathVariable("id") String id);
}
