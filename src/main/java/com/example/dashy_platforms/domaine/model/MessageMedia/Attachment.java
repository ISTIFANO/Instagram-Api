package com.example.dashy_platforms.domaine.model.MessageMedia;

import com.example.dashy_platforms.domaine.model.DefaultAction;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Attachment {
    @JsonProperty("type")
    private String type;
    @JsonProperty("payload")
    private Payload payload;


}
