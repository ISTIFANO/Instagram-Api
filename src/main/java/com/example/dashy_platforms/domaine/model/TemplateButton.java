package com.example.dashy_platforms.domaine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemplateButton {
    @JsonProperty("type")
    private String type;

    @JsonProperty("url")
    private String url;

    @JsonProperty("title")
    private String title;

    @JsonProperty("payload")
    private String payload;

    public TemplateButton() {}

    public TemplateButton(String type, String title) {
        this.type = type;
        this.title = title;
    }

    public void setType(String type) { this.type = type; }

    public void setUrl(String url) { this.url = url; }

    public void setTitle(String title) { this.title = title; }

    public void setPayload(String payload) { this.payload = payload; }
}