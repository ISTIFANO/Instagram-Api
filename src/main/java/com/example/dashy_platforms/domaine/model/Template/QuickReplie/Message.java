package com.example.dashy_platforms.domaine.model.Template.QuickReplie;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Message {

    @JsonProperty("text")
    private String text;

    @JsonProperty("quick_replies")
private List<Quick_replies> quick_replies;}
