package com.ridelink.ridelinkmanagementservice.client;

import com.ridelink.ridelinkmanagementservice.dto.DriverDto;
import org.springframework.stereotype.Component;

@Component
public class DriverServiceClientFallback implements DriverServiceClient {

    @Override
    public DriverDto getDriverDetails(String id) {
        return new DriverDto(id, "Mock Driver", "LIC-0000", "XXX-0000");
    }
}
