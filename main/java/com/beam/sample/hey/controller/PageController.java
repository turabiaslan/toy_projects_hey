package com.beam.sample.hey.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
public class PageController {


    @RequestMapping({"/","/register", "/login", "/logout", "/home", "/new-group", "/update-group/{id}", "/features"})
    public String index() {
        return "index";
    }


    @RequestMapping({"/logout"})
    public String logout(HttpSession session) {
        session.removeAttribute(AccountController.SESSION_ACCOUNT);
        return index();
    }
}