package com.ridelink.ridelinkmanagementservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideRequestDto {

    @NotBlank(message = "Pickup location is required")
    @Schema(description = "Pickup location address or coordinates", example = "Colombo 03")
    private String pickupLocation;

    @NotBlank(message = "Destination is required")
    @Schema(description = "Destination location address or coordinates", example = "Kandy")
    private String destination;

    @Schema(description = "Payment method: CASH or CARD", example = "CASH")
    private String paymentMethod;
}
