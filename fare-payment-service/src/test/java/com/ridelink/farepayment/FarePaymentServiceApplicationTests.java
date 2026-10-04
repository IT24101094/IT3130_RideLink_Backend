package com.ridelink.farepayment;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ridelink.farepayment.dto.DriverPaymentStatsDto;
import com.ridelink.farepayment.model.Fare;
import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.repository.FareRepository;
import com.ridelink.farepayment.repository.PaymentRepository;
import com.ridelink.farepayment.service.PaymentService;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FarePaymentServiceApplicationTests {

    private PaymentRepository paymentRepository;
    private FareRepository fareRepository;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        fareRepository = mock(FareRepository.class);
        paymentService = new PaymentService(paymentRepository, fareRepository);
    }

    @Test
    void processPayment_shouldSetStatusToPaidWithTransactionIdForCard() {

        Payment payment = new Payment(
                "ORDER001",
                "RIDE001",
                "USER001",
                700.0,
                "CARD",
                "PENDING"
        );

        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment result = paymentService.processPayment(payment);

        assertEquals("PAID", result.getPaymentStatus());
        assertNotNull(result.getTransactionId());
        assertEquals(700.0, result.getFareAmount());
        assertEquals(700.0, result.getAmount());
        assertTrue(result.getFareAmount() >= 400.0 && result.getFareAmount() <= 2500.0);
        assertEquals(result.getFareAmount(), Math.round(result.getFareAmount() * 100.0) / 100.0);

        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void processPayment_shouldSetStatusToPaidWithNullTransactionIdForCash() {

        Payment payment = new Payment(
                "ORDER002",
                "RIDE002",
                "USER002",
                0.0,
                "CASH",
                "PENDING"
        );

        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment result = paymentService.processPayment(payment);

        assertEquals("PAID", result.getPaymentStatus());
        assertEquals(700.0, result.getFareAmount());
        assertEquals(700.0, result.getAmount());
        assertTrue(result.getFareAmount() >= 400.0 && result.getFareAmount() <= 2500.0);
        assertEquals(result.getFareAmount(), Math.round(result.getFareAmount() * 100.0) / 100.0);
        assertNull(result.getTransactionId());

        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void processPayment_shouldUseFareFromFareRepositoryWhenAvailable() {
        Fare calculatedFare = new Fare();
        calculatedFare.setRideId("RIDE003");
        calculatedFare.setFinalFare(450.0);

        when(fareRepository.findFirstByRideIdOrderByIdDesc("RIDE003"))
                .thenReturn(Optional.of(calculatedFare));

        Payment payment = new Payment(
                "ORDER003",
                "RIDE003",
                "USER003",
                0.0,
                "CARD",
                "PENDING"
        );

        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment result = paymentService.processPayment(payment);

        assertEquals(450.0, result.getFareAmount());
        assertEquals(450.0, result.getAmount());
        assertEquals("PAID", result.getPaymentStatus());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void processPayment_shouldUsePassedFareAmountFromRide() {
        Payment payment = new Payment(
                "ORDER004",
                "RIDE004",
                "USER004",
                320.50,
                "CASH",
                "PENDING"
        );

        when(paymentRepository.save(payment)).thenReturn(payment);

        Payment result = paymentService.processPayment(payment);

        assertEquals(320.50, result.getFareAmount());
        assertEquals(320.50, result.getAmount());
        assertEquals("PAID", result.getPaymentStatus());
        verify(paymentRepository, times(1)).save(payment);
    }

    @Test
    void getPaymentById_shouldReturnPaymentWhenPaymentExists() {

        Payment payment = new Payment(
                "ORDER001",
                "RIDE001",
                "USER001",
                700.0,
                "CARD",
                "SUCCESS"
        );

        when(paymentRepository.findById("PAY001"))
                .thenReturn(Optional.of(payment));

        Payment result = paymentService.getPaymentById("PAY001");

        assertNotNull(result);
        assertEquals("ORDER001", result.getOrderId());
        assertEquals("RIDE001", result.getRideId());
        assertEquals("USER001", result.getUserId());
        assertEquals(700.0, result.getAmount());

        verify(paymentRepository, times(1)).findById("PAY001");
    }

    @Test
    void getPaymentById_shouldThrowExceptionWhenPaymentDoesNotExist() {

        when(paymentRepository.findById("INVALID"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentService.getPaymentById("INVALID")
        );

        assertEquals(
                "Payment not found: INVALID",
                exception.getMessage()
        );

        verify(paymentRepository, times(1)).findById("INVALID");
    }

    @Test
    void getPaymentStatus_shouldReturnPaymentStatus() {

        Payment payment = new Payment(
                "ORDER001",
                "RIDE001",
                "USER001",
                700.0,
                "CARD",
                "SUCCESS"
        );

        when(paymentRepository.findById("PAY001"))
                .thenReturn(Optional.of(payment));

        String status = paymentService.getPaymentStatus("PAY001");

        assertEquals("SUCCESS", status);

        verify(paymentRepository, times(1)).findById("PAY001");
    }

    @Test
    void getPassengerPaymentHistory_shouldReturnPaymentsForPassenger() {
        Payment p1 = new Payment("ORD1", "R1", "P1", 500.0, "CASH", "PAID");
        Payment p2 = new Payment("ORD2", "R2", "P1", 800.0, "CARD", "PAID");

        when(paymentRepository.findByPassengerId("P1")).thenReturn(List.of(p1, p2));

        List<Payment> history = paymentService.getPassengerPaymentHistory("P1");

        assertEquals(2, history.size());
        assertEquals("ORD1", history.get(0).getOrderId());
        assertEquals("ORD2", history.get(1).getOrderId());
        verify(paymentRepository, times(1)).findByPassengerId("P1");
    }

    @Test
    void getDriverStats_shouldCalculateTotalEarningsAndSplitCorrectly() {
        Payment p1 = new Payment("ORD1", "R1", "P1", "D1", 500.0, "CASH", "PAID", null);
        Payment p2 = new Payment("ORD2", "R2", "P2", "D1", 800.0, "CARD", "PAID", "TXN123");
        Payment p3 = new Payment("ORD3", "R3", "P3", "D1", 200.0, "CASH", "PAID", null);

        when(paymentRepository.findByDriverId("D1")).thenReturn(List.of(p1, p2, p3));

        DriverPaymentStatsDto stats = paymentService.getDriverStats("D1");

        assertEquals("D1", stats.getDriverId());
        assertEquals(1500.0, stats.getTotalEarnings());
        assertEquals(700.0, stats.getCashTotal());
        assertEquals(800.0, stats.getCardTotal());
        verify(paymentRepository, times(1)).findByDriverId("D1");
    }

    @Test
    void getDriverStats_shouldExcludeFailedPayments() {
        Payment p1 = new Payment("ORD1", "R1", "P1", "D1", 500.0, "CASH", "PAID", null);
        Payment p2 = new Payment("ORD2", "R2", "P2", "D1", 800.0, "CARD", "FAILED", null);

        when(paymentRepository.findByDriverId("D1")).thenReturn(List.of(p1, p2));

        DriverPaymentStatsDto stats = paymentService.getDriverStats("D1");

        assertEquals(500.0, stats.getTotalEarnings());
        assertEquals(500.0, stats.getCashTotal());
        assertEquals(0.0, stats.getCardTotal());
    }
}