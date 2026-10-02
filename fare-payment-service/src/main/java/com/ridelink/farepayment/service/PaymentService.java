package com.ridelink.farepayment.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    // Process and save payment
    public Payment processPayment(Payment payment) {

        // 1. Calculate dynamic fareAmount for realistic simulations (random Double between 400.0 and 2500.0 rounded to 2 decimal places)
        double randomFare = 400.0 + (Math.random() * (2500.0 - 400.0));
        double roundedFare = Math.round(randomFare * 100.0) / 100.0;
        payment.setFareAmount(roundedFare);

        // 2. Simulated payment recording based on payment method (CASH or CARD)
        if ("CARD".equalsIgnoreCase(payment.getPaymentMethod())) {
            String txnId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            payment.setTransactionId(txnId);
            payment.setPaymentStatus("PAID");
        } else if ("CASH".equalsIgnoreCase(payment.getPaymentMethod())) {
            payment.setTransactionId(null);
            payment.setPaymentStatus("PAID");
        } else {
            payment.setPaymentStatus("PAID");
        }

        if (payment.getOrderId() == null || payment.getOrderId().isBlank()) {
            payment.setOrderId("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        }

        // 3. Save payment record and return saved Payment
        return paymentRepository.save(payment);
    }

    // Get payment by ID
    public Payment getPaymentById(String id) {

        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));
    }

    // Get payment status
    public String getPaymentStatus(String id) {

        Payment payment = getPaymentById(id);

        return payment.getPaymentStatus();
    }
}