package com.example.dashy_platforms.domaine.model.MessageSeen;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class InstagramWebhookEntry {

    private String id;
    private Long time;
    private List<InstagramMessaging> messaging;
}
