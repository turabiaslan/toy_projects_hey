package com.beam.sample.hey.repository;

import com.beam.sample.hey.model.Account;
import com.beam.sample.hey.model.Contacts;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ContactsRepository extends MongoRepository<Contacts, String> {

    Contacts findByOwner(Account account);


}
