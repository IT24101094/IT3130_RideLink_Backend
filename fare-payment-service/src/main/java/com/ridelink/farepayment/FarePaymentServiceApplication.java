package com.ridelink.farepayment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FarePaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FarePaymentServiceApplication.class, args);
	}

}
