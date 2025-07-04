package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.domaine.model.ScheduleMessage.ScheduleMessageRequest;
import com.example.dashy_platforms.infrastructure.database.entities.ScheduledMessageEntity;

import java.util.List;
import java.util.Set;

public interface IMessageSchedulerService {
    public List<ScheduledMessageEntity> scheduleMessageForAllActiveUsers(ScheduleMessageRequest request , Set<String> activeUsers );
}
