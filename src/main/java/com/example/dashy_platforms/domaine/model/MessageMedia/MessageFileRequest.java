package com.example.dashy_platforms.domaine.model.MessageMedia;

import com.example.dashy_platforms.domaine.model.Recipient;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageFileRequest {
    @JsonProperty("recipient")
    private Recipient recipient;
    @JsonProperty("message")
    private Message message;
    @JsonProperty("messaging_type")
    private String messaging_type;
    @JsonProperty("platform")
    private String platform;
}
