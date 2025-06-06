package com.example.dashy_platforms.domaine.model.Template.Button_Template;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Attachment {

    @JsonProperty("type")
    private String type = "template";

    @JsonProperty("payload")
    private Payload payload;
}

