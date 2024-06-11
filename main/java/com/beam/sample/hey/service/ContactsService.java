package com.beam.sample.hey.service;


import com.beam.sample.hey.model.Account;
import com.beam.sample.hey.model.Contacts;
import com.beam.sample.hey.repository.ContactsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class ContactsService {

    private final ContactsRepository contactsRepository;

    public Contacts findByOwner(Account account) {
        return contactsRepository.findByOwner(account);
    }

    public void save(Contacts contacts) {
        contactsRepository.save(contacts);
    }

    public void addToContacts(Account dominus, Account contact) {
        Contacts byOwner = contactsRepository.findByOwner(dominus);
        byOwner.getAccounts().add(contact);
        contactsRepository.save(byOwner);
    }
}
