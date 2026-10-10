package com.ridelink.ridelinkmanagementservice.service;

import com.ridelink.ridelinkmanagementservice.client.AccountServiceClient;
import com.ridelink.ridelinkmanagementservice.client.DriverServiceClient;
import com.ridelink.ridelinkmanagementservice.client.FarePaymentServiceClient;
import com.ridelink.ridelinkmanagementservice.dto.DriverDto;
import com.ridelink.ridelinkmanagementservice.dto.DriverStatsDto;
import com.ridelink.ridelinkmanagementservice.dto.PassengerDto;
import com.ridelink.ridelinkmanagementservice.exception.BadRequestException;
import com.ridelink.ridelinkmanagementservice.exception.ResourceNotFoundException;
import com.ridelink.ridelinkmanagementservice.model.Ride;
import com.ridelink.ridelinkmanagementservice.model.RideStatus;
import com.ridelink.ridelinkmanagementservice.repository.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService {

    private final RideRepository rideRepository;
    private final AccountServiceClient accountServiceClient;
    private final DriverServiceClient driverServiceClient;
    private final FarePaymentServiceClient farePaymentServiceClient;

    public Ride createRideRequest(String passengerId, String pickupLocation, String destination, String paymentMethod, Double estimatedFare) {
        Ride ride = new Ride();
        ride.setPassengerId(passengerId);
        ride.setPickupLocation(pickupLocation);
        ride.setDestination(destination);
        ride.setPaymentMethod(paymentMethod != null && !paymentMethod.isBlank() ? paymentMethod : "CASH");
        ride.setEstimatedFare(estimatedFare);
        ride.setStatus(RideStatus.REQUESTED);
        ride.setRequestedTime(LocalDateTime.now());

        if (ride.getPassengerId() != null && !ride.getPassengerId().isBlank()) {
            PassengerDto passenger = accountServiceClient.getPassengerDetails(ride.getPassengerId());
            System.out.println("--- Passenger Details Fetched via FeignClient: Name = " + passenger.getName() + ", Phone = " + passenger.getPhoneNumber() + " ---");
        }

        return rideRepository.save(ride);
    }

    public Ride createRideRequest(String passengerId, String pickupLocation, String destination, String paymentMethod) {
        return createRideRequest(passengerId, pickupLocation, destination, paymentMethod, null);
    }

    public Ride createRideRequest(String passengerId, String pickupLocation, String destination) {
        return createRideRequest(passengerId, pickupLocation, destination, "CASH", null);
    }

    public Ride assignDriver(String rideId, String driverId) {
        Ride ride = getRideById(rideId);

        if (ride.getStatus() == RideStatus.COMPLETED) {
            throw new BadRequestException("Ride is already completed");
        }
        if (ride.getStatus() == RideStatus.IN_PROGRESS) {
            throw new BadRequestException("Ride is already in progress");
        }
        if (ride.getStatus() == RideStatus.CANCELLED) {
            throw new BadRequestException("Cannot assign driver to a cancelled ride");
        }

        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ASSIGNED);

        DriverDto driver = driverServiceClient.getDriverDetails(driverId);
        System.out.println("--- Driver Details Fetched via FeignClient: Name = " + driver.getName() + ", Vehicle = " + driver.getVehicleRegistrationNumber() + " ---");

        return rideRepository.save(ride);
    }

    public Ride acceptRide(String rideId, String driverId) {
        Ride ride = getRideById(rideId);

        if (ride.getStatus() == RideStatus.COMPLETED) {
            throw new BadRequestException("Ride is already completed");
        }
        if (ride.getStatus() == RideStatus.IN_PROGRESS) {
            throw new BadRequestException("Ride is already in progress");
        }
        if (ride.getStatus() == RideStatus.CANCELLED) {
            throw new BadRequestException("Cannot accept a cancelled ride");
        }
        if (ride.getStatus() == RideStatus.ACCEPTED) {
            throw new BadRequestException("Ride is already accepted");
        }

        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);

        DriverDto driver = driverServiceClient.getDriverDetails(driverId);
        System.out.println("--- Driver Details Fetched via FeignClient: Name = " + driver.getName() + ", Vehicle = " + driver.getVehicleRegistrationNumber() + " ---");

        return rideRepository.save(ride);
    }

    public Ride startRide(String rideId) {
        Ride ride = getRideById(rideId);

        if (ride.getStatus() == RideStatus.COMPLETED) {
            throw new BadRequestException("Ride is already completed");
        }
        if (ride.getStatus() == RideStatus.IN_PROGRESS) {
            throw new BadRequestException("Ride is already in progress");
        }
        if (ride.getStatus() == RideStatus.CANCELLED) {
            throw new BadRequestException("Cannot start a cancelled ride");
        }

        ride.setStatus(RideStatus.IN_PROGRESS);
        ride.setStartTime(LocalDateTime.now());
        return rideRepository.save(ride);
    }

    public Ride completeRide(String rideId) {
        Ride ride = getRideById(rideId);

        if (ride.getStatus() == RideStatus.COMPLETED) {
            throw new BadRequestException("Ride is already completed");
        }
        if (ride.getStatus() == RideStatus.CANCELLED) {
            throw new BadRequestException("Cannot complete a cancelled ride");
        }

        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedTime(LocalDateTime.now());

        // 1. Check if the ride already has an estimated fare (> 0)
        Double finalFare = null;
        if (ride.getEstimatedFare() != null && ride.getEstimatedFare() > 0) {
            finalFare = ride.getEstimatedFare();
        }

        // 2. Fetch or trigger fare calculation from fare-payment-service via Feign client
        try {
            Object fareResponse = farePaymentServiceClient.calculateFare(ride.getId(), ride.getPaymentMethod());
            System.out.println("--- Fare calculation triggered via FeignClient for Ride ID: " + ride.getId()
                    + " | PaymentMethod: " + ride.getPaymentMethod() + " | Response: " + fareResponse + " ---");

            // If finalFare was not already determined from estimatedFare, parse from Feign response
            if (finalFare == null) {
                finalFare = parseFare(fareResponse);
            }
        } catch (Exception e) {
            System.err.println("--- Failed to calculate fare via FarePaymentServiceClient for Ride ID: " + ride.getId() + " - " + e.getMessage() + " ---");
        }

        // 3. Fallback: calculate based on actual duration if available, or default base fare
        if (finalFare == null) {
            if (ride.getStartTime() != null && ride.getCompletedTime() != null) {
                long durationMinutes = Duration.between(ride.getStartTime(), ride.getCompletedTime()).toMinutes();
                // Base fare: 100.0, per minute: 5.0 (minimum 1 minute)
                finalFare = 100.0 + (Math.max(1, durationMinutes) * 5.0);
            } else {
                finalFare = 100.0;
            }
        }

        // 4. Properly set the finalFare field in the Ride model before saving it to the database
        ride.setFinalFare(finalFare);
        if (ride.getEstimatedFare() == null) {
            ride.setEstimatedFare(finalFare);
        }

        return rideRepository.save(ride);
    }

    private Double parseFare(Object fareResponse) {
        if (fareResponse == null) {
            return null;
        }
        if (fareResponse instanceof Number number) {
            return number.doubleValue();
        }
        if (fareResponse instanceof java.util.Map<?, ?> map) {
            Object finalFare = map.get("finalFare");
            if (finalFare instanceof Number num) return num.doubleValue();
            if (finalFare != null) {
                try { return Double.valueOf(finalFare.toString()); } catch (Exception ignored) {}
            }
            Object estimatedFare = map.get("estimatedFare");
            if (estimatedFare instanceof Number num) return num.doubleValue();
            if (estimatedFare != null) {
                try { return Double.valueOf(estimatedFare.toString()); } catch (Exception ignored) {}
            }
            Object amount = map.get("amount");
            if (amount instanceof Number num) return num.doubleValue();
            if (amount != null) {
                try { return Double.valueOf(amount.toString()); } catch (Exception ignored) {}
            }
            return null;
        }

        String str = fareResponse.toString();
        if (str.isBlank()) {
            return null;
        }
        try {
            return Double.valueOf(str.trim());
        } catch (NumberFormatException ignored) {
        }
        try {
            com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(str);
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

        if (ride.getStatus() == RideStatus.COMPLETED) {
            throw new BadRequestException("Ride is already completed");
        }
        if (ride.getStatus() == RideStatus.CANCELLED) {
            throw new BadRequestException("Ride is already cancelled");
        }
        if (ride.getStatus() == RideStatus.IN_PROGRESS) {
            throw new BadRequestException("Cannot cancel a ride that is in progress");
        }

        ride.setStatus(RideStatus.CANCELLED);
        return rideRepository.save(ride);
    }

    public Ride updateRideStatus(String rideId, RideStatus newStatus) {
        Ride ride = getRideById(rideId);

        if (ride.getStatus() == RideStatus.COMPLETED) {
            throw new BadRequestException("Ride is already completed");
        }
        if (ride.getStatus() == RideStatus.CANCELLED) {
            throw new BadRequestException("Ride is already cancelled");
        }
        if (ride.getStatus() == newStatus) {
            throw new BadRequestException("Ride is already in " + newStatus + " status");
        }

        ride.setStatus(newStatus);
        if (newStatus == RideStatus.COMPLETED) {
            ride.setCompletedTime(LocalDateTime.now());
        } else if (newStatus == RideStatus.IN_PROGRESS && ride.getStartTime() == null) {
            ride.setStartTime(LocalDateTime.now());
        }

        return rideRepository.save(ride);
    }

    public Ride getRideById(String rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride not found with id: " + rideId));
    }

    public List<Ride> getPassengerRideHistory(String passengerId) {
        return rideRepository.findByPassengerId(passengerId);
    }

    public List<Ride> getRideHistoryByPassengerId(String passengerId) {
        return getPassengerRideHistory(passengerId);
    }

    public long getCompletedRidesCountForDriver(String driverId) {
        List<Ride> rides = rideRepository.findByDriverId(driverId);
        if (rides == null) {
            return 0;
        }
        return rides.stream()
                .filter(ride -> ride.getStatus() == RideStatus.COMPLETED)
                .count();
    }

    public long getTotalCompletedRidesByDriverId(String driverId) {
        return getCompletedRidesCountForDriver(driverId);
    }

    public DriverStatsDto getDriverStats(String driverId) {
        long count = getCompletedRidesCountForDriver(driverId);
        return new DriverStatsDto(driverId, count);
    }
}
