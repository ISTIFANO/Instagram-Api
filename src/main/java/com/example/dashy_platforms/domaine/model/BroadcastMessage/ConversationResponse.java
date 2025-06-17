package com.example.dashy_platforms.domaine.model.BroadcastMessage;

import lombok.Data;

import java.util.List;

@Data
public class ConversationResponse {
    private List<Conversation> data;
}
