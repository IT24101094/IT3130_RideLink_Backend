package com.ridelink.farepayment.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ridelink.farepayment.dto.FareRequestDto;
import com.ridelink.farepayment.model.Fare;
import com.ridelink.farepayment.service.FareService;

@RestController
@RequestMapping("/api/fares")
@SecurityRequirement(name = "Bearer Authentication")
public class FareController {

    private final FareService fareService;

    public FareController(FareService fareService) {
        this.fareService = fareService;
    }

    @GetMapping("/latest-estimate")
    public ResponseEntity<Fare> getLatestEstimate(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String passengerId) {

        String targetUserId = (userId != null && !userId.isBlank()) ? userId : passengerId;
        if (targetUserId == null || targetUserId.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        return fareService.getLatestEstimateByUserId(targetUserId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/calculate")
    public ResponseEntity<Fare> calculateFare(
            @RequestParam String rideId,
            @RequestParam(required = false) String paymentMethod) {

        java.util.Optional<Fare> existingFare = fareService.getFareByRideId(rideId);
        if (existingFare.isPresent()) {
            return ResponseEntity.ok(existingFare.get());
        }

        Fare fare = new Fare();

        fare.setRideId(rideId);
        fare.setBaseFare(100.0);
        fare.setDistanceKm(10.0);
        fare.setPerKmRate(50.0);
        fare.setDurationMinutes(20);
        fare.setPerMinuteRate(5.0);

        Fare calculatedFare = fareService.calculateFare(fare);

        return ResponseEntity.ok(calculatedFare);
    }

    @PostMapping("/estimate")
    public ResponseEntity<Fare> estimateFare(
            @RequestBody FareRequestDto request) {

        Fare fare = new Fare();

        fare.setRideId(request.getRideId());
        fare.setUserId(request.getUserId());
        fare.setDistanceKm(request.getDistanceKm());
        fare.setDurationMinutes(request.getDurationMinutes());
        fare.setBaseFare(request.getBaseFare());
        fare.setPerKmRate(request.getPerKmRate());
        fare.setPerMinuteRate(request.getPerMinuteRate());

        Fare estimatedFare = fareService.estimateFare(fare);

        return ResponseEntity.ok(estimatedFare);
    }
}