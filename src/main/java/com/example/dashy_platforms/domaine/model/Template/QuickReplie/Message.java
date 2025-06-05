package com.example.dashy_platforms.domaine.model.Template.QuickReplie;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class Message {

    @JsonProperty("text")
    private String text;

    @JsonProperty("quick_replies")
private List<Quick_replies> quick_replies;}
