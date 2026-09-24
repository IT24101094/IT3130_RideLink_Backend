package com.ridelink.ridelinkmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerDto {
    private String id;
    private String name;
    private String phoneNumber;
}
