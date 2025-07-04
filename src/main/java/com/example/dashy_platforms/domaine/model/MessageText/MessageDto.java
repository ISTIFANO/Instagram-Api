package com.example.dashy_platforms.domaine.model.MessageText;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class MessageDto {
    @JsonProperty("text")
    private String text;



    public MessageDto(String text) {
        this.text = text;
    }
}
