package com.example.dashy_platforms.domaine.service;

import java.time.LocalDateTime;

public interface IMessage {

    void markMessageAsSeen(String mid);


    void saveIncomingMessage(String senderId, String recipientId, String content,
                             String type, LocalDateTime sentAt, String mid);


    void updateMessageStatus(String messageId, String status);


    void addReactionToMessage(String mid, String reaction);
}
