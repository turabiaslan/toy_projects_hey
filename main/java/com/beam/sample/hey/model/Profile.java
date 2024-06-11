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
public class Profile {

    private String nickname;

    private String avatar;

    private String about;




    // FIXME not sure - may cause infinite loop
}
