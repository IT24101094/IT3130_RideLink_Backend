package com.ridelink.farepayment.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.ridelink.farepayment.model.Fare;

public interface FareRepository extends MongoRepository<Fare, String> {

    Optional<Fare> findByRideId(String rideId);

    Optional<Fare> findFirstByRideIdOrderByIdDesc(String rideId);

    Optional<Fare> findFirstByUserIdOrderByIdDesc(String userId);
}