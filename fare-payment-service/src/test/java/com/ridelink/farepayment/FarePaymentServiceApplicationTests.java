package com.ridelink.farepayment;

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

import com.ridelink.farepayment.model.Payment;
import com.ridelink.farepayment.repository.PaymentRepository;
import com.ridelink.farepayment.service.PaymentService;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FarePaymentServiceApplicationTests {

    private PaymentRepository paymentRepository;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        paymentService = new PaymentService(paymentRepository);
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
        assertTrue(result.getFareAmount() >= 400.0 && result.getFareAmount() <= 2500.0);
        assertEquals(result.getFareAmount(), Math.round(result.getFareAmount() * 100.0) / 100.0);
        assertNull(result.getTransactionId());

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
}