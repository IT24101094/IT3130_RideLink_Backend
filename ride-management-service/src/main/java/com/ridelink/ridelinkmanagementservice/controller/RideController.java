package com.ridelink.ridelinkmanagementservice.controller;

import com.ridelink.ridelinkmanagementservice.model.Ride;
import com.ridelink.ridelinkmanagementservice.model.RideStatus;
import com.ridelink.ridelinkmanagementservice.service.RideService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rides")
@RequiredArgsConstructor
public class RideController {

    private final RideService rideService;

    @Data
    public static class RideRequest {
        @NotBlank(message = "Field is mandatory")
        private String pickupLocation;

        @NotBlank(message = "Field is mandatory")
        private String destination;

        private String paymentMethod;
    }

    @PostMapping
    public ResponseEntity<Ride> createRide(
            @Valid @RequestBody RideRequest request,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-Passenger-Id", required = false) String xPassengerId,
            @RequestHeader(value = "passengerId", required = false) String passengerIdHeader,
            @RequestParam(value = "passengerId", required = false) String passengerIdParam) {
        String passengerId = resolvePassengerId(authHeader, xPassengerId, passengerIdHeader, passengerIdParam);

        Ride createdRide = rideService.createRideRequest(
                passengerId,
                request.getPickupLocation(),
                request.getDestination(),
                request.getPaymentMethod()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRide);
    }

    @PutMapping("/{id}/assign")
    public ResponseEntity<Ride> assignDriver(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-Driver-Id", required = false) String xDriverId,
            @RequestHeader(value = "driverId", required = false) String driverIdHeader,
            @RequestParam(value = "driverId", required = false) String driverIdParam) {
        String driverId = resolveDriverId(authHeader, xDriverId, driverIdHeader, driverIdParam);
        Ride updatedRide = rideService.assignDriver(id, driverId);
        return ResponseEntity.ok(updatedRide);
    }

    @PutMapping("/{id}/accept")
    public ResponseEntity<Ride> acceptRide(
            @PathVariable String id,
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @RequestHeader(value = "X-Driver-Id", required = false) String xDriverId,
            @RequestHeader(value = "driverId", required = false) String driverIdHeader,
            @RequestParam(value = "driverId", required = false) String driverIdParam) {
        String driverId = resolveDriverId(authHeader, xDriverId, driverIdHeader, driverIdParam);
        Ride updatedRide = rideService.acceptRide(id, driverId);
        return ResponseEntity.ok(updatedRide);
    }

    private String resolveDriverId(String authHeader, String xDriverId, String driverIdHeader, String driverIdParam) {
        String fromSecurity = extractFromSecurityContext();
        if (fromSecurity != null && !fromSecurity.isBlank()) {
            return fromSecurity;
        }
        if (xDriverId != null && !xDriverId.isBlank()) {
            return xDriverId;
        }
        if (driverIdHeader != null && !driverIdHeader.isBlank()) {
            return driverIdHeader;
        }
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            String extracted = extractClaimFromJwt(token, "driverId");
            if (extracted != null && !extracted.isBlank()) {
                return extracted;
            }
        }
        return driverIdParam;
    }

    private String resolvePassengerId(String authHeader, String xPassengerId, String passengerIdHeader, String passengerIdParam) {
        String fromSecurity = extractFromSecurityContext();
        if (fromSecurity != null && !fromSecurity.isBlank()) {
            return fromSecurity;
        }
        if (xPassengerId != null && !xPassengerId.isBlank()) {
            return xPassengerId;
        }
        if (passengerIdHeader != null && !passengerIdHeader.isBlank()) {
            return passengerIdHeader;
        }
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7).trim();
            String extracted = extractClaimFromJwt(token, "userId");
            if (extracted != null && !extracted.isBlank()) {
                return extracted;
            }
        }
        return passengerIdParam;
    }

    private String extractFromSecurityContext() {
        try {
            Class<?> holderClass = Class.forName("org.springframework.security.core.context.SecurityContextHolder");
            Object context = holderClass.getMethod("getContext").invoke(null);
            if (context != null) {
                Object auth = context.getClass().getMethod("getAuthentication").invoke(context);
                if (auth != null) {
                    Object name = auth.getClass().getMethod("getName").invoke(auth);
                    if (name != null && !name.toString().equals("anonymousUser") && !name.toString().isBlank()) {
                        return name.toString();
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private String extractClaimFromJwt(String token, String preferredClaim) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length >= 2) {
                byte[] decoded = java.util.Base64.getUrlDecoder().decode(parts[1]);
                com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(decoded);
                if (preferredClaim != null && node.has(preferredClaim)) {
                    return node.get(preferredClaim).asText();
                }
                if (node.has("driverId")) {
                    return node.get("driverId").asText();
                }
                if (node.has("userId")) {
                    return node.get("userId").asText();
                }
                if (node.has("id")) {
                    return node.get("id").asText();
                }
                if (node.has("sub")) {
                    return node.get("sub").asText();
                }
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<Ride> startRide(@PathVariable String id) {
        Ride updatedRide = rideService.startRide(id);
        return ResponseEntity.ok(updatedRide);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Ride> completeRide(@PathVariable String id) {
        Ride updatedRide = rideService.completeRide(id);
        return ResponseEntity.ok(updatedRide);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Ride> cancelRide(@PathVariable String id) {
        Ride updatedRide = rideService.cancelRide(id);
        return ResponseEntity.ok(updatedRide);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Ride> updateRideStatus(
            @PathVariable String id,
            @RequestParam RideStatus newStatus) {
        Ride updatedRide = rideService.updateRideStatus(id, newStatus);
        return ResponseEntity.ok(updatedRide);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Ride> getRideById(@PathVariable String id) {
        Ride ride = rideService.getRideById(id);
        return ResponseEntity.ok(ride);
    }
}
