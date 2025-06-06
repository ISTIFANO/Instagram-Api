package com.example.dashy_platforms.domaine.model.MediaAttachment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AttachementResponse {
    @JsonProperty("attachment_id")
    private Long attachmentId;
    @JsonProperty("error_message")
    private String errorMessage;

    public AttachementResponse() {
        super();
    }
    public AttachementResponse(Long attachmentId) {

    }
    public AttachementResponse(String errorMessage) {

    }
}
