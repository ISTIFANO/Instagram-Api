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
this.attachmentId = attachmentId;
    }
    public AttachementResponse(String errorMessage) {

    }
    public String getErrorMessage() {
        return errorMessage;
    }
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    public Long getAttachmentId() {
        return attachmentId;
    }
    public void setAttachmentId(Long attachmentId) {
        this.attachmentId = attachmentId;
    }
    public AttachementResponse(Long attachmentId, String errorMessage) {

    }
}
