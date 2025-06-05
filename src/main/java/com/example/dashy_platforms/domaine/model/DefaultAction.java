package com.example.dashy_platforms.domaine.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DefaultAction {
    @JsonProperty("type")
    private String type;

    @JsonProperty("url")
    private String url;

    public DefaultAction() {}

    public DefaultAction(String type, String url) {
        this.type = type;
        this.url = url;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUrl() { return url; }
    public void setUrl(String url) {
        this.url = url;


    }}

