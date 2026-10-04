package com.ridelink.account_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "passengers")
public class Passenger {
    @Id
    private String id;
    private String name;
    private String email;

    @JsonIgnore
    private String password;
    private String phoneNumber;
    private String nic;
    private String address;
}
