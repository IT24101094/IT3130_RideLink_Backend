package com.ridelink.driver_vehicle_service.controller;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import com.ridelink.driver_vehicle_service.dto.DriverRequest;
import com.ridelink.driver_vehicle_service.dto.DriverResponse;
import com.ridelink.driver_vehicle_service.service.DriverService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/drivers")
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }


    @PostMapping
    public ResponseEntity<DriverResponse> createDriver(
            @RequestBody DriverRequest request) {

        DriverResponse createdDriver =
                driverService.createDriver(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdDriver);
    }

    // UPDATE DRIVER
@PutMapping("/{id}")
public ResponseEntity<DriverResponse> updateDriver(
        @PathVariable String id,
        @RequestBody DriverRequest request) {

    DriverResponse updatedDriver =
            driverService.updateDriver(id, request);

    return ResponseEntity.ok(updatedDriver);
}

// DELETE DRIVER
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteDriver(
        @PathVariable String id) {

    driverService.deleteDriver(id);

    return ResponseEntity.noContent().build();
}

    @GetMapping("/{id}")
    public ResponseEntity<DriverResponse> getDriverById(
            @PathVariable String id) {

        DriverResponse driver =
                driverService.getDriverById(id);

        return ResponseEntity.ok(driver);
    }
}