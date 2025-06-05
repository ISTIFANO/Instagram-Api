package com.example.dashy_platforms.domaine.model.MessageMedia;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Payload {
    @JsonProperty("attachment_id")
    private String attachmentId;
}
