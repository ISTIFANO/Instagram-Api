package com.example.dashy_platforms.domaine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Recipient {
    @JsonProperty("id")
    private String id;

    public Recipient(String id) {
        this.id = id;
    }

    public Recipient() {}
}
