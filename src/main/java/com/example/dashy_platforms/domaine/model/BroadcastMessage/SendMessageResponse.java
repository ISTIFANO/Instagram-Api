package com.example.dashy_platforms.domaine.model.BroadcastMessage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SendMessageResponse {
    @JsonProperty("recipient_id")
    private String recipientId;
    @JsonProperty("message_id")
    private String messageId;
}
