package com.example.dashy_platforms.infrastructure.database.service;

import com.example.dashy_platforms.domaine.enums.IntervalUnit;
import com.example.dashy_platforms.domaine.enums.ScheduleType;
import com.example.dashy_platforms.domaine.model.InstagramMessageResponse;
import com.example.dashy_platforms.domaine.model.MessageText.InstagramMessageRequest;
import com.example.dashy_platforms.domaine.model.MessageText.MessageDto;
import com.example.dashy_platforms.domaine.model.Recipient;
import com.example.dashy_platforms.domaine.service.IInstagramService;
import com.example.dashy_platforms.domaine.service.IMessageSchedulerService;
import com.example.dashy_platforms.infrastructure.database.entities.ScheduledMessageEntity;
import com.example.dashy_platforms.infrastructure.database.repositeries.ScheduledMessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
@Transactional
@Service
public class ScheduledMessageExecutorService implements IMessageSchedulerService.IScheduledMessageExecutorService {
    private final ScheduledMessageRepository scheduledMessageRepository;
    private final IInstagramService instagramService;
    private final MessageSchedulerService schedulerService;

    public ScheduledMessageExecutorService(
            ScheduledMessageRepository scheduledMessageRepository,
            IInstagramService instagramMessageService,
            MessageSchedulerService schedulerService
    ) {
        this.scheduledMessageRepository = scheduledMessageRepository;
        this.instagramService = instagramMessageService;
        this.schedulerService = schedulerService;
    }

    @Scheduled(fixedRate = 60000)
    public void executeScheduledMessages() {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledMessageEntity> messagesToSend =
                scheduledMessageRepository.findByIsActiveTrueAndNextExecutionBefore(now);

        for (ScheduledMessageEntity scheduledMessage : messagesToSend) {
            try {
                InstagramMessageRequest messageRequest = new InstagramMessageRequest();
                messageRequest.setRecipient(new Recipient(scheduledMessage.getRecipientId()));
                messageRequest.setMessage(new MessageDto(scheduledMessage.getMessageContent()));

                InstagramMessageResponse response = instagramService.sendTextMessage(messageRequest);

                scheduledMessage.setLastExecution(now);
                scheduledMessage.setExecutionCount(scheduledMessage.getExecutionCount() + 1);

                if (shouldContinueScheduling(scheduledMessage)) {
                    scheduledMessage.setNextExecution(calculateNextExecution(scheduledMessage));
                } else {
                    scheduledMessage.setIsActive(false);
                }

                scheduledMessageRepository.save(scheduledMessage);

            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi du message planifié: " + e.getMessage());
            }

        }
    }

    private boolean shouldContinueScheduling(ScheduledMessageEntity message) {
        if (message.getMaxExecutions() != null &&
                message.getExecutionCount() >= message.getMaxExecutions()) {
            return false;
        }if (message.getScheduleType() == ScheduleType.ONCE) {
            return false;
        }

        return true;
    }
    private LocalDateTime calculateNextExecution(ScheduledMessageEntity message) {
        LocalDateTime lastExecution = message.getLastExecution();

        switch (message.getScheduleType()) {
            case DAILY:
                return lastExecution.plusDays(1);

            case WEEKLY:
                return lastExecution.plusWeeks(1);

            case MONTHLY:
                return lastExecution.plusMonths(1);

            case INTERVAL:
                if (message.getIntervalUnit() == IntervalUnit.MINUTES) {
                    return lastExecution.plusMinutes(message.getIntervalValue());
                } else if (message.getIntervalUnit() == IntervalUnit.HOURS) {
                    return lastExecution.plusHours(message.getIntervalValue());
                } else {
                    return lastExecution.plusDays(message.getIntervalValue());
                }

            default:
                return lastExecution.plusHours(1);
        }
    }

}
