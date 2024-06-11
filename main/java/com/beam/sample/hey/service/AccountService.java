package com.beam.sample.hey.service;


import com.beam.sample.hey.dto.AuthenticationResult;
import com.beam.sample.hey.dto.LoginRequest;
import com.beam.sample.hey.dto.SearchDto;
import com.beam.sample.hey.dto.StateDto;
import com.beam.sample.hey.model.*;
import com.beam.sample.hey.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final DiskService diskService;

    private final AccountRepository accountRepository;

    private final ChatService chatService;

    private final PasswordEncoder passwordEncoder;

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void saveAccountAvatar(Account account, MultipartFile file) {
        String filename = "";
        try {
            filename = diskService.saveAvatar(file.getBytes());
        } catch (IOException exc) {
            log.error(exc.toString());
        }
        account.getProfile().setAvatar(filename);
    }

    public AuthenticationResult register(String username, String password, MultipartFile file) {
        Account account = new Account();
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return new AuthenticationResult().setMessage("Username or password cannot be null");
        }
        if (accountRepository.existsByUsername(username)) {
            return new AuthenticationResult().setMessage("This username is already taken. Please choose another one");
        } else {

            account.setPassword(passwordEncoder.encode(password)).setUsername(username).setLastSeen(new Date()).setProfile(account.getProfile());
            if (file != null) {
                saveAccountAvatar(account, file);

            }
            account.setId(UUID.randomUUID().toString());
            account = accountRepository.save(account);
            Contacts contacts = new Contacts().setOwner(account);
            contacts.setId(UUID.randomUUID().toString());
            chatService.saveContacts(contacts);
            return new AuthenticationResult()
                    .setSuccess(true)
                    .setProfile(account.getProfile())
                    .setAccount(account);
        }
    }

    public AuthenticationResult login(String username, String password) {
        Account account = accountRepository.findByUsername(username);
        if (account != null && passwordEncoder.matches(password, account.getPassword())) {
            chatService.markDelivered(account);
            account.setState(State.ONLINE);
            save(account);

            for(Account a : list( account)){
                simpMessagingTemplate.convertAndSend("/hey/chat/state/" + a.getUsername(),  account.getState());

            }
            return new AuthenticationResult().setSuccess(true).setAccount(account).setProfile(account.getProfile()).setUsername(account.getUsername());
        } else {
            return new AuthenticationResult();
        }
    }

    public Profile update(Account account, Profile profile, MultipartFile file) {
        account.setProfile(profile);
        if (file != null) {
            saveAccountAvatar(account, file);
        }
        accountRepository.save(account);
        return account.getProfile();
    }

    public byte[] readAvatar(String path) {
        try {
            return diskService.readAvatar(path);
        } catch (IOException exc) {
            log.error(exc.toString());
            return null;
        }
    }


    public void save(Account account) {
        accountRepository.save(account);
    }

    public List<SearchDto> search(Account account, String search) {
        List<SearchDto> results = new ArrayList<>();
        List<Account> sAccounts = accountRepository.findByUsernameStartsWith(search);
        List<Chat> chats = chatService.findAllChats(account);
        List<SearchDto> results1 = new ArrayList<>();
        for (Chat chat : chats) {
            if (chat instanceof GroupChat) {
                if (chat.getName().toLowerCase().startsWith(search)) {
                    results.add(new SearchDto().setName(chat.getName()).setChat_id(chat.getId()).setAvatar(((GroupChat) chat).getAvatar()).setLastMessage(chat.getMessages().get(chat.getMessages().size() - 1)));
                }
            } else {
                for (Account a : chat.getParticipants()) {
                    if (!a.getUsername().equals(account.getUsername()) && a.getUsername().startsWith(search)) {
                        results.add(new SearchDto().setAccount(a).setName(a.getUsername()).setLastMessage(chat.getMessages().get(chat.getMessages().size() - 1)).setAvatar(a.getProfile().getAvatar()).setState(a.getState()).setChat_id(chat.getId()));
                    }
                }
            }
        }
        for (Account sA : sAccounts) {
            if (!sA.getUsername().equals(account.getUsername()) && !containsName(results, sA.getUsername())) {
                results1.add(new SearchDto().setAccount(sA).setAbout(sA.getProfile().getAbout()).setAvatar(sA.getProfile().getAvatar()));
            }
        }
        results.addAll(results1);
        return results;
    }


    public boolean containsName(final List<SearchDto> list, final String name) {
        return list.stream().anyMatch(o -> o.getName().equals(name));
    }

    public List<Account> list(Account account){
        List<Account> accounts = accountRepository.findAll();
        accounts.remove(account);
        return accounts;
    }

    public StateDto stateQuery(String username) {
        if(accountRepository.findByUsername(username).getState() ==State.ONLINE){
            return new StateDto().setState(accountRepository.findByUsername(username).getState());
        }else {
            return new StateDto().setLastSeen(accountRepository.findByUsername(username).getLastSeen());
        }
    }

    public void logout(Account account) {
        account.setState(State.OFFLINE);
        account.setLastSeen(new Date());
        accountRepository.save(account);
        for(Account a: list(account)){
            simpMessagingTemplate.convertAndSend("/hey/chat/state/" + a.getUsername(), account.getLastSeen());
        }

    }

    public GroupChat createGroup(String name, String description, Account account, List<String> nameList, MultipartFile file) {
        GroupChat group = new GroupChat().setDescription(description);
        List<Account> participants = new ArrayList<>();
        for (String n : nameList) {
            participants.add(accountRepository.findByUsername(n));
        }
        group.setName(name);
        participants.add(account);
        group.setParticipants(participants);
        if (file != null) {
            chatService.saveGroupAvatar(group, file);
        }
        group.setId(UUID.randomUUID().toString());
        chatService.save(group);
        chatService.sendMessage(group.getId(), "Welcome to the " + group.getName() + "group", account);
        return group;
    }


    public Chat createNewChat(String name, Account account, String content) {
        Chat chat = new Chat();
        chat.setId(UUID.randomUUID().toString());
        List<Account> participants = new ArrayList<>();
        participants.add(account);
        participants.add(accountRepository.findByUsername(name));
        chat.setParticipants(participants);
        List<Message> messages = new ArrayList<>();
        Message message = new Message().setContent(content).setParticipantCnt(participants.size() - 1).setSendDate(new Date()).setSender(account.getUsername()).setStatus(Status.SENT);
        messages.add(message);
        chat.setMessages(messages);
        chatService.saveChat(chat);
        return chat;
    }


    public void addToGroup(Account account, String participantNew, String id) {
        GroupChat groupChat = (GroupChat) chatService.findById(id);
        Account p = accountRepository.findByUsername(participantNew);
        if (groupChat.getParticipants().contains(account) && !groupChat.getParticipants().contains(participantNew)) {
            groupChat.getParticipants().add(p);
            chatService.save(groupChat);
        }
    }
}
