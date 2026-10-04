package com.ridelink.account_service.repository;

import com.ridelink.account_service.model.Passenger;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassengerRepository extends MongoRepository<Passenger, String> {
    Optional<Passenger> findByEmail(String email);
    boolean existsByEmail(String email);
}
