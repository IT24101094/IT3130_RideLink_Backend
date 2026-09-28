package com.ridelink.farepayment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ridelink.farepayment.model.Fare;
import com.ridelink.farepayment.service.FareService;

@RestController
@RequestMapping("/api/fares")
public class FareController {

    private final FareService fareService;

    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    @PostMapping("/calculate")
    public ResponseEntity<Fare> calculateFare(@RequestParam String rideId) {

        Fare fare = new Fare();

        // Ride information
        fare.setRideId(rideId);

        // Test fare values
        fare.setBaseFare(100.0);
        fare.setDistanceKm(10.0);
        fare.setPerKmRate(50.0);
        fare.setDurationMinutes(20);
        fare.setPerMinuteRate(5.0);

        // Calculate and save the fare
        Fare calculatedFare = fareService.calculateFare(fare);

        return ResponseEntity.ok(calculatedFare);
    }

    @PostMapping("/estimate")
    public ResponseEntity<Fare> estimateFare(@RequestBody Fare fare) {

        Fare estimatedFare = fareService.estimateFare(fare);

        return ResponseEntity.ok(estimatedFare);
    }
}