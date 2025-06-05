package com.example.dashy_platforms.domaine.model.MessageFile;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Attachment {
    @JsonProperty("type")
    private String type ="image";
    @JsonProperty("payload")
    private Payload payload;

}
