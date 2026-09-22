package com.ridelink.ridelinkmanagementservice.service;

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

    public Ride createRideRequest(String passengerId, String pickupLocation, String destination) {
        Ride ride = new Ride();
        ride.setPassengerId(passengerId);
        ride.setPickupLocation(pickupLocation);
        ride.setDestination(destination);
        ride.setStatus(RideStatus.REQUESTED);
        ride.setRequestedTime(LocalDateTime.now());

        return rideRepository.save(ride);
    }

    public Ride assignDriver(String rideId, String driverId) {
        Ride ride = getRideById(rideId);

        if (ride.getStatus() != RideStatus.REQUESTED) {
            throw new RuntimeException("Cannot assign driver: Ride status is not REQUESTED");
        }

        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ASSIGNED);

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
