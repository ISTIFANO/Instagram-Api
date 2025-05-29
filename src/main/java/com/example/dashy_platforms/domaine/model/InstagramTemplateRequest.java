package com.example.dashy_platforms.domaine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstagramTemplateRequest {

    @JsonProperty("recipient")
    private String recipient;

    @JsonProperty("message")
    private Message message;

    public InstagramTemplateRequest(String messageId, Recipient recipient_id) {}

    public InstagramTemplateRequest(String recipient, Message message) {
        this.recipient = recipient;
        this.message = message;
    }

}
