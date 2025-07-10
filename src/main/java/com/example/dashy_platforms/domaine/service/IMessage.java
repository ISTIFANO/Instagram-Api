package com.example.dashy_platforms.domaine.service;

import java.time.LocalDateTime;

public interface IMessage {

    /**
     * Marks a message as seen by its message ID
     * @param mid The message ID to mark as seen
     */
    void markMessageAsSeen(String mid);

    /**
     * Saves an incoming message to the database
     * @param senderId The ID of the message sender
     * @param recipientId The ID of the message recipient
     * @param content The content of the message
     * @param type The type of the message
     * @param sentAt When the message was sent
     * @param mid The message ID
     */
    void saveIncomingMessage(String senderId, String recipientId, String content, String type,
                             LocalDateTime sentAt, String mid);

    /**
     * Updates the status of a message
     * @param messageId The ID of the message to update
     * @param status The new status to set
     */
    void updateMessageStatus(String messageId, String status);

    /**
     * Adds a reaction to a message
     * @param mid The message ID to add reaction to
     * @param reaction The reaction to add
     */
    void addReactionToMessage(String mid, String reaction);
}