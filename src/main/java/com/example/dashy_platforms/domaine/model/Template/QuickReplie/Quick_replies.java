package com.example.dashy_platforms.domaine.model.Template.QuickReplie;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quick_replies {
    @JsonProperty("content_type")
    private String contentType;

    @JsonProperty("title")
    private String title;

    @JsonProperty("payload")
    private String payload;
}
