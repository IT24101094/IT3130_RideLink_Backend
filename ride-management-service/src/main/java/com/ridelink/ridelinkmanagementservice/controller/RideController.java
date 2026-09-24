package com.ridelink.ridelinkmanagementservice.controller;

import com.ridelink.ridelinkmanagementservice.model.Ride;
import com.ridelink.ridelinkmanagementservice.model.RideStatus;
import com.ridelink.ridelinkmanagementservice.service.RideService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @Data
    public static class RideRequest {
        @NotBlank(message = "Field is mandatory")
        private String passengerId;

        @NotBlank(message = "Field is mandatory")
        private String pickupLocation;

        @NotBlank(message = "Field is mandatory")
        private String destination;
    }

    @PostMapping
    public ResponseEntity<Ride> createRide(@Valid @RequestBody RideRequest request) {
        Ride createdRide = rideService.createRideRequest(
                request.getPassengerId(),
                request.getPickupLocation(),
                request.getDestination()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRide);
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<Ride> assignDriver(
            @PathVariable String id,
            @RequestParam String driverId) {
        Ride updatedRide = rideService.assignDriver(id, driverId);
        return ResponseEntity.ok(updatedRide);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<Ride> acceptRide(
            @PathVariable String id,
            @RequestParam String driverId) {
        Ride updatedRide = rideService.acceptRide(id, driverId);
        return ResponseEntity.ok(updatedRide);
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<Ride> startRide(@PathVariable String id) {
        Ride updatedRide = rideService.startRide(id);
        return ResponseEntity.ok(updatedRide);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Ride> completeRide(@PathVariable String id) {
        Ride updatedRide = rideService.completeRide(id);
        return ResponseEntity.ok(updatedRide);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Ride> cancelRide(@PathVariable String id) {
        Ride updatedRide = rideService.cancelRide(id);
        return ResponseEntity.ok(updatedRide);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Ride> updateRideStatus(
            @PathVariable String id,
            @RequestParam RideStatus newStatus) {
        Ride updatedRide = rideService.updateRideStatus(id, newStatus);
        return ResponseEntity.ok(updatedRide);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ride> getRideById(@PathVariable String id) {
        Ride ride = rideService.getRideById(id);
        return ResponseEntity.ok(ride);
    }
}
