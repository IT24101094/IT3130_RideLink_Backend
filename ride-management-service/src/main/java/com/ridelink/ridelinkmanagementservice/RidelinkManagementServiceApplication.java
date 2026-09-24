package com.ridelink.ridelinkmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RidelinkManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RidelinkManagementServiceApplication.class, args);
	}

}
