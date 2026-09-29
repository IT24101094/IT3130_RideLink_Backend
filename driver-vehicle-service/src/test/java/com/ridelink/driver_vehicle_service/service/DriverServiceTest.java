package com.ridelink.driver_vehicle_service.service;

import com.ridelink.driver_vehicle_service.dto.DriverProfileResponse;
import com.ridelink.driver_vehicle_service.dto.DriverRequest;
import com.ridelink.driver_vehicle_service.dto.DriverResponse;
import com.ridelink.driver_vehicle_service.exception.DriverNotFoundException;
import com.ridelink.driver_vehicle_service.model.Driver;
import com.ridelink.driver_vehicle_service.model.Location;
import com.ridelink.driver_vehicle_service.model.Vehicle;
import com.ridelink.driver_vehicle_service.repository.DriverRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    private DriverService driverService;

    private Driver testDriver;


    // =========================================================
    // SETUP
    // =========================================================

    @BeforeEach
    void setUp() {

        driverService = new DriverService(driverRepository);

        Vehicle vehicle = new Vehicle();

        vehicle.setRegistrationNumber("CAB-1234");
        vehicle.setMake("Toyota");
        vehicle.setModel("Prius");
        vehicle.setType("Car");
        vehicle.setColor("White");

        testDriver = new Driver();

        testDriver.setId("DRV001");
        testDriver.setName("Nimal Perera");
        testDriver.setLicenseNumber("B1234567");
        testDriver.setAvailable(true);
        testDriver.setServiceArea("Colombo");
        testDriver.setVehicle(vehicle);
    }


    // =========================================================
    // TEST 1 - GET EXISTING DRIVER
    // =========================================================

    @Test
    void shouldReturnDriverWhenDriverExists() {

        when(driverRepository.findById("DRV001"))
                .thenReturn(Optional.of(testDriver));

        DriverResponse response =
                driverService.getDriverById("DRV001");

        assertNotNull(response);

        assertEquals(
                "DRV001",
                response.getId()
        );

        assertEquals(
                "Nimal Perera",
                response.getName()
        );

        assertEquals(
                "B1234567",
                response.getLicenseNumber()
        );

        assertEquals(
                "CAB-1234",
                response.getVehicleRegistrationNumber()
        );

        verify(driverRepository, times(1))
                .findById("DRV001");
    }


    // =========================================================
    // TEST 2 - GET NON-EXISTING DRIVER
    // =========================================================

    @Test
    void shouldThrowExceptionWhenDriverDoesNotExist() {

        when(driverRepository.findById("UNKNOWN"))
                .thenReturn(Optional.empty());

        DriverNotFoundException exception =
                assertThrows(
                        DriverNotFoundException.class,
                        () -> driverService.getDriverById("UNKNOWN")
                );

        assertEquals(
                "Driver not found with id: UNKNOWN",
                exception.getMessage()
        );

        verify(driverRepository, times(1))
                .findById("UNKNOWN");
    }


    // =========================================================
    // TEST 3 - CREATE DRIVER
    // =========================================================

    @Test
    void shouldCreateDriverSuccessfully() {

        DriverRequest request = new DriverRequest();

        request.setName("Kamal Silva");
        request.setLicenseNumber("B7654321");
        request.setAvailable(true);
        request.setServiceArea("Colombo");
        request.setVehicle(testDriver.getVehicle());

        when(driverRepository.save(any(Driver.class)))
                .thenAnswer(invocation -> {

                    Driver driver =
                            invocation.getArgument(0);

                    driver.setId("DRV002");

                    return driver;
                });

        DriverResponse response =
                driverService.createDriver(request);

        assertNotNull(response);

        assertEquals(
                "DRV002",
                response.getId()
        );

        assertEquals(
                "Kamal Silva",
                response.getName()
        );

        assertEquals(
                "B7654321",
                response.getLicenseNumber()
        );

        assertEquals(
                "CAB-1234",
                response.getVehicleRegistrationNumber()
        );

        verify(driverRepository, times(1))
                .save(any(Driver.class));
    }


    // =========================================================
    // TEST 4 - UPDATE DRIVER
    // =========================================================

    @Test
    void shouldUpdateDriverSuccessfully() {

        DriverRequest request = new DriverRequest();

        request.setName("Nimal Updated");
        request.setLicenseNumber("B9999999");
        request.setAvailable(false);
        request.setServiceArea("Kandy");
        request.setVehicle(testDriver.getVehicle());

        when(driverRepository.findById("DRV001"))
                .thenReturn(Optional.of(testDriver));

        when(driverRepository.save(any(Driver.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        DriverResponse response =
                driverService.updateDriver(
                        "DRV001",
                        request
                );

        assertNotNull(response);

        assertEquals(
                "DRV001",
                response.getId()
        );

        assertEquals(
                "Nimal Updated",
                response.getName()
        );

        assertEquals(
                "B9999999",
                response.getLicenseNumber()
        );

        assertEquals(
                "CAB-1234",
                response.getVehicleRegistrationNumber()
        );

        assertFalse(testDriver.isAvailable());

        assertEquals(
                "Kandy",
                testDriver.getServiceArea()
        );

        verify(driverRepository, times(1))
                .findById("DRV001");

        verify(driverRepository, times(1))
                .save(testDriver);
    }


    // =========================================================
    // TEST 5 - GET ALL AVAILABLE DRIVERS
    // =========================================================

    @Test
    void shouldReturnAllAvailableDrivers() {

        Vehicle secondVehicle = new Vehicle();

        secondVehicle.setRegistrationNumber("CAB-2222");
        secondVehicle.setMake("Honda");
        secondVehicle.setModel("Vezel");
        secondVehicle.setType("Car");
        secondVehicle.setColor("Black");

        Driver secondDriver = new Driver();

        secondDriver.setId("DRV002");
        secondDriver.setName("Amal Fernando");
        secondDriver.setLicenseNumber("B2222222");
        secondDriver.setAvailable(true);
        secondDriver.setServiceArea("Kandy");
        secondDriver.setVehicle(secondVehicle);

        when(driverRepository.findByAvailableTrue())
                .thenReturn(
                        List.of(
                                testDriver,
                                secondDriver
                        )
                );

        List<DriverResponse> responses =
                driverService.getAvailableDrivers(null);

        assertNotNull(responses);

        assertEquals(
                2,
                responses.size()
        );

        assertEquals(
                "DRV001",
                responses.get(0).getId()
        );

        assertEquals(
                "DRV002",
                responses.get(1).getId()
        );

        assertEquals(
                "CAB-1234",
                responses.get(0)
                        .getVehicleRegistrationNumber()
        );

        assertEquals(
                "CAB-2222",
                responses.get(1)
                        .getVehicleRegistrationNumber()
        );

        verify(driverRepository, times(1))
                .findByAvailableTrue();

        verify(
                driverRepository,
                never()
        ).findByAvailableTrueAndServiceAreaIgnoreCase(
                anyString()
        );
    }


    // =========================================================
    // TEST 6 - FILTER AVAILABLE DRIVERS BY SERVICE AREA
    // =========================================================

    @Test
    void shouldReturnAvailableDriversByServiceArea() {

        when(
                driverRepository
                        .findByAvailableTrueAndServiceAreaIgnoreCase(
                                "Colombo"
                        )
        ).thenReturn(
                List.of(testDriver)
        );

        List<DriverResponse> responses =
                driverService.getAvailableDrivers(
                        "Colombo"
                );

        assertNotNull(responses);

        assertEquals(
                1,
                responses.size()
        );

        assertEquals(
                "DRV001",
                responses.get(0).getId()
        );

        assertEquals(
                "Nimal Perera",
                responses.get(0).getName()
        );

        assertEquals(
                "B1234567",
                responses.get(0).getLicenseNumber()
        );

        assertEquals(
                "CAB-1234",
                responses.get(0)
                        .getVehicleRegistrationNumber()
        );

        verify(driverRepository, times(1))
                .findByAvailableTrueAndServiceAreaIgnoreCase(
                        "Colombo"
                );

        verify(
                driverRepository,
                never()
        ).findByAvailableTrue();
    }


    // =========================================================
    // TEST 7 - DELETE EXISTING DRIVER
    // =========================================================

    @Test
    void shouldDeleteExistingDriver() {

        when(driverRepository.existsById("DRV001"))
                .thenReturn(true);

        driverService.deleteDriver("DRV001");

        verify(driverRepository, times(1))
                .existsById("DRV001");

        verify(driverRepository, times(1))
                .deleteById("DRV001");
    }


    // =========================================================
    // TEST 8 - DELETE NON-EXISTING DRIVER
    // =========================================================

    @Test
    void shouldThrowExceptionWhenDeletingDriverDoesNotExist() {

        when(driverRepository.existsById("UNKNOWN"))
                .thenReturn(false);

        DriverNotFoundException exception =
                assertThrows(
                        DriverNotFoundException.class,
                        () -> driverService.deleteDriver(
                                "UNKNOWN"
                        )
                );

        assertEquals(
                "Driver not found with id: UNKNOWN",
                exception.getMessage()
        );

        verify(driverRepository, times(1))
                .existsById("UNKNOWN");

        verify(
                driverRepository,
                never()
        ).deleteById(anyString());
    }


    // =========================================================
    // TEST 9 - GET FULL DRIVER PROFILE
    // =========================================================

    @Test
    void shouldReturnFullDriverProfile() {

        Location location = new Location();

        location.setLatitude(6.9271);
        location.setLongitude(79.8612);

        testDriver.setCurrentLocation(location);

        when(driverRepository.findById("DRV001"))
                .thenReturn(Optional.of(testDriver));

        DriverProfileResponse response =
                driverService.getDriverProfile("DRV001");

        assertNotNull(response);

        assertEquals(
                "DRV001",
                response.getId()
        );

        assertEquals(
                "Nimal Perera",
                response.getName()
        );

        assertEquals(
                "B1234567",
                response.getLicenseNumber()
        );

        assertTrue(
                response.isAvailable()
        );

        assertEquals(
                "Colombo",
                response.getServiceArea()
        );

        // Check current location
        assertNotNull(
                response.getCurrentLocation()
        );

        assertEquals(
                6.9271,
                response.getCurrentLocation().getLatitude()
        );

        assertEquals(
                79.8612,
                response.getCurrentLocation().getLongitude()
        );

        // Check vehicle information
        assertNotNull(
                response.getVehicle()
        );

        assertEquals(
                "CAB-1234",
                response.getVehicle().getRegistrationNumber()
        );

        assertEquals(
                "Toyota",
                response.getVehicle().getMake()
        );

        assertEquals(
                "Prius",
                response.getVehicle().getModel()
        );

        assertEquals(
                "Car",
                response.getVehicle().getType()
        );

        assertEquals(
                "White",
                response.getVehicle().getColor()
        );

        verify(driverRepository, times(1))
                .findById("DRV001");
    }
}