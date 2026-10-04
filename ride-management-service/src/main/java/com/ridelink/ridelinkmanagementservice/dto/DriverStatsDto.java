package com.ridelink.ridelinkmanagementservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DriverStatsDto {
    private String driverId;
    private long totalRides;

    public DriverStatsDto(long totalRides) {
        this.totalRides = totalRides;
    }
}
