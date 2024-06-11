package com.beam.sample.hey.repository;

import com.beam.sample.hey.model.Account;
import com.beam.sample.hey.model.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, String>{



   List<Chat> findByParticipantsIn(Account account);



}
