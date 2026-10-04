package com.ridelink.farepayment.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.ridelink.farepayment.model.Payment;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {

    List<Payment> findByPassengerId(String passengerId);

    List<Payment> findByDriverId(String driverId);

    List<Payment> findByUserId(String userId);
}