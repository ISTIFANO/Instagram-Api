package com.example.dashy_platforms.domaine.model.MediaAttachment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AttachmentDto {
    @JsonProperty("message")
    private Message message;
    @JsonProperty("platform ")
    private String platform;
    public AttachmentDto() {}
    public AttachmentDto(Message message, String platform) {
    }

}
