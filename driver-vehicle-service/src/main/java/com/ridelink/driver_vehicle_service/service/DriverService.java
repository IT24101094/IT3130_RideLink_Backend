package com.ridelink.driver_vehicle_service.service;

import com.ridelink.driver_vehicle_service.dto.DriverRequest;
import com.ridelink.driver_vehicle_service.dto.DriverResponse;
import com.ridelink.driver_vehicle_service.exception.DriverNotFoundException;
import com.ridelink.driver_vehicle_service.model.Driver;
import com.ridelink.driver_vehicle_service.repository.DriverRepository;
import org.springframework.stereotype.Service;

@Service
public class DriverService {

    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    // CREATE DRIVER
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
    // UPDATE DRIVER
public DriverResponse updateDriver(String id, DriverRequest request) {

    Driver driver = driverRepository.findById(id)
            .orElseThrow(() -> new DriverNotFoundException(id));

    driver.setName(request.getName());
    driver.setLicenseNumber(request.getLicenseNumber());
    driver.setAvailable(request.isAvailable());
    driver.setServiceArea(request.getServiceArea());
    driver.setCurrentLocation(request.getCurrentLocation());
    driver.setVehicle(request.getVehicle());

    Driver updatedDriver = driverRepository.save(driver);

    String vehicleRegistrationNumber = null;

    if (updatedDriver.getVehicle() != null) {
        vehicleRegistrationNumber =
                updatedDriver.getVehicle().getRegistrationNumber();
    }

    return new DriverResponse(
            updatedDriver.getId(),
            updatedDriver.getName(),
            updatedDriver.getLicenseNumber(),
            vehicleRegistrationNumber
    );
}


// DELETE DRIVER
public void deleteDriver(String id) {

    if (!driverRepository.existsById(id)) {
        throw new DriverNotFoundException(id);
    }

    driverRepository.deleteById(id);
}


    // GET DRIVER BY ID
    public DriverResponse getDriverById(String id) {

        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new DriverNotFoundException(id));

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
}