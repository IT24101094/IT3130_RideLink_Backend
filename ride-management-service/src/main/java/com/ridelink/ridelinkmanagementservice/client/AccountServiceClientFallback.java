package com.ridelink.ridelinkmanagementservice.client;

import com.ridelink.ridelinkmanagementservice.dto.PassengerDto;
import org.springframework.stereotype.Component;

@Component
public class AccountServiceClientFallback implements AccountServiceClient {

    @Override
    public PassengerDto getPassengerDetails(String id) {
        return new PassengerDto(id, "Mock Passenger", "000-000-0000");
    }
}
