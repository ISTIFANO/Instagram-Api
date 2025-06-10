package com.example.dashy_platforms.domaine.model.MediaAttachment;

import com.example.dashy_platforms.domaine.model.Recipient;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

@Setter
@Getter
public class AttachmentRequest {
    @JsonProperty("recipient")
    private Recipient recipient;
    @JsonProperty("message")
    private Message message;
    @JsonProperty("platform ")
    private String platform;
    public AttachmentRequest() {
    }

    public AttachmentRequest(Recipient recipient, Message message) {
        this.recipient = recipient;
        this.message = message;
    }

    public AttachmentRequest(Recipient recipient, Message message, String platform) {
        this.recipient = recipient;
        this.message = message;
        this.platform = platform;
    }

    public void setRecipient(Recipient recipient) {
        this.recipient = recipient;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "AttachmentRequest{" +
                "recipient=" + recipient +
                ", message=" + message +
                ", platform='" + platform + '\'' +
                '}';
    }
}
