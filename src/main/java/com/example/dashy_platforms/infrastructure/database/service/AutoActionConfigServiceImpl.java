package com.example.dashy_platforms.infrastructure.database.service;

import com.example.dashy_platforms.domaine.model.InstagramMessageResponse;
import com.example.dashy_platforms.domaine.model.MessageText.InstagramMessageRequest;
import com.example.dashy_platforms.domaine.service.AutoActionConfigService;
import com.example.dashy_platforms.infrastructure.database.entities.AutoActionConfigEntity;
import com.example.dashy_platforms.infrastructure.database.entities.Company;
import com.example.dashy_platforms.infrastructure.database.entities.MessageEntity;
import com.example.dashy_platforms.infrastructure.database.repositeries.AutoActionConfigRepository;
import com.example.dashy_platforms.infrastructure.database.repositeries.MessageRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@Slf4j

public class AutoActionConfigServiceImpl{
private AutoActionConfigRepository autoActionConfigRepository;
    @Value("${instagram.graph.api.url}")
    private String graphApiUrl;

    @Value("${instagram.graph.access.token:default-value}")
    private String accessToken;
private CompanyService companyService;
    private MessageRepository messageRepository;
    public AutoActionConfigServiceImpl(AutoActionConfigRepository autoActionConfigRepository, MessageRepository messageRepository, CompanyService companyService) {
        this.autoActionConfigRepository = autoActionConfigRepository;
        this.messageRepository = messageRepository;
        this.companyService = companyService;
    }


    public List<AutoActionConfigEntity> getActionsByCompany(Company company) {
        return autoActionConfigRepository.findByCompany(company);
    }

    public InstagramMessageResponse sendTextMessage(String recipientId) {
        try {
            Company company = companyService.getCompanyByname("DASHY");

        AutoActionConfigEntity msg = autoActionConfigRepository.findByCompany_IdAndCompany_Id(2,11);

            InstagramMessageRequest request1 = new InstagramMessageRequest();
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMessageType("text");
            messageEntity.setRecipientId(recipientId);
            messageEntity.setMessageContent(msg.getMessage());
            messageEntity.setStatus("PENDING");
            messageEntity.setCreatedAt(LocalDateTime.now());
            messageEntity.setSentAt(LocalDateTime.now());
            MessageEntity dbMessage = messageRepository.save(messageEntity);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("recipient", Map.of("id", recipientId));
            requestBody.put("message", Map.of("text", msg));
            String url = String.format("%s/v22.0/me/messages", graphApiUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String messageId = (String) response.getBody().get("message_id");

                dbMessage.setStatus("SENT");
                dbMessage.setMessageContent(msg.getMessage());
                messageRepository.save(dbMessage);

                return new InstagramMessageResponse(messageId,recipientId, "SENT");
            } else {
                dbMessage.setStatus("FAILED");
                messageRepository.save(dbMessage);
                return new InstagramMessageResponse("FAILED", "Échec de l'envoi du message");
            }
        } catch (Exception e) {
            return new InstagramMessageResponse("ERROR", e.getMessage());
        }
    }

}
