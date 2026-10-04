package com.ridelink.farepayment.dto;

public class DriverPaymentStatsDto {

    private String driverId;
    private double totalEarnings;
    private double cashTotal;
    private double cardTotal;

    public DriverPaymentStatsDto() {
    }

    public DriverPaymentStatsDto(String driverId, double totalEarnings, double cashTotal, double cardTotal) {
        this.driverId = driverId;
        this.totalEarnings = totalEarnings;
        this.cashTotal = cashTotal;
        this.cardTotal = cardTotal;
    }

    public DriverPaymentStatsDto(double totalEarnings, double cashTotal, double cardTotal) {
        this(null, totalEarnings, cashTotal, cardTotal);
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public double getCashTotal() {
        return cashTotal;
    }

    public void setCashTotal(double cashTotal) {
        this.cashTotal = cashTotal;
    }

    public double getCardTotal() {
        return cardTotal;
    }

    public void setCardTotal(double cardTotal) {
        this.cardTotal = cardTotal;
    }
}
