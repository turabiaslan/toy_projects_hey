package com.beam.sample.hey.model;


import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

@Getter
@Setter

public class Base {

    @Id
    private String id;


    @Override
    public boolean equals(Object obj) {
        Base other = (Base) obj;
        if (other == null) {
            return false;
        }else {
            return getId().equals(other.getId());
        }
    }
}
