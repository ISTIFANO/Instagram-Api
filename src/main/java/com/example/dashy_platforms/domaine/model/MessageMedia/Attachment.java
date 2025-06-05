package com.example.dashy_platforms.domaine.model.MessageMedia;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Attachment {
    @JsonProperty("type")
    private String type;
    @JsonProperty("payload")
    private Payload payload;

}
