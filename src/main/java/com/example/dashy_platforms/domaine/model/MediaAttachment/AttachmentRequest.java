package com.example.dashy_platforms.domaine.model.MediaAttachment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachmentRequest {
    @JsonProperty("message")
    private Message message;
    @JsonProperty("platform ")
    private String platform;
}
