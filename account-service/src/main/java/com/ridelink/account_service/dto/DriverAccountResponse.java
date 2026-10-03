package com.ridelink.account_service.dto;

import com.ridelink.account_service.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverAccountResponse {

    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private Role role;
}
