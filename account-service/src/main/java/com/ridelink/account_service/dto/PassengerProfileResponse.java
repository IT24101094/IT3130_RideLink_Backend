package com.ridelink.account_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerProfileResponse {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String phoneNumber;
    private String nic;
    private String address;

    @Builder.Default
    private List<RideHistoryDto> rideHistory = new ArrayList<>();

    @Builder.Default
    private List<PaymentHistoryDto> paymentHistory = new ArrayList<>();

    public String getPhone() {
        return phone != null ? phone : phoneNumber;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        this.phoneNumber = phone;
    }

    public String getPhoneNumber() {
        return phoneNumber != null ? phoneNumber : phone;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        this.phone = phoneNumber;
    }

    public List<RideHistoryDto> getRides() {
        return rideHistory;
    }

    public void setRides(List<RideHistoryDto> rides) {
        this.rideHistory = rides;
    }

    public List<PaymentHistoryDto> getPayments() {
        return paymentHistory;
    }

    public void setPayments(List<PaymentHistoryDto> payments) {
        this.paymentHistory = payments;
    }
}
