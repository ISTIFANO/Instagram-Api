package com.example.dashy_platforms.infrastructure.database.service;

import com.example.dashy_platforms.domaine.model.BroadcastMessage.MessageTemplate;
import com.example.dashy_platforms.domaine.model.GenericTemplateData;
import com.example.dashy_platforms.domaine.model.InstagramMessageResponse;
import com.example.dashy_platforms.domaine.model.InstagramTemplateRequest;
import com.example.dashy_platforms.domaine.model.Template.Button_Template.InstagramButtonTemplateRequest;
import com.example.dashy_platforms.domaine.model.Template.QuickReplie.Quick_replies_Request;
import com.example.dashy_platforms.domaine.service.ITemplateService;
import com.example.dashy_platforms.infrastructure.database.entities.TemplateInstagram;
import com.example.dashy_platforms.infrastructure.database.repositeries.TemplateRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class TemplateService implements ITemplateService {
    @Value("${instagram.graph.api.url}")
    private String graphApiUrl;

    @Value("${instagram.graph.access.token:default-value}")
    private String accessToken;

    @Value("${instagram.graph.page.access.token:default-value}")
    private String pageaccessToken;

    @Value("${instagram.graph.page.id}")
    private String pageId;

    @Value("${instagram.graph.facebookpage.id}")
    private String facebookPageId;
    @Value("${application.host}")
    private String hosturl;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TemplateRepository templateRepository;

    public String generateUniqueTemplateCode() {
        String code;
        do {
            code = "TEMPLATE-" + UUID.randomUUID().toString();
        } while (templateRepository.existsByCode(code));

        return code;
    }
    public TemplateInstagram getTemplateById(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template not found with ID: " + id));
    }
    public TemplateInstagram getTemplateByCode(String code) {
        return templateRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Template not found with ID: " + code));
    }
@Override
    public InstagramMessageResponse sendGenericTemplate(String recipientId, InstagramTemplateRequest templateData) {
        try {
            TemplateInstagram dbMessage = templateRepository.save(new TemplateInstagram());

            String url = String.format("%s/v22.0/me/messages", graphApiUrl);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("recipient", Map.of("id", recipientId));
            requestBody.put("message", templateData.getMessage());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String messageId = (String) response.getBody().get("message_id");

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonTemplateContent = objectMapper.writeValueAsString(templateData);

                dbMessage.setLang("fr");
                dbMessage.setName(templateData.getMessage().getAttachment().getPayload().getElements().get(0).getSubtitle());
                dbMessage.setRecipientId(templateData.getRecipient().getId());
                // ajouter api pour recupirer le CompanyId
                dbMessage.setCompanyId("1946986879374368");
                dbMessage.setStatus("SENT");
                dbMessage.setTemplateType("generic");
                dbMessage.setCode(generateUniqueTemplateCode());
                dbMessage.setCreatedAt(LocalDateTime.now());
                dbMessage.setTemplateContent(jsonTemplateContent);
                templateRepository.save(dbMessage);

                return new InstagramMessageResponse(messageId, recipientId, "SENT");
            } else {
                dbMessage.setStatus("FAILED");
                templateRepository.save(dbMessage);

                InstagramMessageResponse errorResponse = new InstagramMessageResponse();
                errorResponse.setStatus("FAILED");
                errorResponse.setErrorMessage("Failed to send template");
                return errorResponse;
            }
        } catch (Exception e) {
            InstagramMessageResponse errorResponse = new InstagramMessageResponse();
            errorResponse.setStatus("ERROR");
            errorResponse.setErrorMessage(e.getMessage());
            return errorResponse;
        }
    }
@Override
    public InstagramMessageResponse sendButtonTemplate(String recipientId, InstagramButtonTemplateRequest templateRequest) {
        try {
            TemplateInstagram dbMessage = templateRepository.save(new TemplateInstagram());

            String url = String.format("%s/v22.0/me/messages", graphApiUrl);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("recipient", Map.of("id", recipientId));
            requestBody.put("message", templateRequest.getMessage());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String messageId = (String) response.getBody().get("message_id");
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonTemplateContent = objectMapper.writeValueAsString(templateRequest);
                dbMessage.setLang("fr");
                dbMessage.setName(templateRequest.getMessage().getAttachment().getPayload().getElements().get(0).getTitle());
                dbMessage.setRecipientId(templateRequest.getRecipient().getId());
                // ajouter api pour recupirer le CompanyId
                dbMessage.setCompanyId("1946986879374368");
                dbMessage.setStatus("SENT");
                dbMessage.setTemplateType("button");
                dbMessage.setCode(generateUniqueTemplateCode());
                dbMessage.setCreatedAt(LocalDateTime.now());
                dbMessage.setTemplateContent(jsonTemplateContent);
                templateRepository.save(dbMessage);

                return new InstagramMessageResponse(messageId, recipientId, "SENT");
            } else {
                dbMessage.setStatus("FAILED");
                templateRepository.save(dbMessage);

                InstagramMessageResponse errorResponse = new InstagramMessageResponse();
                errorResponse.setStatus("FAILED");
                errorResponse.setErrorMessage("Failed to send button template");
                return errorResponse;
            }
        } catch (Exception e) {
            InstagramMessageResponse errorResponse = new InstagramMessageResponse();
            errorResponse.setStatus("ERROR");
            errorResponse.setErrorMessage(e.getMessage());
            return errorResponse;
        }
    }

    @Override
    public InstagramMessageResponse sendQuick_repliesTemplate(Quick_replies_Request quickReplies) {
        try {
            TemplateInstagram dbMessage = templateRepository.save(new TemplateInstagram());

            String url = String.format("%s/v18.0/me/messages", graphApiUrl);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("recipient", Map.of("id", quickReplies.getRecipient().getId()));
            requestBody.put("message", quickReplies.getMessage());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String messageId = (String) response.getBody().get("message_id");
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonTemplateContent = objectMapper.writeValueAsString(quickReplies);
                dbMessage.setName(quickReplies.getMessage().getQuick_replies().get(0).getTitle());
                dbMessage.setRecipientId(quickReplies.getRecipient().getId());
                // ajouter api pour recupirer le CompanyId

                dbMessage.setCompanyId("1946986879374368");
                dbMessage.setStatus("SENT");
                dbMessage.setLang("fr");
                dbMessage.setTemplateType("quick_replies");
                dbMessage.setCode(generateUniqueTemplateCode());
                dbMessage.setCreatedAt(LocalDateTime.now());
                dbMessage.setTemplateContent(jsonTemplateContent);
                templateRepository.save(dbMessage);

                return new InstagramMessageResponse(messageId, quickReplies.getRecipient().getId(), "SENT");
            } else {
                dbMessage.setStatus("FAILED");
                templateRepository.save(dbMessage);

                InstagramMessageResponse errorResponse = new InstagramMessageResponse();
                errorResponse.setStatus("FAILED");
                errorResponse.setErrorMessage("Failed to send quick replies message");
                return errorResponse;
            }
        } catch (Exception e) {
            InstagramMessageResponse errorResponse = new InstagramMessageResponse();
            errorResponse.setStatus("ERROR");
            errorResponse.setErrorMessage(e.getMessage());
            return errorResponse;
        }
    }

    public InstagramTemplateRequest getTemplateDataByCode(String code) {
        TemplateInstagram template = templateRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException(" Template not found with code :  " + code));
        String jsonContent = template.getTemplateContent();
        try {
            return objectMapper.readValue(jsonContent, InstagramTemplateRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse template content", e);
        }
    }
}
