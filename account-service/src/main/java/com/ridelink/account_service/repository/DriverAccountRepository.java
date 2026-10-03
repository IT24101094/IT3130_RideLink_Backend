package com.ridelink.account_service.repository;

import com.ridelink.account_service.model.DriverAccount;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverAccountRepository extends MongoRepository<DriverAccount, String> {
    Optional<DriverAccount> findByEmail(String email);
    boolean existsByEmail(String email);
}
