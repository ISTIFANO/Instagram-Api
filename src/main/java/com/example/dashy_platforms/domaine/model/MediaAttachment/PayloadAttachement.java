package com.example.dashy_platforms.domaine.model.MediaAttachment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PayloadAttachement {
    @JsonProperty("url")
    private String url;
    @JsonProperty("is_reusable")
    private Boolean isReusable=true;

    public PayloadAttachement(String url, Boolean isReusable) {
        this.url = url;
        this.isReusable = isReusable;
    }
    public PayloadAttachement() {}
}
