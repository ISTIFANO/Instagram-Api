package com.example.dashy_platforms.infrastructure.database.service;

import com.example.dashy_platforms.domaine.model.*;
import com.example.dashy_platforms.domaine.service.IInstagramService;
import com.example.dashy_platforms.infrastructure.database.entities.MessageEntity;
import com.example.dashy_platforms.infrastructure.database.repositeries.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InstagramService implements IInstagramService {

    @Value("${instagram.graph.api.url}")
    private String graphApiUrl;

    @Value("${instagram.graph.access.token:default-value}")
    private String accessToken;

    @Value("${instagram.graph.page.id}")
    private String pageId;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public InstagramMessageResponse sendTextMessage(String recipientId, String message) {
        try {

            MessageEntity messageEntity = new MessageEntity();
            MessageEntity dbMessage = messageRepository.save(messageEntity);

            String url = String.format("%s/v22.0/me/messages", graphApiUrl);
            Map<String, Object> body = new HashMap<>();
            body.put("message", message);
            body.put("recipient_id", recipientId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {

                String message_id = (String) response.getBody().get("message_id");
                dbMessage.setStatus("SENT");
                dbMessage.setSentAt(LocalDateTime.now());
                messageRepository.save(dbMessage);

                return new InstagramMessageResponse(message_id, recipientId, "SENT");
            } else {
                dbMessage.setStatus("FAILED");
                messageRepository.save(dbMessage);
                InstagramMessageResponse errorResponse = new InstagramMessageResponse();

                errorResponse.setStatus("FAILED");
                errorResponse.setErrorMessage("Failed to send message");
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
    public InstagramMessageResponse sendGenericTemplate(String recipientId, GenericTemplateData templateData) {
        try {

            MessageEntity messageEntity = new MessageEntity();
            MessageEntity dbMessage = messageRepository.save(messageEntity);

            String url = String.format("%s/v22.0/me/message", graphApiUrl);
            Map<String, Object> body = new HashMap<>();
            body.put("title", templateData.getTitle());
            body.put("img_url", templateData.getImageUrl());
            body.put("subtitle", templateData.getSubtitle());
            if (templateData.getDefaultAction() != null) {
                Map<Object, Object> defaultAction = new HashMap<>();
                defaultAction.put("type", templateData.getDefaultAction().getType());
                defaultAction.put("url", templateData.getDefaultAction().getUrl());
                defaultAction.put("default_action", defaultAction);
            }
            if (templateData.getButtons() != null && !templateData.getButtons().isEmpty()) {
                List<Map<String, Object>> buttons = new ArrayList<>();

                for (TemplateButton button : templateData.getButtons()) {
                    Map<String, Object> buttonAction = new HashMap<>();
                    buttonAction.put("type", button.getType());
                    buttonAction.put("title", button.getTitle());

                    if ("web_url".equals(button.getType())) {
                        buttonAction.put("url", button.getUrl());
                    } else if ("postback".equals(button.getType())) {
                        buttonAction.put("payload", button.getPayload());
                    }

                    buttons.add(buttonAction);
                }
                body.put("buttons", buttons);

            }
            Map<String, Object> templatepaylaod = new HashMap<>();
            templatepaylaod.put("template_type", "generic");
            templatepaylaod.put("elements", body);

            Map<String, Object> attachment = new HashMap<>();
            attachment.put("type", "template");
            attachment.put("payload", templatepaylaod);

            Map<String, Object> message = new HashMap<>();
            message.put("attachment", attachment);

            Map<String, Object> payload = new HashMap<>();
            payload.put("recipient_id", Map.of("id", recipientId));
            payload.put("message", templatepaylaod);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String messageId = (String) response.getBody().get("message_id");

                dbMessage.setStatus("SENT");
                dbMessage.setSentAt(LocalDateTime.now());
                messageRepository.save(dbMessage);

                return new InstagramMessageResponse(messageId, recipientId, "SENT");
            } else {
                dbMessage.setStatus("FAILED");
                messageRepository.save(dbMessage);

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
    public InstagramMessageResponse sendMessage(InstagramMessageRequest request) {
        if ("TEMPLATE".equals(request.getMessageType()) && request.getTemplateData() != null) {
            return sendGenericTemplate(request.getRecipientId(), request.getTemplateData());
        } else {
            return sendTextMessage(request.getRecipientId(), request.getMessage());
        }
    }
    @Override
    public void processPendingMessages() {

    }

}
