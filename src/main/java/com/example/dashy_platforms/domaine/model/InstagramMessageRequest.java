package com.example.dashy_platforms.domaine.model;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InstagramMessageRequest {
    @JsonProperty("recipient_id")
    private String recipientId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("template_data")
    private GenericTemplateData templateData;

    @JsonProperty("message_type")
    private String messageType = "TEXT";

    public InstagramMessageRequest() {}

    public InstagramMessageRequest(String recipientId, String message) {
        this.recipientId = recipientId;
        this.message = message;
    }

    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public GenericTemplateData getTemplateData() { return templateData; }
    public void setTemplateData(GenericTemplateData templateData) { this.templateData = templateData; }

    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
}