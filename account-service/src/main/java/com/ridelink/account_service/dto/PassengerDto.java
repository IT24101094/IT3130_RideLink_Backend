package com.ridelink.account_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PassengerDto {
    private String id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private String nic;
    private String address;
}
