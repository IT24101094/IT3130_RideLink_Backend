package com.ridelink.farepayment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ridelink.farepayment.model.Fare;
import com.ridelink.farepayment.repository.FareRepository;
import com.ridelink.farepayment.service.FareService;

class FareServiceTest {

    private FareRepository fareRepository;
    private FareService fareService;

    @BeforeEach
    void setUp() {
        fareRepository = mock(FareRepository.class);
        fareService = new FareService(fareRepository);
    }

    @Test
    void calculateFare_shouldCalculateAndSaveFare() {

        Fare fare = new Fare();

        fare.setRideId("RIDE001");
        fare.setUserId("USER001");
        fare.setDistanceKm(10);
        fare.setDurationMinutes(20);
        fare.setBaseFare(100);
        fare.setPerKmRate(50);
        fare.setPerMinuteRate(5);

        when(fareRepository.save(fare)).thenReturn(fare);

        Fare result = fareService.calculateFare(fare);

        // 100 + (10 × 50) + (20 × 5) = 700
        assertEquals(700.0, result.getEstimatedFare());
        assertEquals(700.0, result.getFinalFare());

        verify(fareRepository, times(1)).save(fare);
    }

    @Test
    void estimateFare_shouldCalculateEstimatedFare() {

        Fare fare = new Fare();

        fare.setDistanceKm(10);
        fare.setDurationMinutes(20);
        fare.setBaseFare(100);
        fare.setPerKmRate(50);
        fare.setPerMinuteRate(5);

        when(fareRepository.save(fare)).thenReturn(fare);

        Fare result = fareService.estimateFare(fare);

        // 100 + (10 × 50) + (20 × 5) = 700
        assertEquals(700.0, result.getEstimatedFare());
        verify(fareRepository, times(1)).save(fare);
    }
}