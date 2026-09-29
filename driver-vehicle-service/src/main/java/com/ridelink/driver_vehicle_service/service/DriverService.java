package com.ridelink.driver_vehicle_service.service;

import com.ridelink.driver_vehicle_service.dto.DriverAvailabilityRequest;
import com.ridelink.driver_vehicle_service.dto.DriverProfileResponse;
import com.ridelink.driver_vehicle_service.dto.DriverRequest;
import com.ridelink.driver_vehicle_service.dto.DriverResponse;
import com.ridelink.driver_vehicle_service.exception.DriverNotFoundException;
import com.ridelink.driver_vehicle_service.model.Driver;
import com.ridelink.driver_vehicle_service.repository.DriverRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }


    // =========================================================
    // CREATE DRIVER
    // =========================================================

    public DriverResponse createDriver(DriverRequest request) {

        Driver driver = new Driver();

        driver.setName(request.getName());
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
                savedDriver.getName(),
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

        driver.setName(request.getName());
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
                updatedDriver.getName(),
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
                updatedDriver.getName(),
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
                driver.getName(),
                driver.getLicenseNumber(),
                vehicleRegistrationNumber
        );
    }


    // =========================================================
    // GET FULL DRIVER PROFILE
    // =========================================================

    public DriverProfileResponse getDriverProfile(String id) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(
                        () -> new DriverNotFoundException(id)
                );

        return new DriverProfileResponse(
                driver.getId(),
                driver.getName(),
                driver.getLicenseNumber(),
                driver.isAvailable(),
                driver.getServiceArea(),
                driver.getCurrentLocation(),
                driver.getVehicle()
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
                            driver.getName(),
                            driver.getLicenseNumber(),
                            vehicleRegistrationNumber
                    );
                })
                .toList();
    }
}