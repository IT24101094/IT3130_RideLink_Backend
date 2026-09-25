package com.ridelink.ridelinkmanagementservice.model;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "rides")
public class Ride {

    @Id
    private String id;

    @NotBlank(message = "Field is mandatory")
    private String passengerId;

    private String driverId;

    @NotBlank(message = "Field is mandatory")
    private String pickupLocation;

    @NotBlank(message = "Field is mandatory")
    private String destination;

    private RideStatus status;

    private Double estimatedFare;

    private Double finalFare;

    private LocalDateTime requestedTime;

    private LocalDateTime completedTime;
}
