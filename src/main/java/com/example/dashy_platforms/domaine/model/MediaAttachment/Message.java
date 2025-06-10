package com.example.dashy_platforms.domaine.model.MediaAttachment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {
    @JsonProperty("attachment")
    private AttachmentMedia attachmentMedia;

    public Message() {}
    public Message(AttachmentMedia attachmentMedia) {
        this.attachmentMedia = attachmentMedia;
    }

}
