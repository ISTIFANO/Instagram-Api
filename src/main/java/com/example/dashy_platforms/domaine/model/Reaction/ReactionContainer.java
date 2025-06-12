package com.example.dashy_platforms.domaine.model.Reaction;

import com.example.dashy_platforms.domaine.model.Recipient;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class ReactionContainer {
    private Recipient recipient;
    private String sender_action;
    private Payload payload;

    public ReactionContainer() {}

    public ReactionContainer(String recipientId, String senderAction, String messageId, String reaction) {
        this.recipient = new Recipient(recipientId);
        this.sender_action = senderAction;
        this.payload = new Payload(messageId, reaction);
    }


}
