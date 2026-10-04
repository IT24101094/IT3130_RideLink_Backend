package com.ridelink.farepayment.dto;

public class DriverStatsDto extends DriverPaymentStatsDto {

    public DriverStatsDto() {
        super();
    }

    public DriverStatsDto(String driverId, double totalEarnings, double cashTotal, double cardTotal) {
        super(driverId, totalEarnings, cashTotal, cardTotal);
    }

    public DriverStatsDto(double totalEarnings, double cashTotal, double cardTotal) {
        super(totalEarnings, cashTotal, cardTotal);
    }
}
