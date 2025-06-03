package com.example.dashy_platforms.domaine.model.MessageText;

import com.example.dashy_platforms.domaine.model.Recipient;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstagramMessageRequest {
    @JsonProperty("recipient")
    private Recipient recipient;
    @JsonProperty("message")
    private MessageDto message;}
