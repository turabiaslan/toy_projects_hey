package com.beam.sample.hey.service;


import com.beam.sample.hey.dto.AuthenticationResult;
import com.beam.sample.hey.dto.ConversationsDto;
import com.beam.sample.hey.model.*;
import com.beam.sample.hey.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final DiskService diskService;
    private final ChatRepository chatRepository;

    private final ContactsService contactsService;


    private final SimpMessagingTemplate simpMessagingTemplate;


    public Chat sendMessage(String id, String content, Account sender) {
        Chat chat = chatRepository.findById(id).get();
        if (!chat.getParticipants().contains(sender)) {
            return null;
        }
        Message message = new Message().setContent(content).setSendDate(new Date()).setSender(sender.getUsername()).setParticipantCnt(chat.getParticipants().size() - 1).setStatus(Status.SENT);
        List<Message> messageList = chat.getMessages();
        messageList.add(message);
        chat.setMessages(messageList);
        chatRepository.save(chat);
        for (Account a : chat.getParticipants()) {
            simpMessagingTemplate.convertAndSend("/hey/chat/update" + a.getUsername() + chat.getId(), message);
            if (!a.getUsername().equals(sender.getUsername())) {
                if (chat instanceof GroupChat) {
                    simpMessagingTemplate.convertAndSend("/hey/chat/conversation" + a.getUsername(), new ConversationsDto().setChatId(chat.getId()).setAvatar(((GroupChat) chat).getAvatar()).setChatName(chat.getName()).setUnreadMessages(unreadMessageCount(chat, a)));
                } else {
                    simpMessagingTemplate.convertAndSend("/hey/chat/conversation" + a.getUsername(), new ConversationsDto().setChatId(chat.getId()).setAvatar(sender.getProfile().getAvatar()).setChatName(sender.getUsername()).setUnreadMessages(unreadMessageCount(chat,a)));
                }
            }
        }

        return chat;
    }

    public GroupChat addParticipant(GroupChat group, Account account) {
        List<Account> accounts = group.getParticipants();
        if (!accounts.contains(account)) {
            accounts.add(account);
            group.setParticipants(accounts);
            Contacts contacts = contactsService.findByOwner(account);
            contacts.getGroups().add(group);
            contactsService.save(contacts);
            chatRepository.save(group);

        }
        return group;
    }

    public void save(GroupChat groupChat) {
        chatRepository.save(groupChat);
    }


    public void saveGroupAvatar(GroupChat group, MultipartFile file) {
        String filename = "";
        try {
            filename = diskService.saveAvatar(file.getBytes());
        } catch (IOException exc) {
            log.error(exc.toString());
        }
        group.setAvatar(filename);
    }

    public byte[] readAvatar(String path) {
        try {
            return diskService.readAvatar(path);
        } catch (IOException exc) {
            log.error(exc.toString());
            return null;
        }
    }

    public void leaveGroup(String id, Account participant) {
        GroupChat group = (GroupChat) chatRepository.findById(id).get();
        List<Account> participants = group.getParticipants();
        participants.remove(participant);
        chatRepository.save(group);

    }

    public List<Chat> sortAllConversations(Account account) {
        List<Chat> chats = chatRepository.findByParticipantsIn(account);
        Collections.sort(chats, new Comparator<Chat>() {
            @Override
            public int compare(Chat o2, Chat o1) {
                return o1.getMessages().get(o1.getMessages().size() - 1).getSendDate()
                        .compareTo(o2.getMessages().get(o2.getMessages().size() - 1).getSendDate());
            }
        });
        return chats;
    }

    public boolean containsName(final List<ReadDeliver> list, final String name) {
        return list.stream().anyMatch(o -> o.getUsername().equals(name));
    }

    public int unreadMessageCount(Chat chat, Account account) {
        int cnt = 0;
        String username = account.getUsername();
        for (int i = chat.getMessages().size() - 1; i > -1; i--) {
            if (chat.getMessages().get(i).getSender() != username && !containsName(chat.getMessages().get(i).getRead(), username)) {
                cnt++;
            } else {
                break;
            }
        }
        return cnt;
    }

    public void markDeliveredForAChat(Chat chat, Account account) {
        List<Message> allMessages = chat.getMessages();

        for (int i = allMessages.size() - 1; i > -1; i--) {
            if (allMessages.get(i).getStatus() == Status.SENT) {
                if (allMessages.get(i).getSendDate().after(account.getLastSeen())) {
                    if (!allMessages.get(i).getSender().equals(account.getUsername()) && !containsName(allMessages.get(i).getDeliver(), account.getUsername())) {
                        allMessages.get(i).getDeliver().add(
                                new ReadDeliver().setUsername(account.getUsername()).setReadDate(new Date()).setStatus(Status.DELIVERED));
                        allMessages.get(i).setDeliverCnt(allMessages.get(i).getDeliverCnt() + 1);
                        if (allMessages.get(i).getDeliverCnt() == chat.getParticipants().size() - 1) {
                            allMessages.get(i).setStatus(Status.DELIVERED);
                        }
                    }
                }
            } else {
                break;
            }
        }
        chatRepository.save(chat);
    }

    public List<ConversationsDto> showAllConversations(Account account) {
        List<ConversationsDto> all = new ArrayList<>();
        List<Chat> chats = sortAllConversations(account);
        for (Chat chat : chats) {
            if (chat instanceof GroupChat) {
                all.add(new ConversationsDto().setChatName(chat.getName()).setChatId(chat.getId()).setLastMessageDate(chat.getMessages().get(chat.getMessages().size() - 1).getSendDate()).setUnreadMessages(unreadMessageCount(chat, account)).setAvatar(((GroupChat) chat).getAvatar()).setG(true));
            } else {
                if (chat.getName() == null) {
                    for (Account account1 : chat.getParticipants()) {
                        if (!account1.getUsername().equals(account.getUsername())) {
                            all.add(new ConversationsDto().setChatId(chat.getId()).setChatName(account1.getUsername()).setLastMessageDate(chat.getMessages().get(chat.getMessages().size() - 1).getSendDate()).setUnreadMessages(unreadMessageCount(chat, account)).setAvatar(account1.getProfile().getAvatar()));
                        }
                    }
                }
            }
        }
        return all;
    }

    public void markDelivered(Account account) {
        List<Chat> chats = sortAllConversations(account);
        for (int i = 0; i < chats.size(); i++) {
            if (chats.get(i).getMessages().get(chats.get(i).getMessages().size() - 1).getSendDate().compareTo(account.getLastSeen()) > 0) {
                markDeliveredForAChat(chats.get(i), account);
            } else {
                break;
            }
        }
    }

    public boolean isReadBy(Message message, Account account) {
        List<ReadDeliver> readList = message.getRead();
        if (readList.size() == 0) {
            return false;
        } else {
            for (int i = 0; i < readList.size(); i++) {
                if (readList.get(i).getUsername().equals(account.getUsername())) {
                    return true;

                }
            }
            return false;
        }
    }

    public Chat readChat(String id, Account account) {
        Chat chat = chatRepository.findById(id).get();
        List<Message> messages = chat.getMessages();
        for (int i = messages.size() - 1; i > -1; i--) {
            if (messages.get(i).getStatus() != Status.SEEN) {
                if (!isReadBy(messages.get(i), account)) {
                    if (!messages.get(i).getSender().equals(account.getUsername())) {
                        List<ReadDeliver> read = messages.get(i).getRead();
                        read.add(new ReadDeliver().setReadDate(new Date()).setStatus(Status.SEEN).setUsername(account.getUsername()));
                        messages.get(i).setRead(read);
                        messages.get(i).setReadCnt(messages.get(i).getReadCnt() + 1);
                        if (messages.get(i).getReadCnt() == chat.getParticipants().size() - 1) {
                            messages.get(i).setStatus(Status.SEEN);
                        }
                        chatRepository.save(chat);
                    }
                }
            } else {
                break;

            }
        }
        /**
         for (Account a : chat.getParticipants()) {
         if(!(chat instanceof GroupChat)) {
         if (a != account) {
         if(a.getState().equals(State.ONLINE)){
         simpMessagingTemplate.convertAndSend("/hey/chat/state/" + a.getUsername() + chat.getId(), a.getState());
         }
         else{
         simpMessagingTemplate.convertAndSend("/hey/chat/state/" + a.getUsername() + chat.getId(), a.getLastSeen());

         }
         }
         }
         }*/

        return chat;
    }

    public int tickQuery(Message message) {
        int x = 0;
        if (message.getReadCnt() == message.getParticipantCnt()) {
            x = 2;
        } else if (message.getDeliverCnt() == message.getParticipantCnt()) {
            x = 1;
        }
        simpMessagingTemplate.convertAndSend("/hey/chat/tick/" + x);
        return x;
    }

    public void typing(String id, Account account) {
        Typing typing = new Typing().setTyping("Typing...");
        List<Account> accounts = chatRepository.findById(id).get().getParticipants();
        for (Account a : accounts) {
            if (!a.getUsername().equals(account.getUsername())) {
                simpMessagingTemplate.convertAndSend("/hey/chat/typing/" + id + a.getUsername(), typing);
            }
        }
    }


    public void saveContacts(Contacts contacts) {
        contactsService.save(contacts);
    }

    public List<Chat> findAllChats(Account account) {
        return chatRepository.findByParticipantsIn(account);
    }

    public void seeMessages(Account account, String id) {
        Chat chat = chatRepository.findById(id).get();
        readChat(id, account);
        for (Account a : chat.getParticipants()) {
            if (!a.getUsername().equals(account.getUsername())) {
                simpMessagingTemplate.convertAndSend("/hey/chat/messages/" + id + a.getUsername(), chat.getMessages());
            }
        }
    }

    public void saveChat(Chat chat) {
        chatRepository.save(chat);
    }

    public GroupChat showParticipants(String id, Account account) {
        GroupChat groupChat = (GroupChat) chatRepository.findById(id).get();
        if (!groupChat.getParticipants().contains(account)) {
            return null;
        } else {
            return groupChat;
        }
    }

    public GroupChat updateGroup(String id, String name, String description, MultipartFile file, Account account) {
        GroupChat groupChat = (GroupChat) chatRepository.findById(id).get();
        if (groupChat.getParticipants().contains(account)) {
            groupChat.setName(name);
            groupChat.setDescription(description);
            if (file != null) {
                saveGroupAvatar(groupChat, file);
            }
            chatRepository.save(groupChat);
            for(Account a : groupChat.getParticipants()) {
                simpMessagingTemplate.convertAndSend("/hey/chat/conversation" + a.getUsername(), new ConversationsDto().setChatId(groupChat.getId()).setAvatar((groupChat).getAvatar()).setChatName(groupChat.getName()).setUnreadMessages(unreadMessageCount(groupChat, a)));
            }
            return groupChat;
        } else {
            return null;
        }
    }


    public void removeFromGroup(Account account, String removalPart, String id) {
        GroupChat groupChat = (GroupChat) chatRepository.findById(id).get();
        if (groupChat.getParticipants().contains(account)) {
            for (Account a : groupChat.getParticipants()) {
                if (a.getUsername().equals(removalPart)) {
                    groupChat.getParticipants().remove(a);
                    chatRepository.save(groupChat);
                }
            }
        }
    }

    public Chat findById(String id) {
        return chatRepository.findById(id).get();
    }

    public AuthenticationResult deneme(Account account, String name) {
        if(account.getUsername().equals(name)){
            return new AuthenticationResult().setSuccess(true);
        }
        else{
            return new AuthenticationResult();
        }
    }
}
