package com.example.dashy_platforms.domaine.model.Button_Template;

import com.example.dashy_platforms.domaine.model.Message;
import com.example.dashy_platforms.domaine.model.Recipient;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InstagramButtonTemplateRequest {
    @JsonProperty("recipient")
    private Recipient recipient;


    @JsonProperty("message")
    private Message message;
}
