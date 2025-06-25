package com.example.dashy_platforms.infrastructure.database.service;

import ch.qos.logback.classic.Logger;
import com.example.dashy_platforms.domaine.helper.JsoonFormat;
import com.example.dashy_platforms.domaine.model.Autoaction.AutoactionConfigDTO;
import com.example.dashy_platforms.domaine.model.InstagramMessageResponse;
import com.example.dashy_platforms.infrastructure.database.entities.Autoaction;
import com.example.dashy_platforms.infrastructure.database.entities.Company;
import com.example.dashy_platforms.infrastructure.database.repositeries.AutoactionRepository;
import com.example.dashy_platforms.infrastructure.database.repositeries.CompanyRepository;
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

    @Value("${instagram.graph.api.url}")
    private String graphApiUrl;

    @Value("${instagram.graph.access.token:default-value}")
    private String accessToken;
    private final RestTemplate restTemplate;

    @Value("${instagram.graph.access.token}")
    private String pageAccessToken;
public AutoReplyService(AutoactionRepository autoactionRepository, CompanyRepository companyRepository, InstagramService instagramService) {
    this.autoactionRepository = autoactionRepository;
    this.instagramService = instagramService;
    this.companyRepository = companyRepository;
    this.restTemplate = new RestTemplate();
}
    public void checkAndReply(String senderId, String message, LocalDateTime receivedAt) {
        Autoaction autoaction = autoactionRepository.findByCompanyName("ISTIFANO")
                .orElseThrow(() -> new RuntimeException("Aucune configuration trouvée"));
System.out.println(autoaction);
        if (shouldReply(autoaction, receivedAt)) {
            String replyMessage = buildReplyMessage(autoaction, receivedAt);
            System.out.println(replyMessage);
            System.out.println(senderId);
            sendReply(senderId, replyMessage);
        }
    }

    private boolean shouldReply(Autoaction autoaction, LocalDateTime receivedAt) {
        List<String> nonWorkingDays = Arrays.asList(autoaction.getNonWorkingDays().split(","));
        DayOfWeek currentDay = receivedAt.getDayOfWeek();
        if (nonWorkingDays.contains(currentDay.name())) {
            return true;
        }

        LocalTime currentTime = receivedAt.toLocalTime();

        // Vérifier les heures de pause
        if (isDuringPause(autoaction, currentTime)) {
            return true;
        }

        // Vérifier les heures hors travail
        Company company = autoaction.getCompany();
        return currentTime.isBefore(company.getWorkStartTime()) ||
                currentTime.isAfter(company.getWorkEndTime());
    }

    private String buildReplyMessage(Autoaction autoaction, LocalDateTime receivedAt) {
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
        List<String> allDays = Arrays.asList("MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY","SUNDAY");
        List<String> nonWorking = Arrays.asList(autoaction.getNonWorkingDays().split(","));

        return allDays.stream()
                .filter(day -> !nonWorking.contains(day))
                .map(day -> day.substring(0, 3))
                .collect(Collectors.joining(","));
    }
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )

    private void sendReply(String recipientId, String message) {
        final String url = String.format("%s/v22.0/me/messages", graphApiUrl);

        // Create request payload
        Map<String, Object> request = Map.of(
                "recipient", Map.of("id", recipientId),
                "message", Map.of("text", message),
                "messaging_type", "RESPONSE"
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(request, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    httpEntity,
                    Map.class
            );

            // Validate response
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String messageId = (String) response.getBody().get("message_id");
            } else {

                throw new RuntimeException("Failed to send message: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            throw new RuntimeException("Error sending message to Instagram API", e);
        }
    }

    public Autoaction updateAutoactionConfig(AutoactionConfigDTO configDTO) {
        Company company = companyRepository.findByName(configDTO.getCompanyName())
                .orElseGet(() -> {
                    Company newCompany = new Company();
                    newCompany.setName(configDTO.getCompanyName());
                    return companyRepository.save(newCompany);
                });

        // Mettre à jour les heures de travail si fournies
        if (configDTO.getWorkStartTime() != null) {
            company.setWorkStartTime(LocalTime.parse(configDTO.getWorkStartTime()));
        }
        if (configDTO.getWorkEndTime() != null) {
            company.setWorkEndTime(LocalTime.parse(configDTO.getWorkEndTime()));
        }
        companyRepository.save(company);

        Autoaction autoaction = autoactionRepository.findByCompanyName(configDTO.getCompanyName())
                .orElseGet(() -> {
                    Autoaction newAutoaction = new Autoaction();
                    newAutoaction.setCompany(company);
                    return newAutoaction;
                });

        if (configDTO.getNonWorkingDays() != null) {
            autoaction.setNonWorkingDays(String.join(",", configDTO.getNonWorkingDays()));
        }
        if (configDTO.getPauseStart() != null) {
            autoaction.setPauseStart(LocalTime.parse(configDTO.getPauseStart()));
        }
        if (configDTO.getPauseEnd() != null) {
            autoaction.setPauseEnd(LocalTime.parse(configDTO.getPauseEnd()));
        }

        return autoactionRepository.save(autoaction);
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
}