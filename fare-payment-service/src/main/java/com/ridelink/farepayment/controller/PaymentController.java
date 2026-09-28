package com.ridelink.farepayment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Process payment
    @PostMapping
    public ResponseEntity<Payment> processPayment(
            @RequestBody Payment payment) {

        Payment processedPayment =
                paymentService.processPayment(payment);

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
    public ResponseEntity<Payment> getReceipt(
            @PathVariable String id) {

        Payment payment = paymentService.getPaymentById(id);

        return ResponseEntity.ok(payment);
    }
}