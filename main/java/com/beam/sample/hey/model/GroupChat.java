package com.beam.sample.hey.model;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Accessors(chain = true)
@TypeAlias("GroupChat")
@Document("GroupChat")

public class GroupChat extends Chat {

    private String avatar;


    private String description;








}
