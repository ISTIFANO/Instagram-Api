package com.example.dashy_platforms.infrastructure.database.service;

import com.example.dashy_platforms.domaine.enums.IntervalUnit;
import com.example.dashy_platforms.domaine.enums.ScheduleType;
import com.example.dashy_platforms.domaine.helper.JsoonFormat;
import com.example.dashy_platforms.domaine.model.*;
import com.example.dashy_platforms.domaine.model.BroadcastMessage.InstagramMessageR;
import com.example.dashy_platforms.domaine.model.BroadcastMessage.MessageTemplate;
import com.example.dashy_platforms.domaine.model.MessageText.InstagramMessageRequest;
import com.example.dashy_platforms.domaine.model.MessageText.MessageDto;
import com.example.dashy_platforms.domaine.model.ScheduleMessage.TemplateScheduler;
import com.example.dashy_platforms.domaine.model.Template.Button_Template.InstagramButtonTemplateRequest;
import com.example.dashy_platforms.domaine.service.*;
import com.example.dashy_platforms.infrastructure.database.entities.InstagramUserEntity;
import com.example.dashy_platforms.infrastructure.database.entities.MessageEntity;
import com.example.dashy_platforms.infrastructure.database.entities.ScheduledMessageEntity;
import com.example.dashy_platforms.infrastructure.database.entities.TemplateInstagram;
import com.example.dashy_platforms.infrastructure.database.repositeries.InstagramUserRepository;
import com.example.dashy_platforms.infrastructure.database.repositeries.ScheduledMessageRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Transactional
@Service
@Slf4j
public class ScheduledMessageExecutorService implements IScheduledMessageExecutorService {
    private final ScheduledMessageRepository scheduledMessageRepository;
    private final IInstagramService instagramService;
    private final MessageSchedulerService schedulerService;
    private final InstagramUserService instagramUserService;
private final ITemplateService templateService;
    private final InstagramUserRepository instagramUserRepository;

    public ScheduledMessageExecutorService(
            ScheduledMessageRepository scheduledMessageRepository,
            IInstagramService instagramMessageService,
            MessageSchedulerService schedulerService,
            ITemplateService templateService,InstagramUserService instagramUserService,
            InstagramUserRepository instagramUserRepository) {
        this.scheduledMessageRepository = scheduledMessageRepository;
        this.instagramService = instagramMessageService;
        this.schedulerService = schedulerService;
        this.templateService = templateService;
        this.instagramUserService = instagramUserService;
        this.instagramUserRepository = instagramUserRepository;
    }
@Override
    @Scheduled(fixedRate = 60000)
    public void executeScheduledMessages() {
        LocalDateTime now = LocalDateTime.now();
        List<ScheduledMessageEntity> messagesToSend =
                scheduledMessageRepository.findByIsActiveTrueAndNextExecutionBefore(now);

        for (ScheduledMessageEntity scheduledMessage : messagesToSend) {
            try {

                if ("BIRTHDAY".equals(scheduledMessage.getSchedule_sending_date_type())) {
                    return ;
                }
                switch (scheduledMessage.getMessagetype()) {
                    case "TEXT":
                        this.instagramService.sendTextToAllActiveUsers(scheduledMessage.getMessageContent());
                        break;
                    case "MEDIA":
                        this.instagramService.sendMediaToAllActiveUsers(scheduledMessage.getAttachment(), scheduledMessage.getMediaType());
                        break;
                    case "TEMPLATE":
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                        InstagramTemplateRequest templateData = this.templateService.getTemplateDataByCode(scheduledMessage.getMessageContent());
                        MessageTemplate templatemsg = new MessageTemplate();
                        templatemsg.setType("GENERIC");

                        MessageTemplate.GenericContent genericContent = new MessageTemplate.GenericContent();
                        List<MessageTemplate.GenericContent.Element> elements = new ArrayList<>();
                        List<ElementModel> instagramElements = templateData.getMessage()
                                .getAttachment()
                                .getPayload()
                                .getElements();

                        for (ElementModel instagramElement : instagramElements) {
                            MessageTemplate.GenericContent.Element element = new MessageTemplate.GenericContent.Element();
                            element.setTitle(instagramElement.getTitle());
                            element.setImage_url(instagramElement.getImage_url());
                            element.setSubtitle(instagramElement.getSubtitle());

                            if (instagramElement.getDefaultAction() != null) {
                                MessageTemplate.GenericContent.Element.DefaultAction defaultAction =
                                        new MessageTemplate.GenericContent.Element.DefaultAction();
                                defaultAction.setType(instagramElement.getDefaultAction().getType());
                                defaultAction.setUrl(instagramElement.getDefaultAction().getUrl());
                                element.setDefault_action(defaultAction);
                            }
                            if (instagramElement.getButtons() != null) {
                                List<MessageTemplate.Button> buttons = new ArrayList<>();
                                for (TemplateButton instagramButton : instagramElement.getButtons()) {
                                    MessageTemplate.Button button = new MessageTemplate.Button();
                                    button.setType(instagramButton.getType());
                                    button.setUrl(instagramButton.getUrl());
                                    button.setTitle(instagramButton.getTitle());
                                    button.setPayload(instagramButton.getPayload());
                                    buttons.add(button);
                                }
                                element.setButtons(buttons);
                            }

                            elements.add(element);
                        }

                        genericContent.setElements(elements);
                        templatemsg.setContent(genericContent);

                        this.instagramService.sendTemplateToAllActiveUsers(templatemsg);
                        break;
                      case "QUICK_REPLY":
                          InstagramMessageR quick_replies = this.templateService.getQuick_replies(scheduledMessage.getMessageContent());
                          this.instagramService.sendCustomMessageToAllActiveUsers(quick_replies);
                        break;
                    case "TEMPLATE_BUTTON":
                        InstagramButtonTemplateRequest template_button = this.templateService.getTemplatebutton(scheduledMessage.getMessageContent());

                        this.templateService.sendButtonTemplateToAllActiveUsers(template_button);

                        break;
                    default:
                        log.warn("Type de message non reconnu: {}", scheduledMessage.getMessagetype());
                }
                scheduledMessage.setLastExecution(now);
                scheduledMessage.setExecutionCount(scheduledMessage.getExecutionCount() + 1);

                if (shouldContinueScheduling(scheduledMessage)) {
                    scheduledMessage.setNextExecution(calculateNextExecution(scheduledMessage));
                } else {
                    scheduledMessage.setIsActive(false);
                    log.info("Désactivation du message planifié ID: {}", scheduledMessage.getId());
                }

                scheduledMessageRepository.save(scheduledMessage);

            } catch (Exception e) {
                log.error("Erreur lors de l'envoi du message planifié ID {}: {}",
                        scheduledMessage.getId(), e.getMessage());
//                saveFailedExecution(scheduledMessage, e.getMessage());
            }
        }
    }

