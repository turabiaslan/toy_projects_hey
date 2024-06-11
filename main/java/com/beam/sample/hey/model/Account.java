package com.beam.sample.hey.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@Document("Account")
@TypeAlias("Account")
public class Account extends Base {

    private String username;


    @JsonIgnore
    private String password;

    private Profile profile;

    private State state;

    private Date lastSeen;

/**
    @DBRef
    private List<Chat> chats;

    @DBRef
    private List<Group> groups;

    */

    public Profile getProfile() {
        if(profile == null) {
            return new Profile();
        }else {
            return profile;
        }
    }
/**
    public List<Chat> getChats() {
        if(chats == null) {
            chats = new ArrayList<>();
        }
        return chats;
    }

    public List<Group> getGroups() {
        if(groups == null) {
            groups = new ArrayList<>();
        }
        return groups;
    }
 */







}
