package com.ridelink.driver_vehicle_service.service;

import com.ridelink.driver_vehicle_service.dto.DriverAvailabilityRequest;
import com.ridelink.driver_vehicle_service.dto.DriverLocationRequest;
import com.ridelink.driver_vehicle_service.dto.DriverProfileResponse;
import com.ridelink.driver_vehicle_service.dto.DriverRequest;
import com.ridelink.driver_vehicle_service.dto.DriverResponse;
import com.ridelink.driver_vehicle_service.exception.DriverNotFoundException;
import com.ridelink.driver_vehicle_service.model.Driver;
import com.ridelink.driver_vehicle_service.model.Location;
import com.ridelink.driver_vehicle_service.repository.DriverRepository;

import com.ridelink.driver_vehicle_service.client.FarePaymentServiceClient;
import com.ridelink.driver_vehicle_service.client.RideServiceClient;
import com.ridelink.driver_vehicle_service.dto.DriverPaymentStatsDto;
import com.ridelink.driver_vehicle_service.dto.DriverRideStatsDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {

    private static final Logger log = LoggerFactory.getLogger(DriverService.class);

    private final DriverRepository driverRepository;
    private final RideServiceClient rideServiceClient;
    private final FarePaymentServiceClient farePaymentServiceClient;

    @Autowired
    public DriverService(DriverRepository driverRepository,
                         RideServiceClient rideServiceClient,
                         FarePaymentServiceClient farePaymentServiceClient) {
        this.driverRepository = driverRepository;
        this.rideServiceClient = rideServiceClient;
        this.farePaymentServiceClient = farePaymentServiceClient;
    }

    public DriverService(DriverRepository driverRepository) {
        this(driverRepository, null, null);
    }


    // =========================================================
    // CREATE DRIVER PROFILE
    // =========================================================

    public DriverResponse createDriver(DriverRequest request) {

        Driver driver = new Driver();

        driver.setId(request.getId());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setAvailable(request.isAvailable());
        driver.setServiceArea(request.getServiceArea());
        driver.setCurrentLocation(request.getCurrentLocation());
        driver.setVehicle(request.getVehicle());

        Driver savedDriver = driverRepository.save(driver);

        String vehicleRegistrationNumber = null;

        if (savedDriver.getVehicle() != null) {
            vehicleRegistrationNumber =
                    savedDriver.getVehicle().getRegistrationNumber();
        }

        return new DriverResponse(
                savedDriver.getId(),
                savedDriver.getLicenseNumber(),
                vehicleRegistrationNumber
        );
    }


    // =========================================================
    // UPDATE DRIVER
    // =========================================================

    public DriverResponse updateDriver(
            String id,
            DriverRequest request) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(
                        () -> new DriverNotFoundException(id)
                );

        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setAvailable(request.isAvailable());
        driver.setServiceArea(request.getServiceArea());
        driver.setCurrentLocation(request.getCurrentLocation());
        driver.setVehicle(request.getVehicle());

        Driver updatedDriver =
                driverRepository.save(driver);

        String vehicleRegistrationNumber = null;

        if (updatedDriver.getVehicle() != null) {
            vehicleRegistrationNumber =
                    updatedDriver
                            .getVehicle()
                            .getRegistrationNumber();
        }

        return new DriverResponse(
                updatedDriver.getId(),
                updatedDriver.getLicenseNumber(),
                vehicleRegistrationNumber
        );
    }


    // =========================================================
    // UPDATE DRIVER AVAILABILITY
    // =========================================================

    public DriverProfileResponse updateDriverAvailability(
            String id,
            DriverAvailabilityRequest request) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(
                        () -> new DriverNotFoundException(id)
                );

        driver.setAvailable(request.isAvailable());

        Driver updatedDriver =
                driverRepository.save(driver);

        return new DriverProfileResponse(
                updatedDriver.getId(),
                updatedDriver.getLicenseNumber(),
                updatedDriver.isAvailable(),
                updatedDriver.getServiceArea(),
                updatedDriver.getCurrentLocation(),
                updatedDriver.getVehicle()
        );
    }


    // =========================================================
    // UPDATE DRIVER LOCATION
    // =========================================================

    public DriverProfileResponse updateDriverLocation(
            String id,
            DriverLocationRequest request) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(
                        () -> new DriverNotFoundException(id)
                );

        Location updatedLocation = new Location(
                request.getLatitude(),
                request.getLongitude()
        );

        driver.setCurrentLocation(updatedLocation);

        Driver updatedDriver =
                driverRepository.save(driver);

        return new DriverProfileResponse(
                updatedDriver.getId(),
                updatedDriver.getLicenseNumber(),
                updatedDriver.isAvailable(),
                updatedDriver.getServiceArea(),
                updatedDriver.getCurrentLocation(),
                updatedDriver.getVehicle()
        );
    }


    // =========================================================
    // DELETE DRIVER
    // =========================================================

    public void deleteDriver(String id) {

        if (!driverRepository.existsById(id)) {
            throw new DriverNotFoundException(id);
        }

        driverRepository.deleteById(id);
    }


    // =========================================================
    // GET DRIVER BY ID
    // =========================================================

    public DriverResponse getDriverById(String id) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(
                        () -> new DriverNotFoundException(id)
                );

        String vehicleRegistrationNumber = null;

        if (driver.getVehicle() != null) {
            vehicleRegistrationNumber =
                    driver.getVehicle().getRegistrationNumber();
        }

        return new DriverResponse(
                driver.getId(),
                driver.getLicenseNumber(),
                vehicleRegistrationNumber
        );
    }


    // =========================================================
    // GET FULL DRIVER PROFILE (API COMPOSITION)
    // =========================================================

    public DriverProfileResponse getDriverProfile(String id) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(
                        () -> new DriverNotFoundException(id)
                );

        long totalRides = 0;
        if (rideServiceClient != null) {
            try {
                DriverRideStatsDto rideStats = rideServiceClient.getDriverStats(id);
                if (rideStats != null) {
                    totalRides = rideStats.getTotalRides();
                }
            } catch (Exception e) {
                log.warn("Failed to fetch ride stats for driver {}: {}", id, e.getMessage());
            }
        }

        double totalEarnings = 0.0;
        double cashPayments = 0.0;
        double cardPayments = 0.0;
        if (farePaymentServiceClient != null) {
            try {
                DriverPaymentStatsDto paymentStats = farePaymentServiceClient.getDriverStats(id);
                if (paymentStats != null) {
                    totalEarnings = paymentStats.getTotalEarnings();
                    cashPayments = paymentStats.getCashTotal();
                    cardPayments = paymentStats.getCardTotal();
                }
            } catch (Exception e) {
                log.warn("Failed to fetch payment stats for driver {}: {}", id, e.getMessage());
            }
        }

        // Placeholder for ratings if not yet implemented
        Double rating = null;

        return new DriverProfileResponse(
                driver.getId(),
                driver.getLicenseNumber(),
                driver.isAvailable(),
                driver.getServiceArea(),
                driver.getCurrentLocation(),
                driver.getVehicle(),
                totalRides,
                totalEarnings,
                cashPayments,
                cardPayments,
                rating
        );
    }


    // =========================================================
    // GET AVAILABLE DRIVERS
    // =========================================================

    public List<DriverResponse> getAvailableDrivers(
            String serviceArea) {

        List<Driver> drivers;

        if (serviceArea == null || serviceArea.isBlank()) {

            drivers =
                    driverRepository.findByAvailableTrue();

        } else {

            drivers =
                    driverRepository
                            .findByAvailableTrueAndServiceAreaIgnoreCase(
                                     serviceArea
                            );
        }

        return drivers.stream()
                .map(driver -> {

                    String vehicleRegistrationNumber = null;

                    if (driver.getVehicle() != null) {
                        vehicleRegistrationNumber =
                                driver
                                        .getVehicle()
                                        .getRegistrationNumber();
                    }

                    return new DriverResponse(
                            driver.getId(),
                            driver.getLicenseNumber(),
                            vehicleRegistrationNumber
                    );
                })
                .toList();
    }
}