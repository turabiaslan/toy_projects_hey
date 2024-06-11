package com.beam.sample.hey.repository;


import com.beam.sample.hey.model.Account;
import com.beam.sample.hey.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

@SpringBootTest
public class AccountRepositoryTest {


    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountService accountService;

    String[] users = {"muzo", "bahar", "asli", "turabi", "turedi", "yesim", "melis", "yagiz"};

    /**
     * @Test public void testRegister() {
     * for (int i= 0; i < users.length; i++) {
     * Account account = new Account().setUsername(users[i]).setPassword(users[i]);
     * accountService.register(account, null);
     * }
     * }
     */

    @Test
    public void testLastSeen() {
        Account turabi = accountRepository.findByUsername("turabi");
        turabi.setLastSeen(java.sql.Timestamp.valueOf(LocalDateTime.parse("2022-10-18T08:00:00")));
        accountRepository.save(turabi);

    }

    @Test void testfindByUsernameRegex() {
        System.out.println(accountRepository.findByUsernameStartsWith("t"));
    }

    @Test void testSearch() {
        Account turabi = accountRepository.findByUsername("turabi");
        System.out.println(accountService.search(turabi, "h"));
    }


}
