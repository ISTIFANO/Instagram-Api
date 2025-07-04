package com.example.dashy_platforms.domaine.model.MessageMedia;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class InstagramMediaMessageRequest {

    private String recipientId;
    private String type;
}
