package com.example.dashy_platforms.domaine.model.MediaAttachment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Attachment {

    @JsonProperty("type")
    private String type;
    @JsonProperty("payload")
    private PayloadAttachement payloadAttachement;
public Attachment(String type, PayloadAttachement payloadAttachement) {
    this.type = type;
}


}
