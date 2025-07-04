package com.example.dashy_platforms.domaine.model.Template.QuickReplie;

import com.example.dashy_platforms.domaine.model.Recipient;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Quick_replies_Request {
    @JsonProperty("recipient")
    private Recipient recipient;
@JsonProperty("message")
private Message message;
}
