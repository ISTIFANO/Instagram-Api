package com.example.dashy_platforms.infrastructure.database.service;

import ch.qos.logback.classic.Logger;
import com.example.dashy_platforms.domaine.helper.JsoonFormat;
import com.example.dashy_platforms.domaine.model.Autoaction.AutoactionConfigDTO;
import com.example.dashy_platforms.domaine.model.Autoaction.AutoactionResponseDTO;
import com.example.dashy_platforms.domaine.model.InstagramMessageResponse;
import com.example.dashy_platforms.domaine.model.InstagramTemplateRequest;
import com.example.dashy_platforms.infrastructure.database.entities.Autoaction;
import com.example.dashy_platforms.infrastructure.database.entities.Company;
import com.example.dashy_platforms.infrastructure.database.entities.TemplateInstagram;
import com.example.dashy_platforms.infrastructure.database.repositeries.AutoactionRepository;
import com.example.dashy_platforms.infrastructure.database.repositeries.CompanyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AutoReplyService {

    private final AutoactionRepository autoactionRepository;
private final CompanyRepository companyRepository;
private final InstagramService instagramService;
private final TemplateService templateService;

    @Value("${instagram.graph.api.url}")
    private String graphApiUrl;

    @Value("${instagram.graph.access.token:default-value}")
    private String accessToken;
    private final RestTemplate restTemplate;
    @Value("${instagram.graph.page.id}")
    private String pageId;
    @Value("${instagram.graph.access.token}")
    private String pageAccessToken;
    @Autowired
    private MessageServiceImp messageServiceImp;
public AutoReplyService(AutoactionRepository autoactionRepository,CompanyRepository companyRepository, InstagramService instagramService, TemplateService templateService) {
    this.autoactionRepository = autoactionRepository;
    this.templateService = templateService;
    this.instagramService = instagramService;
    this.companyRepository = companyRepository;
    this.restTemplate = new RestTemplate();
}
    public void checkAndReply(String senderId, String message, LocalDateTime receivedAt) {
        Autoaction autoaction = autoactionRepository.findByCompanyName("DASHY")
                .orElseThrow(() -> new RuntimeException("Aucune configuration trouvée"));

        if (shouldReply(autoaction, receivedAt)) {
            sendAppropriateReply(autoaction, senderId, receivedAt);
        }
    }

    public boolean shouldReply(Autoaction autoaction, LocalDateTime receivedAt) {
        List<String> nonWorkingDays = Arrays.asList(autoaction.getNonWorkingDays().split(","));
        DayOfWeek currentDay = receivedAt.getDayOfWeek();

        // Check if it's a non-working day
        if (nonWorkingDays.contains(currentDay.name())) {
            return true;
        }

        LocalTime currentTime = receivedAt.toLocalTime();

        // Check if during lunch break
        if (isDuringPause(autoaction, currentTime)) {
            return true;
        }

        // Check if outside working hours
        Company company = autoaction.getCompany();
        return currentTime.isBefore(company.getWorkStartTime()) ||
                currentTime.isAfter(company.getWorkEndTime());
    }

    private void sendAppropriateReply(Autoaction autoaction, String receiverId, LocalDateTime receivedAt) {
     try {
            switch (autoaction.getMessageType()) {
                case "TEMPLATE" -> sendTemplateMessage(autoaction, receiverId);

                case "TEXT" -> {
                    if (!isPageMessage(receiverId)) {
                        instagramService.sendTextMessage(receiverId, autoaction.getMessage());
                    }
                }

                case "AUTO" -> {
                    if (!isPageMessage(receiverId)) {
                        String message = buildTextMessage(autoaction, receivedAt);
                        instagramService.sendTextMessage(receiverId, message);
                    }
                }

                default -> {
                    throw new IllegalArgumentException("Unknown message type: " + autoaction.getMessageType());
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to send reply message", e);
        }

    }


    private void sendTemplateMessage(Autoaction autoaction, String receiverId) throws JsonProcessingException {

    TemplateInstagram templateInstagram = templateService.getTemplateByCode((String)autoaction.getMessage());



        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        InstagramTemplateRequest templateObject = objectMapper.readValue(
                templateInstagram.getTemplateContent(),
                InstagramTemplateRequest.class
        );
        if (isPageMessage(receiverId)) {
            return;
        }
        templateObject.getRecipient().setId(receiverId);
        templateService.sendGenericTemplate(receiverId, templateObject);
    }

    private String buildTextMessage(Autoaction autoaction, LocalDateTime receivedAt) {
        DayOfWeek day = receivedAt.getDayOfWeek();
        LocalTime currentTime = receivedAt.toLocalTime();
        Company company = autoaction.getCompany();
        if (Arrays.asList(autoaction.getNonWorkingDays().split(",")).contains(day.name())) {
            return String.format("Nous sommes fermés le %s. Horaires: %s-%s (%s)",
                    day.getDisplayName(TextStyle.FULL, Locale.FRENCH),
                    company.getWorkStartTime(),
                    company.getWorkEndTime(),
                    getWorkingDays(autoaction));
        } else if (isDuringPause(autoaction, currentTime)) {
            return String.format("Pause déjeuner (%s-%s). Nous répondrons dès notre retour.",
                    autoaction.getPauseStart(),
                    autoaction.getPauseEnd());
        } else {
            return String.format("Hors horaires (%s-%s). Merci de revenir pendant nos heures d'ouverture.",
                    company.getWorkStartTime(),
                    company.getWorkEndTime());
        }
    }

    private boolean isDuringPause(Autoaction autoaction, LocalTime currentTime) {
        return !currentTime.isBefore(autoaction.getPauseStart()) &&
                !currentTime.isAfter(autoaction.getPauseEnd());
    }

    private String getWorkingDays(Autoaction autoaction) {
        List<String> allDays = Arrays.asList(
                "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY",
                "FRIDAY", "SATURDAY", "SUNDAY"
        );
        List<String> nonWorking = Arrays.asList(autoaction.getNonWorkingDays().split(","));

        return allDays.stream()
                .filter(day -> !nonWorking.contains(day))
                .map(day -> day.substring(0, 3))
                .collect(Collectors.joining(","));
    }

    private boolean isPageMessage(String fromId) {
        return pageId.equals(fromId);
    }
    @Transactional
    public AutoactionResponseDTO updateAutoactionConfig(AutoactionConfigDTO configDTO) {

        Company company = companyRepository.findCompanyByName(configDTO.getCompanyName())
                .orElseGet(() -> {
            Company newCompany = new Company();
            newCompany.setName(configDTO.getCompanyName());
            newCompany.setWorkStartTime(LocalTime.parse(configDTO.getWorkStartTime()));
            newCompany.setWorkEndTime(LocalTime.parse(configDTO.getWorkEndTime()));
            Autoaction newAutoaction = new Autoaction();
            newAutoaction.setCompany(newCompany);
            newAutoaction.setPauseStart(LocalTime.parse(configDTO.getPauseStart()));
            newAutoaction.setPauseEnd(LocalTime.parse(configDTO.getPauseEnd()));
            newAutoaction.setNonWorkingDays(configDTO.getNonWorkingDays().toString());
                    return companyRepository.save(newCompany);
        });

        // Update work hours
        if (configDTO.getWorkStartTime() != null) {
            company.setWorkStartTime(LocalTime.parse(configDTO.getWorkStartTime()));
        }
        if (configDTO.getWorkEndTime() != null) {
            company.setWorkEndTime(LocalTime.parse(configDTO.getWorkEndTime()));
        }
        companyRepository.save(company);

        Autoaction autoaction = autoactionRepository.findByCompanyName(configDTO.getCompanyName())
                .orElseThrow(() -> new RuntimeException("Autoaction config not found for company: " + configDTO.getCompanyName()));

        if (configDTO.getNonWorkingDays() != null) {
            autoaction.setNonWorkingDays(String.join(",", configDTO.getNonWorkingDays()));
        }
        if (configDTO.getPauseStart() != null) {
            autoaction.setPauseStart(LocalTime.parse(configDTO.getPauseStart()));
        }
        if (configDTO.getPauseEnd() != null) {
            autoaction.setPauseEnd(LocalTime.parse(configDTO.getPauseEnd()));
        }
            autoaction.setMessageType(configDTO.getMessageType());
        autoaction.setMessage(configDTO.getMessage());
        autoactionRepository.save(autoaction);

        // Build response DTO
        AutoactionResponseDTO response = new AutoactionResponseDTO();
        response.setCompanyName(company.getName());
        response.setWorkStartTime(company.getWorkStartTime().toString());
        response.setWorkEndTime(company.getWorkEndTime().toString());
        response.setPauseStart(autoaction.getPauseStart().toString());
        response.setPauseEnd(autoaction.getPauseEnd().toString());
        response.setNonWorkingDays(List.of(autoaction.getNonWorkingDays().split(",")));

        return response;
    }


    public AutoactionConfigDTO getAutoactionConfig(String companyName) {
        Autoaction autoaction = autoactionRepository.findByCompanyName(companyName)
                .orElseThrow(() -> new RuntimeException("Configuration not found"));

        AutoactionConfigDTO dto = new AutoactionConfigDTO();
        dto.setCompanyName(companyName);
        dto.setNonWorkingDays(Arrays.asList(autoaction.getNonWorkingDays().split(",")));
        dto.setPauseStart(autoaction.getPauseStart().toString());
        dto.setPauseEnd(autoaction.getPauseEnd().toString());
        dto.setWorkStartTime(autoaction.getCompany().getWorkStartTime().toString());
        dto.setWorkEndTime(autoaction.getCompany().getWorkEndTime().toString());

        return dto;
    }
    public Autoaction getAutoaction(String companyName) {
        return autoactionRepository.findByCompanyName(companyName)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Configuration Autoaction introuvable pour l'entreprise '%s'", companyName)
                ));
    }


    public void markMessageSeenByMid(String senderId, String mid, LocalDateTime seenAt) {
        System.out.println("✅ [Service] Marking message with ID " + mid + " as seen by user " + senderId + " at " + seenAt);
        messageServiceImp.markMessageAsSeen(mid);  }

    public void markMessagesSeenUntil(String senderId, LocalDateTime seenUntil) {
        System.out.println("✅ [Service] Marking all messages from " + senderId + " seen until " + seenUntil);
        // Ex: messageRepository.markAllBefore(senderId, seenUntil);
    }

}