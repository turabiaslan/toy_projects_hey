package com.beam.sample.hey.dto;


import com.beam.sample.hey.model.Chat;
import com.beam.sample.hey.model.State;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ConversationsDto {

    private String chatId;

    private String chatName;

    private Date lastMessageDate;

    private State state;

    private int unreadMessages;

    private String avatar;

    private boolean g;
}
