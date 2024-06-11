package com.beam.sample.hey.dto;


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
public class StateDto {

    private State state;

    private Date lastSeen;
}
