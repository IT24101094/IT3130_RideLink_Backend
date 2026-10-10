package com.ridelink.farepayment.service;

import org.springframework.stereotype.Service;

import com.ridelink.farepayment.model.Fare;
import com.ridelink.farepayment.repository.FareRepository;

@Service
public class FareService {

    private final FareRepository fareRepository;

    public FareService(FareRepository fareRepository) {
        this.fareRepository = fareRepository;
    }

    public Fare calculateFare(Fare fare) {

        double calculatedFare =
                fare.getBaseFare()
                + (fare.getDistanceKm() * fare.getPerKmRate())
                + (fare.getDurationMinutes() * fare.getPerMinuteRate());

        fare.setEstimatedFare(calculatedFare);
        fare.setFinalFare(calculatedFare);

        return fareRepository.save(fare);
    }

    public Fare estimateFare(Fare fare) {

        double estimatedFare =
                fare.getBaseFare()
                + (fare.getDistanceKm() * fare.getPerKmRate())
                + (fare.getDurationMinutes() * fare.getPerMinuteRate());

        fare.setEstimatedFare(estimatedFare);
        fare.setFinalFare(estimatedFare);

        return fareRepository.save(fare);
    }

    public java.util.Optional<Fare> getLatestEstimateByUserId(String userId) {
        return fareRepository.findFirstByUserIdOrderByIdDesc(userId);
    }

    public java.util.Optional<Fare> getFareByRideId(String rideId) {
        return fareRepository.findFirstByRideIdOrderByIdDesc(rideId)
                .or(() -> fareRepository.findByRideId(rideId));
    }
}