    @Scheduled(fixedRate = 86400000) // Run every 24 hours
    public void shouldhappybirthday() {
        LocalDate today = LocalDate.now();
        int day = today.getDayOfMonth();
        int month = today.getMonthValue();

        List<ScheduledMessageEntity> messagesToSend = scheduledMessageRepository.findActiveBirthdayMessages();
        List<InstagramUserEntity> users = instagramUserRepository.findAll().stream()
                .filter(user -> {
                    try {
                        LocalDate birthday = LocalDate.parse(user.getBirthday());
                        return birthday.getDayOfMonth() == day && birthday.getMonthValue() == month;
                    } catch (Exception e) {
                        return false;
                    }
                }).toList();
        for (ScheduledMessageEntity scheduledMessage : messagesToSend) {
            for (InstagramUserEntity user : users) {
                scheduledMessage.setRecipientId(user.getInstagramUserId());
                switch (scheduledMessage.getMessagetype()) {
                    case "TEXT" -> {
                        InstagramMessageRequest instagramMessageRequest = new InstagramMessageRequest();
                        instagramMessageRequest.getRecipient().setId(user.getInstagramUserId());
                        instagramMessageRequest.getMessage().setText(scheduledMessage.getMessageContent());

                        instagramService.sendTextMessage(instagramMessageRequest);
                    }
                    case "TEMPLATE" -> {
                        InstagramTemplateRequest templateData = this.templateService.getTemplateDataByCode(scheduledMessage.getMessageContent());
                        templateData.getRecipient().setId(user.getInstagramUserId());
                        instagramService.sendGenericTemplate(user.getInstagramUserId(), templateData);
                    }
                }
            }
        }
    }


    private boolean shouldContinueScheduling(ScheduledMessageEntity message) {
        if (message.getMaxExecutions() != null &&
                message.getExecutionCount() >= message.getMaxExecutions()) {
            return false;
        }
        if (message.getScheduleType() == ScheduleType.ONCE) {
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
    private String messageTemplateObject(ScheduledMessageEntity scheduledMessage) {
  ScheduledMessageEntity templateMsg = scheduledMessageRepository.findById(scheduledMessage.getId()).get();

  String template = templateMsg.getMessageContent();

        return template;
    }





}



