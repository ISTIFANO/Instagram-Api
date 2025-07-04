package com.example.dashy_platforms.infrastructure.database.service;


import com.example.dashy_platforms.domaine.enums.IntervalUnit;
import com.example.dashy_platforms.domaine.model.ScheduleMessage.ScheduleMessageRequest;
import com.example.dashy_platforms.domaine.service.IMessageSchedulerService;
import com.example.dashy_platforms.infrastructure.database.entities.ScheduledMessageEntity;
import com.example.dashy_platforms.infrastructure.database.repositeries.MessageRepository;
import com.example.dashy_platforms.infrastructure.database.repositeries.ScheduledMessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MessageSchedulerService implements IMessageSchedulerService {


    @Autowired
    private ScheduledMessageRepository scheduledMessageRepository;

    @Autowired
    InstagramService instagramService;


    @Override
    public List<ScheduledMessageEntity> scheduleMessageForAllActiveUsers(ScheduleMessageRequest request ,Set<String> activeUsers ) {
        try {
            List<ScheduledMessageEntity> scheduledMessages = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();

            for (String userId : activeUsers) {
                ScheduledMessageEntity scheduledMessage = new ScheduledMessageEntity();
                scheduledMessage.setRecipientId(userId);

                switch (request.getMessageType()) {
                    case "TEMPLATE":
                        scheduledMessage.setMessageContent(request.getMessageContent().getCode());
                        break;

                    case "QUICK_REPLY":
                        scheduledMessage.setMessageContent(request.getMessageContent().getCode());
                        break;

                    case "TEMPLATE_BUTTON":
                        scheduledMessage.setMessageContent(request.getMessageContent().getCode());
                        break;

                    case "TEXT":
                        scheduledMessage.setMessageContent(request.getMessageContent().getText());
                        break;
                    case "MEDIA":
                        scheduledMessage.setMessageContent(request.getAttachmentId());
                        scheduledMessage.setAttachment(request.getAttachmentId());
                        scheduledMessage.setMediaType(request.getMediaType());
                        break;

                    default:
                        scheduledMessage.setMessageContent(request.getMessageContent().getText());
                        break;
                }
                scheduledMessage.setMessagetype(request.getMessageType());
                scheduledMessage.setScheduleType(request.getScheduleType());
                scheduledMessage.setIntervalValue(request.getIntervalValue());
                scheduledMessage.setIntervalUnit(request.getIntervalUnit());
                scheduledMessage.setDayOfWeek(request.getDayOfWeek());
                scheduledMessage.setDayOfMonth(request.getDayOfMonth());
                scheduledMessage.setHourOfDay(request.getHourOfDay());
                scheduledMessage.setMinuteOfHour(request.getMinuteOfHour());
                scheduledMessage.setMaxExecutions(request.getMaxExecutions());
                LocalDateTime nextExecution = calculateNextExecution(request);
                scheduledMessage.setNextExecution(nextExecution);
                scheduledMessages.add(scheduledMessageRepository.save(scheduledMessage));
            }
            return scheduledMessages;

        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON in messageContent", e);
        }
    }
    private LocalDateTime calculateNextExecution(ScheduleMessageRequest request) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = request.getStartDate() != null ? request.getStartDate() : now;

        switch (request.getScheduleType()) {
            case DAILY:
                return startDate.plusDays(1)
                        .withHour(request.getHourOfDay() != null ? request.getHourOfDay() : 9)
                        .withMinute(request.getMinuteOfHour() != null ? request.getMinuteOfHour() : 0)
                        .withSecond(0);

            case WEEKLY:
                LocalDateTime nextWeek = startDate.plusWeeks(1);
                if (request.getDayOfWeek() != null) {
                    nextWeek = nextWeek.with(DayOfWeek.of(request.getDayOfWeek()));
                }
                return nextWeek
                        .withHour(request.getHourOfDay() != null ? request.getHourOfDay() : 9)
                        .withMinute(request.getMinuteOfHour() != null ? request.getMinuteOfHour() : 0)
                        .withSecond(0);

            case MONTHLY:
                LocalDateTime nextMonth = startDate.plusMonths(1);
                if (request.getDayOfMonth() != null) {
                    nextMonth = nextMonth.withDayOfMonth(request.getDayOfMonth());
                }
                return nextMonth
                        .withHour(request.getHourOfDay() != null ? request.getHourOfDay() : 9)
                        .withMinute(request.getMinuteOfHour() != null ? request.getMinuteOfHour() : 0)
                        .withSecond(0);
            case INTERVAL:
                if (request.getIntervalUnit() == IntervalUnit.MINUTES) {
                    return startDate.plusMinutes(request.getIntervalValue());
                } else if (request.getIntervalUnit() == IntervalUnit.HOURS) {
                    return startDate.plusHours(request.getIntervalValue());
                } else {
                    return startDate.plusDays(request.getIntervalValue());
                }
            case ONCE:
                return startDate;
            default:
                return startDate.plusHours(1);
        }
    }
    public void stopScheduledMessage(Long scheduledMessageId) {
        ScheduledMessageEntity message = scheduledMessageRepository.findById(scheduledMessageId)
                .orElseThrow(() -> new RuntimeException("Message planifié non trouvé"));
        message.setIsActive(false);
        scheduledMessageRepository.save(message);
    }

    public List<ScheduledMessageEntity> getActiveScheduledMessages(String recipientId) {
        return scheduledMessageRepository.findByRecipientIdAndIsActiveTrue(recipientId);
    }
    public String getMediaType(String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("Content type is null");
        }

        if (contentType.startsWith("image/")) return "image";
        if (contentType.startsWith("audio/")) return "audio";
        if (contentType.startsWith("video/")) return "video";

        throw new IllegalArgumentException("Unsupported media type: " + contentType);
    }
}
