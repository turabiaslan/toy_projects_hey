package com.beam.sample.hey.controller;


import com.beam.sample.hey.model.Account;
import com.beam.sample.hey.service.ContactsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

import static com.beam.sample.hey.controller.AccountController.SESSION_ACCOUNT;

@RequiredArgsConstructor
@RestController
@RequestMapping("contacts")
public class ContactsController {

    private final ContactsService contactsService;

    @PostMapping("add-to-contacts")
    public void addToContacts(@RequestBody Account account, HttpSession session) {
        Account owner = (Account) session.getAttribute(SESSION_ACCOUNT);
        contactsService.addToContacts(owner, account);
    }
}
