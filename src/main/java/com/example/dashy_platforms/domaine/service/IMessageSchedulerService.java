package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.domaine.model.ScheduleMessage.ScheduleMessageRequest;
import com.example.dashy_platforms.infrastructure.database.entities.ScheduledMessageEntity;

import java.util.List;
import java.util.Set;

public interface IMessageSchedulerService {

    /**
     * Saves a scheduled message configuration
     * @param request The scheduling configuration details
     * @return The saved scheduled message entity
     */
    ScheduledMessageEntity saveScheduledMessage(ScheduleMessageRequest request);

    /**
     * Schedules messages for all active users
     * @param request The message scheduling request
     * @param activeUsers Set of active user IDs
     * @return List of created scheduled message entities
     */
    List<ScheduledMessageEntity> scheduleMessageForAllActiveUsers(ScheduleMessageRequest request, Set<String> activeUsers);

    /**
     * Stops a scheduled message by deactivating it
     * @param scheduledMessageId ID of the message to stop
     */
    void stopScheduledMessage(Long scheduledMessageId);

    /**
     * Retrieves active scheduled messages for a recipient
     * @param recipientId The user ID to check for messages
     * @return List of active scheduled messages
     */
    List<ScheduledMessageEntity> getActiveScheduledMessages(String recipientId);

    /**
     * Determines media type from content type string
     * @param contentType The content type header
     * @return Simplified media type (image/audio/video)
     * @throws IllegalArgumentException if content type is unsupported
     */
    String getMediaType(String contentType);
}