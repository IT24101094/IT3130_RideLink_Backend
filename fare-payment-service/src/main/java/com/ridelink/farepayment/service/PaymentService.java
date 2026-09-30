package com.ridelink.farepayment.service;

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

        payment.setPaymentStatus("SUCCESS");

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