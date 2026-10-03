package com.ridelink.account_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "driver_accounts")
public class DriverAccount {

    @Id
    private String id;
    private String name;
    private String email;
    private String password;
    private String phoneNumber;

    @Builder.Default
    private Role role = Role.DRIVER;

    public DriverAccount(String id, String email, String password, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role != null ? role : Role.DRIVER;
    }
}
