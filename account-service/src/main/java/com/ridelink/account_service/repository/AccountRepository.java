package com.ridelink.account_service.repository;

import com.ridelink.account_service.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends MongoRepository<Account, String> {
    // Custom queries can go here later, but standard ones are already built-in!
}
