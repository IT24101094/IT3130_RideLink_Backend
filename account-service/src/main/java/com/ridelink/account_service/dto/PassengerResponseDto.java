package com.ridelink.account_service.dto;

import com.ridelink.account_service.model.Passenger;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PassengerResponseDto {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
    private String nic;
    private String address;

    public static PassengerResponseDto fromPassenger(Passenger passenger) {
        if (passenger == null) {
            return null;
        }
        return PassengerResponseDto.builder()
                .id(passenger.getId())
                .name(passenger.getName())
                .email(passenger.getEmail())
                .phoneNumber(passenger.getPhoneNumber())
                .nic(passenger.getNic())
                .address(passenger.getAddress())
                .build();
    }

    public static PassengerResponseDto fromDto(PassengerDto dto) {
        if (dto == null) {
            return null;
        }
        return PassengerResponseDto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .nic(dto.getNic())
                .address(dto.getAddress())
                .build();
    }
}
