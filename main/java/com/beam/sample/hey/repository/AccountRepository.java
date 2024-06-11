package com.beam.sample.hey.repository;

import com.beam.sample.hey.model.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AccountRepository extends MongoRepository <Account, String> {
    Account findByUsername(String username);

    boolean existsByUsername(String username);

    List<Account> findByUsernameStartsWith(String regex);
}
