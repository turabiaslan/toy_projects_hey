package com.beam.sample.hey.controller;


import com.beam.sample.hey.dto.AuthenticationResult;
import com.beam.sample.hey.dto.ConversationsDto;
import com.beam.sample.hey.model.Account;
import com.beam.sample.hey.model.Chat;
import com.beam.sample.hey.model.GroupChat;
import com.beam.sample.hey.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;

import java.util.List;

import static com.beam.sample.hey.controller.AccountController.SESSION_ACCOUNT;

@RequiredArgsConstructor
@RestController
@RequestMapping("chat")
public class ChatController {

    private final ChatService chatService;

    @GetMapping("all-conversations")
    public List<ConversationsDto> showAllConversations(HttpSession session){
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        return chatService.showAllConversations(account);
    }

    @GetMapping("read-chat/{id}")
    public Chat readChat(@PathVariable String id, HttpSession session) {
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        return chatService.readChat(id, account);
    }

    @GetMapping("leave-group")
    public void leaveGroup(@RequestParam String id, HttpSession session){
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        chatService.leaveGroup(id, account);
    }



    @PostMapping("add-participant")
    public GroupChat addParticipant(@RequestBody GroupChat group,@RequestBody Account participant, HttpSession session) {
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        return chatService.addParticipant(group, participant);
    }


    @PostMapping("send-message")
    public Chat sendMessage(@RequestParam  String id, @RequestParam  String content, HttpSession session){
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        return chatService.sendMessage(id, content, account);
    }




    @GetMapping("group-avatar/{path}")
    public ResponseEntity<byte[]> groupAvatar(@PathVariable String path) {
        byte[] img = chatService.readAvatar(path);
        return ResponseEntity.ok(img);
    }

    @GetMapping("typing")
    public void typing (String id, HttpSession session) {
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        chatService.typing(id, account);
    }

    @GetMapping("see-messages")
    public void seeMessages(String id, HttpSession session){
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        chatService.seeMessages(account,id);
    }

    @GetMapping("group")
    public GroupChat group (@RequestParam String id, HttpSession session) {
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        return chatService.showParticipants(id, account);
    }

    @PostMapping("update-group")
    public GroupChat groupChat(@RequestParam String id, @RequestParam String name, @RequestParam String description,
                               MultipartFile file, HttpSession session) {
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        return chatService.updateGroup(id,name, description, file, account);
    }



    @PostMapping ("remove-from-group")
    public void removeFromGroup(@RequestParam String id, @RequestParam String pRemoval, HttpSession session) {
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        chatService.removeFromGroup(account,pRemoval,id);
    }

    @PostMapping("deneme")
    public AuthenticationResult deneme(@RequestBody Account account, @RequestParam String name) {
        return chatService.deneme(account, name);
    }






}
