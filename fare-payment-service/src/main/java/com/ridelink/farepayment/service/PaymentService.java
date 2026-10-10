package com.ridelink.farepayment.service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ridelink.farepayment.dto.DriverPaymentStatsDto;

import com.ridelink.farepayment.model.Fare;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.repository.FareRepository;
import com.ridelink.farepayment.repository.PaymentRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final FareRepository fareRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, FareRepository fareRepository) {
        this.paymentRepository = paymentRepository;
        this.fareRepository = fareRepository;
    }

    public PaymentService(PaymentRepository paymentRepository) {
        this(paymentRepository, null);
    }

    // Process and save payment
    public Payment processPayment(Payment payment) {

        // Auto-fetch ride details from ride-management-service if rideId is provided
        java.util.Map<String, Object> rideData = null;
        if (payment.getRideId() != null && !payment.getRideId().isBlank()) {
            try {
                org.springframework.web.client.RestClient client = org.springframework.web.client.RestClient.builder()
                        .baseUrl("http://localhost:8083")
                        .build();
                rideData = client.get()
                        .uri("/api/rides/{id}", payment.getRideId())
                        .retrieve()
                        .body(java.util.Map.class);
            } catch (Exception e) {
                System.err.println("--- PaymentService: Failed to fetch ride details for Ride ID " + payment.getRideId() + ": " + e.getMessage() + " ---");
            }
        }

        // Auto-populate ride details from the ride record
        if (rideData != null) {
            // Auto-populate passengerId / userId
            if ((payment.getPassengerId() == null || payment.getPassengerId().isBlank()) && rideData.get("passengerId") != null) {
                String pId = rideData.get("passengerId").toString();
                payment.setPassengerId(pId);
                payment.setUserId(pId);
            }
            // Auto-populate driverId
            if ((payment.getDriverId() == null || payment.getDriverId().isBlank()) && rideData.get("driverId") != null) {
                payment.setDriverId(rideData.get("driverId").toString());
            }
            // Auto-populate paymentMethod
            if ((payment.getPaymentMethod() == null || payment.getPaymentMethod().isBlank()) && rideData.get("paymentMethod") != null) {
                payment.setPaymentMethod(rideData.get("paymentMethod").toString());
            }
        }

        // Default payment method to CARD if still not set
        if (payment.getPaymentMethod() == null || payment.getPaymentMethod().isBlank()) {
            payment.setPaymentMethod("CARD");
        }

        // 1. Determine accurate fareAmount / payable amount
        double finalFare = 0.0;

        // Prioritize the ride's completed finalFare / estimatedFare
        if (rideData != null) {
            Object ff = rideData.get("finalFare");
            Object ef = rideData.get("estimatedFare");
            if (ff instanceof Number num && num.doubleValue() > 0) {
                finalFare = num.doubleValue();
            } else if (ef instanceof Number num && num.doubleValue() > 0) {
                finalFare = num.doubleValue();
            } else if (ff != null) {
                try { finalFare = Double.parseDouble(ff.toString()); } catch (Exception ignored) {}
            }
        }

        // If not found from ride, check if an explicit positive fareAmount was passed
        if (finalFare <= 0) {
            if (payment.getFareAmount() > 0) {
                finalFare = payment.getFareAmount();
            } else if (payment.getAmount() > 0) {
                finalFare = payment.getAmount();
            }
        }

        // If not provided, fetch the actual calculated finalFare for that specific rideId
        if (finalFare <= 0 && fareRepository != null && payment.getRideId() != null && !payment.getRideId().isBlank()) {
            Fare fare = fareRepository.findFirstByRideIdOrderByIdDesc(payment.getRideId())
                    .orElseGet(() -> fareRepository.findByRideId(payment.getRideId()).orElse(null));
            if (fare != null) {
                finalFare = fare.getFinalFare() > 0 ? fare.getFinalFare() : fare.getEstimatedFare();
            }
        }

        // If still not found, check latest estimate for passenger/user
        if (finalFare <= 0 && fareRepository != null && payment.getPassengerId() != null && !payment.getPassengerId().isBlank()) {
            Fare fare = fareRepository.findFirstByUserIdOrderByIdDesc(payment.getPassengerId()).orElse(null);
            if (fare != null) {
                finalFare = fare.getFinalFare() > 0 ? fare.getFinalFare() : fare.getEstimatedFare();
            }
        }

        // Fallback to standard calculated fare (100.0 base + 10km * 50.0 + 20min * 5.0 = 700.0)
        if (finalFare <= 0) {
            finalFare = 700.0;
        }

        // Round to 2 decimal places to ensure clean currency representation
        double roundedFare = Math.round(finalFare * 100.0) / 100.0;
        payment.setFareAmount(roundedFare);
        payment.setAmount(roundedFare);

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

    // Fetch a list of payment records for a given passengerId
    public List<Payment> getPaymentsByPassengerId(String passengerId) {
        List<Payment> list = paymentRepository.findByPassengerId(passengerId);
        if (list == null || list.isEmpty()) {
            List<Payment> byUser = paymentRepository.findByUserId(passengerId);
            if (byUser != null && !byUser.isEmpty()) {
                return byUser;
            }
        }
        return list != null ? list : Collections.emptyList();
    }

    public List<Payment> getPassengerPaymentHistory(String passengerId) {
        return getPaymentsByPassengerId(passengerId);
    }

    // Calculate the total earnings and the split between cash and card payments for a given driverId
    public DriverPaymentStatsDto getDriverStats(String driverId) {
        List<Payment> payments = paymentRepository.findByDriverId(driverId);
        double cashTotal = 0.0;
        double cardTotal = 0.0;
        double totalEarnings = 0.0;

        if (payments != null) {
            for (Payment p : payments) {
                String status = p.getPaymentStatus();
                if (status != null && ("FAILED".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status))) {
                    continue;
                }

                double amount = p.getFareAmount() > 0 ? p.getFareAmount() : p.getAmount();
                if ("CASH".equalsIgnoreCase(p.getPaymentMethod())) {
                    cashTotal += amount;
                } else if ("CARD".equalsIgnoreCase(p.getPaymentMethod())) {
                    cardTotal += amount;
                }
                totalEarnings += amount;
            }
        }

        double roundedTotal = Math.round(totalEarnings * 100.0) / 100.0;
        double roundedCash = Math.round(cashTotal * 100.0) / 100.0;
        double roundedCard = Math.round(cardTotal * 100.0) / 100.0;

        return new DriverPaymentStatsDto(driverId, roundedTotal, roundedCash, roundedCard);
    }

    public DriverPaymentStatsDto calculateDriverEarnings(String driverId) {
        return getDriverStats(driverId);
    }
}