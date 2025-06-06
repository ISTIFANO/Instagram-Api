package com.example.dashy_platforms.domaine.model.MediaAttachment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Attachment {

    @JsonProperty("image")
    private String image;
    @JsonProperty("payload")
    private Payload payload;



}
