package com.beam.sample.hey.dto;


import com.beam.sample.hey.model.Account;
import com.beam.sample.hey.model.Message;
import com.beam.sample.hey.model.State;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class SearchDto {

    private String name;

    private Account account;

    private String chat_id;

    private Message lastMessage;

    private String about;

    private State state;

    private String avatar;

}
