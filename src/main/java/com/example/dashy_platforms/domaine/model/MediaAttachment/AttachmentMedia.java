package com.example.dashy_platforms.domaine.model.MediaAttachment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachmentMedia {
    @JsonProperty("type")
    private String type;
    @JsonProperty("payload")
    private PayloadAttachement payloadAttachement;

    public AttachmentMedia(String type, PayloadAttachement payloadAttachement) {
        this.type = type;
        this.payloadAttachement = payloadAttachement;
    }

    public AttachmentMedia() {}

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public PayloadAttachement getPayloadAttachement() {
        return payloadAttachement;
    }

    public void setPayloadAttachement(PayloadAttachement payloadAttachement) {
        this.payloadAttachement = payloadAttachement;
    }
}
