package com.example.dashy_platforms.domaine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

@Getter
@Setter
public class InstagramTemplateRequest {

    private Recipient recipient;

    private Message message;

    public InstagramTemplateRequest() {

    }
    public InstagramTemplateRequest(Recipient recipient, Message message) {
        this.recipient = recipient;
        this.message = message;
    }
}
