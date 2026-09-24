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

    public Ride createRideRequest(String passengerId, String pickupLocation, String destination) {
        Ride ride = new Ride();
        ride.setPassengerId(passengerId);
        ride.setPickupLocation(pickupLocation);
        ride.setDestination(destination);
        ride.setStatus(RideStatus.REQUESTED);
        ride.setRequestedTime(LocalDateTime.now());

        PassengerDto passenger = accountServiceClient.getPassengerDetails(ride.getPassengerId());
        System.out.println("--- Passenger Details Fetched via FeignClient: Name = " + passenger.getName() + ", Phone = " + passenger.getPhoneNumber() + " ---");

        return rideRepository.save(ride);
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
        return rideRepository.save(ride);
    }

    public Ride completeRide(String id) {
        Ride ride = getRideById(id);
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedTime(LocalDateTime.now());
        Ride savedRide = rideRepository.save(ride);

        farePaymentServiceClient.calculateFare(id);
        System.out.println("--- Fare calculation triggered via FeignClient for Ride ID: " + id + " ---");

        return savedRide;
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
