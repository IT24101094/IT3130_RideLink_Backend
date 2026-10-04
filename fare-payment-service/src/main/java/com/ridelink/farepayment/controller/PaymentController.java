package com.ridelink.farepayment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ridelink.farepayment.dto.DriverPaymentStatsDto;
import com.ridelink.farepayment.dto.PaymentRequestDto;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.service.PaymentService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/payments")
@SecurityRequirement(name = "Bearer Authentication")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Process payment
    @PostMapping
    public ResponseEntity<Payment> processPayment(
            @Valid @RequestBody PaymentRequestDto request) {

        Payment payment = new Payment();

        payment.setOrderId(request.getOrderId());
        payment.setRideId(request.getRideId());
        payment.setPassengerId(request.getPassengerId());
        payment.setDriverId(request.getDriverId());
        payment.setFareAmount(request.getFareAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setPaymentStatus(request.getPaymentStatus());

        Payment processedPayment = paymentService.processPayment(payment);

        return ResponseEntity.ok(processedPayment);
    }

    // Get payment status
    @GetMapping("/{id}/status")
    public ResponseEntity<String> getPaymentStatus(
            @PathVariable String id) {

        String status = paymentService.getPaymentStatus(id);

        return ResponseEntity.ok(status);
    }

    // Get payment receipt
    @GetMapping("/{id}/receipt")
    public ResponseEntity<Payment> getPaymentReceipt(
            @PathVariable String id) {

        Payment payment = paymentService.getPaymentById(id);

        return ResponseEntity.ok(payment);
    }

    // Get passenger payment history
    @GetMapping("/passenger/{passengerId}/history")
    public ResponseEntity<List<Payment>> getPassengerPaymentHistory(
            @PathVariable String passengerId) {

        List<Payment> history = paymentService.getPassengerPaymentHistory(passengerId);

        return ResponseEntity.ok(history);
    }

    // Get driver payment stats
    @GetMapping("/driver/{driverId}/stats")
    public ResponseEntity<DriverPaymentStatsDto> getDriverStats(
            @PathVariable String driverId) {

        DriverPaymentStatsDto stats = paymentService.getDriverStats(driverId);

        return ResponseEntity.ok(stats);
    }
}