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
@Accessors(chain = true)
@TypeAlias("Contacts")
@Document("Contacts")
public class Contacts extends Base{

    @DBRef
    private Account owner;

    @DBRef
    private List<Account> accounts;

    @DBRef
    private List<GroupChat> groups;

    public List<Account> getAccounts() {
        if (accounts == null) {
            accounts = new ArrayList<>();
        }
        return accounts;
    }

    public List<GroupChat> getGroups(){
        if (groups == null) {
            groups = new ArrayList<>();
        }
        return groups;
    }

}
