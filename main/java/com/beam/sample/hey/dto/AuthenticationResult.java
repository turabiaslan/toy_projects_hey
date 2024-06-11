package com.beam.sample.hey.dto;


import com.beam.sample.hey.model.Account;
import com.beam.sample.hey.model.Profile;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class AuthenticationResult {

    private boolean success;

    private Profile profile;

    private Account account;

    private String message;

    private String username;
}
