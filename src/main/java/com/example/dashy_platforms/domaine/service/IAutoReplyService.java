package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.domaine.model.Autoaction.AutoactionConfigDTO;
import com.example.dashy_platforms.domaine.model.Autoaction.AutoactionResponseDTO;
import com.example.dashy_platforms.infrastructure.database.entities.Autoaction;

import java.time.LocalDateTime;

public interface IAutoReplyService {

    /**
     * Checks incoming message and sends appropriate auto-reply if conditions are met
     * @param senderId The ID of the message sender
     * @param message The message content
     * @param receivedAt When the message was received
     */
    void checkAndReply(String senderId, String message, LocalDateTime receivedAt);

    /**
     * Updates auto-reply configuration
     * @param configDTO Configuration data transfer object
     * @return Updated configuration response
     */
    AutoactionResponseDTO updateAutoactionConfig(AutoactionConfigDTO configDTO);

    /**
     * Retrieves current auto-reply configuration
     * @param companyName Name of the company
     * @return Current configuration
     */
    AutoactionConfigDTO getAutoactionConfig(String companyName);

    /**
     * Gets autoaction entity by company name
     * @param companyName Name of the company
     * @return Autoaction entity
     */
    Autoaction getAutoaction(String companyName);

    /**
     * Marks a specific message as seen
     * @param senderId ID of the sender
     * @param mid Message ID
     * @param seenAt When the message was seen
     */
    void markMessageSeenByMid(String senderId, String mid, LocalDateTime seenAt);

    /**
     * Marks all messages from a sender as seen up to a certain time
     * @param senderId ID of the sender
     * @param seenUntil Cutoff time for marking messages as seen
     */
    void markMessagesSeenUntil(String senderId, LocalDateTime seenUntil);

    /**
     * Processes automatic replies based on message content
     * @param senderId ID of the message sender
     * @param messageText Content of the message
     * @param eventTime When the message was received
     */
    void ProcessSendingAutoReplay(String senderId, String messageText, LocalDateTime eventTime);
}