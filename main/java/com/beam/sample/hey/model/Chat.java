package com.beam.sample.hey.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString
@TypeAlias("Chat")
@Document("Chat")
@Accessors(chain = true)
public class Chat extends Base {

    private String name;

    @DBRef
    private List<Account> participants;

    private List<Message> messages;

    public List<Message> getMessages() {
        if(messages == null) {
            messages = new ArrayList<>();
        }
        return messages;
    }

}
