package com.ridelink.driver_vehicle_service.controller;

import com.ridelink.driver_vehicle_service.dto.DriverAvailabilityRequest;
import com.ridelink.driver_vehicle_service.dto.DriverLocationRequest;
import com.ridelink.driver_vehicle_service.dto.DriverLoginRequest;
import com.ridelink.driver_vehicle_service.dto.DriverLoginResponse;
import com.ridelink.driver_vehicle_service.dto.DriverProfileResponse;
import com.ridelink.driver_vehicle_service.dto.DriverRequest;
import com.ridelink.driver_vehicle_service.dto.DriverResponse;
import com.ridelink.driver_vehicle_service.service.DriverService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@Tag(
        name = "Driver & Vehicle Management",
        description = "APIs for managing drivers, vehicles, availability, service areas and simulated current locations"
)
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }


    // =========================================================
    // DRIVER LOGIN
    // =========================================================

    @Operation(
            summary = "Driver login",
            description = "Authenticates a driver using license number or email and password, returning a JWT token"
    )
    @PostMapping("/login")
    public ResponseEntity<DriverLoginResponse> login(
            @Valid @RequestBody DriverLoginRequest request) {

        DriverLoginResponse response = driverService.login(request);
        return ResponseEntity.ok(response);
    }


    // =========================================================
    // CREATE DRIVER
    // =========================================================

    @Operation(
            summary = "Create a driver",
            description = "Creates a new driver together with vehicle, service area, availability and location information"
    )
    @PostMapping
    public ResponseEntity<DriverResponse> createDriver(
            @Valid @RequestBody DriverRequest request) {

        DriverResponse createdDriver =
                driverService.createDriver(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdDriver);
    }


    // =========================================================
    // UPDATE DRIVER
    // =========================================================

    @Operation(
            summary = "Update a driver",
            description = "Updates an existing driver's operational profile and vehicle information"
    )
    @PutMapping("/{id}")
    public ResponseEntity<DriverResponse> updateDriver(
            @PathVariable String id,
            @Valid @RequestBody DriverRequest request) {

        DriverResponse updatedDriver =
                driverService.updateDriver(id, request);

        return ResponseEntity.ok(updatedDriver);
    }


    // =========================================================
    // UPDATE DRIVER AVAILABILITY
    // =========================================================

    @Operation(
            summary = "Update driver availability",
            description = "Updates the availability status of an existing driver"
    )
    @PatchMapping("/{id}/availability")
    public ResponseEntity<DriverProfileResponse> updateDriverAvailability(
            @PathVariable String id,
            @RequestBody DriverAvailabilityRequest request) {

        DriverProfileResponse updatedDriver =
                driverService.updateDriverAvailability(
                        id,
                        request
                );

        return ResponseEntity.ok(updatedDriver);
    }


    // =========================================================
    // UPDATE DRIVER LOCATION
    // =========================================================

    @Operation(
            summary = "Update driver location",
            description = "Updates the simulated current location of an existing driver"
    )
    @PatchMapping("/{id}/location")
    public ResponseEntity<DriverProfileResponse> updateDriverLocation(
            @PathVariable String id,
            @Valid @RequestBody DriverLocationRequest request) {

        DriverProfileResponse updatedDriver =
                driverService.updateDriverLocation(
                        id,
                        request
                );

        return ResponseEntity.ok(updatedDriver);
    }


    // =========================================================
    // DELETE DRIVER
    // =========================================================

    @Operation(
            summary = "Delete a driver",
            description = "Deletes an existing driver using the driver ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(
            @PathVariable String id) {

        driverService.deleteDriver(id);

        return ResponseEntity
                .noContent()
                .build();
    }


    // =========================================================
    // GET AVAILABLE DRIVERS
    // =========================================================

    @Operation(
            summary = "Get available drivers",
            description = "Returns available drivers and optionally filters them by service area"
    )
    @GetMapping("/available")
    public ResponseEntity<List<DriverResponse>> getAvailableDrivers(
            @RequestParam(required = false) String serviceArea) {

        List<DriverResponse> drivers =
                driverService.getAvailableDrivers(serviceArea);

        return ResponseEntity.ok(drivers);
    }


    // =========================================================
    // GET FULL DRIVER PROFILE
    // =========================================================

    @Operation(
            summary = "Get full driver profile",
            description = "Returns the driver's operational profile including availability, service area, current location and vehicle details"
    )
    @GetMapping("/{id}/profile")
    public ResponseEntity<DriverProfileResponse> getDriverProfile(
            @PathVariable String id) {

        DriverProfileResponse profile =
                driverService.getDriverProfile(id);

        return ResponseEntity.ok(profile);
    }


    // =========================================================
    // GET DRIVER BY ID
    // =========================================================

    @Operation(
            summary = "Get driver by ID",
            description = "Returns driver identification and vehicle registration information using the driver ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getDriverById(
            @PathVariable String id) {

        DriverResponse driver =
                driverService.getDriverById(id);

        return ResponseEntity.ok(driver);
    }
}