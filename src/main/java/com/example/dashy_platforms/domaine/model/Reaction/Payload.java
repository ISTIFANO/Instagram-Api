package com.example.dashy_platforms.domaine.model.Reaction;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Payload {
    private String message_id;
    private String reaction;

    public Payload() {}

    public Payload(String messageId, String reaction) {
        this.message_id = messageId;
        this.reaction = reaction;
    }


    public String getMessage_id() { return message_id; }
    public void setMessage_id(String message_id) { this.message_id = message_id; }

    public String getReaction() { return reaction; }
    public void setReaction(String reaction) { this.reaction = reaction; }
}
