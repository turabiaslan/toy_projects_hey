package com.beam.sample.hey.repository;


import com.beam.sample.hey.model.Account;
import com.beam.sample.hey.model.Chat;
import com.beam.sample.hey.model.GroupChat;
import com.beam.sample.hey.model.ReadDeliver;
import com.beam.sample.hey.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
public class ChatRepositoryTest {


    @Autowired
    AccountRepository accountRepository;

    @Autowired
    ChatService chatService;

    @Autowired
    ChatRepository chatRepository;
    /**

    @Test
    public void testCreateNewChat() {
        Account account = accountRepository.findByUsername("turabi");
        Account account1 = accountRepository.findByUsername("bahar");
        List<Account> participants = new ArrayList<>();
        participants.add(account1);
        chatService.createNewChat(account1, account, "Pazarciga gidek mi");
    }


     * @Test public void testSendMessage() {
     * Account asli = accountRepository.findByUsername("asli");
     * chatService.sendMessage("8a4ee105-fab5-4321-ad29-c5c1e6531d93", "Hey", asli);
     * }
     * @Test public void testFindByParticipants() {
     * List<Account> accounts = new ArrayList<>();
     * Account account = accountRepository.findByUsername("turabi");
     * accounts.add(account);
     * System.out.println(chatRepository.findByParticipantsIn(account));
     * }

    @Test
    public void testCreateGroup() {
        Account turabi = accountRepository.findByUsername("turabi");
        Account asli = accountRepository.findByUsername("asli");
        Account muzo = accountRepository.findByUsername("muzo");
        Account bahar = accountRepository.findByUsername("bahar");
        List<Account> participants = new ArrayList<>();
        participants.add(asli);
        participants.add(muzo);
        participants.add(bahar);
        chatService.createGroup("Hey", "Sun Tzu says: ", turabi, participants, null);
    }
     */

    @Test
    public void testSendMessage() {
        Account asli = accountRepository.findByUsername("turabi");
        chatService.sendMessage("8a4ee105-fab5-4321-ad29-c5c1e6531d93", "Hey there", asli);
    }


    @Test
    public void testContainsName() {
        Chat chat = chatRepository.findById("8a4ee105-fab5-4321-ad29-c5c1e6531d93").get();
        List<ReadDeliver> deliver = chat.getMessages().get(1).getDeliver();
        System.out.println(chatService.containsName(deliver, "turabi"));
    }

    @Test
    public void testMarkDeliveredForAChat() {
        Chat chat = chatRepository.findById("8a4ee105-fab5-4321-ad29-c5c1e6531d93").get();
        Account turabi = accountRepository.findByUsername("asli");
        chatService.markDeliveredForAChat(chat, turabi);
    }

    @Test
    public void testX() {
        Chat chat = chatRepository.findById("ad496297-92b6-4459-bdad-ba61e5cbcd00").get();
        if (chat instanceof GroupChat) {
            ((GroupChat) chat).setAvatar(UUID.randomUUID().toString());
            chatRepository.save(chat);
        }
    }


}
