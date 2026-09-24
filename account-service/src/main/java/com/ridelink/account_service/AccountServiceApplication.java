package com.ridelink.account_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class AccountServiceApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(AccountServiceApplication.class);
        
        Map<String, Object> props = new HashMap<>();
        props.put("server.port", "5002");
        props.put("spring.data.mongodb.uri", "mongodb+srv://dinali:dinali12@cluster0.ctucovu.mongodb.net/ridelink_account_db?retryWrites=true&w=majority&appName=Cluster0");
        
        app.setDefaultProperties(props);
        app.run(args);
    }
}