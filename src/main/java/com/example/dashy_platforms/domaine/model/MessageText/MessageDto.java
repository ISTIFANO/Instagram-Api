package com.example.dashy_platforms.domaine.model.MessageText;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageDto {
    @JsonProperty("text")
    private String text;



    public MessageDto(String text) {
        this.text = text;
    }
}
