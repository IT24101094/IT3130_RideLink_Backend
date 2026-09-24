package com.ridelink.ridelinkmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverDto {
    private String id;
    private String name;
    private String licenseNumber;
    private String vehicleRegistrationNumber;
}
