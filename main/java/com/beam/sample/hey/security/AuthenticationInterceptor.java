package com.beam.sample.hey.security;


import com.beam.sample.hey.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.beam.sample.hey.controller.AccountController.SESSION_ACCOUNT;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final AccountRepository accountRepository;

    /**
     * @Override public boolean preHandle(HttpServletRequest request,
     * HttpServletResponse response,
     * Object handler) throws Exception {
     * Object account = request.getSession().getAttribute(SESSION_ACCOUNT);
     * if (account != null) {
     * return true;
     * } else {
     * Account acc = accountRepository.findByUsername("turabi");
     * request.getSession().setAttribute(SESSION_ACCOUNT, acc);
     * return true;
     * }
     * <p>
     * }
     */

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        Object account = request.getSession().getAttribute(SESSION_ACCOUNT);
        if (account != null) {
            return true;
        } else {
            if (request.getRequestURI().equals("/") ||
                    request.getRequestURI().startsWith("/3rd") ||
                    request.getRequestURI().startsWith("/app") ||
                    request.getRequestURI().startsWith("/login") ||
                    request.getRequestURI().startsWith("/logout") ||
                    request.getRequestURI().startsWith("/register") ||
                    request.getRequestURI().startsWith("/account/login")) {
                return true;
            } else {
                response.setStatus(401);
                return false;
            }

        }
    }

}
