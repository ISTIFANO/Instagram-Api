package com.example.dashy_platforms.domaine.model.BroadcastMessage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Message {
    private String id;
    private From from;
    private To to;
    private String message;
    @JsonProperty("created_time")
    private String createdTime;
}