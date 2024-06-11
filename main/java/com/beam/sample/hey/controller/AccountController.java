package com.beam.sample.hey.controller;


import com.beam.sample.hey.dto.AuthenticationResult;
import com.beam.sample.hey.dto.LoginRequest;
import com.beam.sample.hey.dto.SearchDto;
import com.beam.sample.hey.dto.StateDto;
import com.beam.sample.hey.model.*;
import com.beam.sample.hey.service.AccountService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("account")
public class AccountController {

    public static final String SESSION_ACCOUNT = "session_account";

    private final AccountService accountService;

    @PostMapping("register")
    public AuthenticationResult register (@RequestParam String username,
                                          @RequestParam String password,
                                          MultipartFile file,
                                          HttpSession session) {

        AuthenticationResult result = accountService.register(username,password, file);
        if(result.isSuccess()) {
            session.setAttribute(SESSION_ACCOUNT, result.getAccount());
            return result;
        }else
            return result;
    }


    @PostMapping("login")
    public AuthenticationResult login (@RequestBody LoginRequest request,
                                       HttpSession session) {
        AuthenticationResult result = accountService.login(request.getUsername(), request.getPassword());
        if(result.isSuccess()){
            session.setAttribute(SESSION_ACCOUNT, result.getAccount());
            return result;
        }else {
            return result;
        }
    }

    @GetMapping("logout")
    public AuthenticationResult logout(HttpSession session) {
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        accountService.logout(account);
        session.removeAttribute(SESSION_ACCOUNT);
        return new AuthenticationResult();
    }


    @GetMapping("me")
    public AuthenticationResult whoami (HttpSession session) {
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        if(account != null) {
            return new AuthenticationResult().setSuccess(true).setAccount(account).setProfile(account.getProfile());
        }else{
            return new AuthenticationResult();
        }
    }


    @PostMapping("me")
    public AuthenticationResult saveProfile(@RequestParam String nickname, @RequestParam String about,
                                            MultipartFile file, HttpSession session) {
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        if(account != null) {
            Profile profile = account.getProfile().setNickname(nickname).setAbout(about);
            return new AuthenticationResult()
                    .setSuccess(true)
                    .setProfile(accountService.update(account, profile, file));
        }else {
            return null;
        }
    }

    @GetMapping("/avatar/{path}")
    public ResponseEntity<byte[]> avatar(@PathVariable String path,
                                         HttpSession session) {
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        byte[] img = accountService.readAvatar(path);
        return ResponseEntity.ok(img);

    }

    @GetMapping("search")
    public List<SearchDto> search(@RequestParam String s, HttpSession session){
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        return accountService.search(account, s);
    }

    @GetMapping("list")
    public List<Account> list(HttpSession session){
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        return accountService.list(account);
    }

    @PostMapping("new-chat")
    public Chat createNewChat(@RequestParam String content, @RequestParam String name, HttpSession session) {
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        return accountService.createNewChat(name, account, content);
    }

    @GetMapping("state")
    public StateDto stateQuery(String username) {
        return accountService.stateQuery(username);
    }

    @PostMapping("create-group")
    public GroupChat createGroup(@RequestParam String name, @RequestParam String description,
                                 @RequestParam List<String> participants, MultipartFile file, HttpSession session){
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        return accountService.createGroup(name, description, account, participants, file);
    }
    @PostMapping ("add-to-group")
    public void addToGroup(@RequestParam String id, @RequestParam String  participantNew, HttpSession session) {
        Account account = (Account) session.getAttribute(SESSION_ACCOUNT);
        accountService.addToGroup(account,participantNew,id);
    }

}
