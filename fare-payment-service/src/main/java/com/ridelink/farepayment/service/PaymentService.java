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

    public Payment processPayment(Payment payment) {

        payment.setPaymentStatus("SUCCESS");

        return paymentRepository.save(payment);
    }
}