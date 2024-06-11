package com.beam.sample.hey.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Getter
@Setter
@ToString
@Accessors(chain = true)
public class Message {


    private String content;

    private Date sendDate;

    private List<ReadDeliver> deliver;

    private List<ReadDeliver> read;

    private Status status;

    private int readCnt;

    private int deliverCnt;

    private String sender;

    private int participantCnt;

    //TODO think about how are u going to implement writing

    public List<ReadDeliver> getDeliver(){
        if(deliver ==null) {
            deliver = new ArrayList<>();
        }
        return deliver;
    }

    public List<ReadDeliver> getRead() {
        if(read == null) {
            read = new ArrayList<>();
        }
        return read;
    }

}
