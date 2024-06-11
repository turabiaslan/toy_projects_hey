package com.beam.sample.hey.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;


@Getter
@Setter
@ToString
@Accessors(chain = true)
public class ReadDeliver {

    private Date readDate;

    private Status status;

    private String username;

}
