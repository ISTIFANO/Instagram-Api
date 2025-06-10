package com.example.dashy_platforms.domaine.model.MediaAttachment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
    @JsonProperty("attachment")
    private Attachment attachment;

    public Message() {}
    public Message(Attachment attachment) {
        this.attachment = attachment;
    }


}
