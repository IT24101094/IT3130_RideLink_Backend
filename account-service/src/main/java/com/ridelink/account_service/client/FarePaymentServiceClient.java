package com.ridelink.account_service.client;

import com.ridelink.account_service.dto.PaymentHistoryDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "fare-payment-service", url = "${fare-payment-service.url:http://localhost:8084}")
public interface FarePaymentServiceClient {

    @GetMapping("/api/payments/passenger/{passengerId}/history")
    List<PaymentHistoryDto> getPassengerPaymentHistory(@PathVariable("passengerId") String passengerId);
}
