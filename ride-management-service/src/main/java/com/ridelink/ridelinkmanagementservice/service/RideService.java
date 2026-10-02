package com.ridelink.ridelinkmanagementservice.service;

import com.ridelink.ridelinkmanagementservice.client.AccountServiceClient;
import com.ridelink.ridelinkmanagementservice.client.DriverServiceClient;
import com.ridelink.ridelinkmanagementservice.client.FarePaymentServiceClient;
import com.ridelink.ridelinkmanagementservice.dto.DriverDto;
import com.ridelink.ridelinkmanagementservice.dto.PassengerDto;
import com.ridelink.ridelinkmanagementservice.model.Ride;
import com.ridelink.ridelinkmanagementservice.model.RideStatus;
import com.ridelink.ridelinkmanagementservice.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final AccountServiceClient accountServiceClient;
    private final DriverServiceClient driverServiceClient;
    private final FarePaymentServiceClient farePaymentServiceClient;

    public Ride createRideRequest(String passengerId, String pickupLocation, String destination, String paymentMethod) {
        Ride ride = new Ride();
        ride.setPassengerId(passengerId);
        ride.setPickupLocation(pickupLocation);
        ride.setDestination(destination);
        ride.setPaymentMethod(paymentMethod != null && !paymentMethod.isBlank() ? paymentMethod : "CASH");
        ride.setStatus(RideStatus.REQUESTED);
        ride.setRequestedTime(LocalDateTime.now());

        if (ride.getPassengerId() != null && !ride.getPassengerId().isBlank()) {
            PassengerDto passenger = accountServiceClient.getPassengerDetails(ride.getPassengerId());
            System.out.println("--- Passenger Details Fetched via FeignClient: Name = " + passenger.getName() + ", Phone = " + passenger.getPhoneNumber() + " ---");
        }

        return rideRepository.save(ride);
    }

    public Ride createRideRequest(String passengerId, String pickupLocation, String destination) {
        return createRideRequest(passengerId, pickupLocation, destination, "CASH");
    }

    public Ride assignDriver(String rideId, String driverId) {
        Ride ride = getRideById(rideId);
        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ASSIGNED);

        DriverDto driver = driverServiceClient.getDriverDetails(driverId);
        System.out.println("--- Driver Details Fetched via FeignClient: Name = " + driver.getName() + ", Vehicle = " + driver.getVehicleRegistrationNumber() + " ---");

        return rideRepository.save(ride);
    }

    public Ride acceptRide(String rideId, String driverId) {
        Ride ride = getRideById(rideId);
        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);

        DriverDto driver = driverServiceClient.getDriverDetails(driverId);
        System.out.println("--- Driver Details Fetched via FeignClient: Name = " + driver.getName() + ", Vehicle = " + driver.getVehicleRegistrationNumber() + " ---");

        return rideRepository.save(ride);
    }

    public Ride startRide(String rideId) {
        Ride ride = getRideById(rideId);
        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setStartTime(LocalDateTime.now());
        return rideRepository.save(ride);
    }

    public Ride completeRide(String rideId) {
        Ride ride = getRideById(rideId);
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedTime(LocalDateTime.now());
        Ride savedRide = rideRepository.save(ride);

        String fareResponse = farePaymentServiceClient.calculateFare(savedRide.getId(), savedRide.getPaymentMethod());
        System.out.println("--- Fare calculation triggered via FeignClient for Ride ID: " + savedRide.getId() + " | PaymentMethod: " + savedRide.getPaymentMethod() + " | Response: " + fareResponse + " ---");

        Double finalFare = parseFare(fareResponse);
        if (finalFare != null) {
            savedRide.setFinalFare(finalFare);
        }
        return rideRepository.save(savedRide);
    }

    private Double parseFare(String fareResponse) {
        if (fareResponse == null || fareResponse.isBlank()) {
            return null;
        }
        try {
            return Double.valueOf(fareResponse.trim());
        } catch (NumberFormatException ignored) {
        }
        try {
            com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(fareResponse);
            if (node.has("finalFare") && !node.get("finalFare").isNull()) {
                return node.get("finalFare").asDouble();
            }
            if (node.has("estimatedFare") && !node.get("estimatedFare").isNull()) {
                return node.get("estimatedFare").asDouble();
            }
            if (node.has("amount") && !node.get("amount").isNull()) {
                return node.get("amount").asDouble();
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public Ride cancelRide(String rideId) {
        Ride ride = getRideById(rideId);
        ride.setStatus(RideStatus.CANCELLED);
        return rideRepository.save(ride);
    }

    public Ride updateRideStatus(String rideId, RideStatus newStatus) {
        Ride ride = getRideById(rideId);

        ride.setStatus(newStatus);
        if (newStatus == RideStatus.COMPLETED) {
            ride.setCompletedTime(LocalDateTime.now());
        }

        return rideRepository.save(ride);
    }

    public Ride getRideById(String rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found with id: " + rideId));
    }
}
