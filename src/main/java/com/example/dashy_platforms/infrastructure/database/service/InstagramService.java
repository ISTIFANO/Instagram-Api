package com.example.dashy_platforms.infrastructure.database.service;

import com.example.dashy_platforms.domaine.model.*;
import com.example.dashy_platforms.domaine.model.MediaAttachment.AttachementResponse;
import com.example.dashy_platforms.domaine.model.MediaAttachment.AttachmentRequest;
import com.example.dashy_platforms.domaine.model.MessageMedia.MessageFileRequest;
import com.example.dashy_platforms.domaine.model.MessageText.InstagramMessageRequest;
import com.example.dashy_platforms.domaine.model.MessageText.MessageDto;
import com.example.dashy_platforms.domaine.model.Template.Button_Template.InstagramButtonTemplateRequest;
import com.example.dashy_platforms.domaine.model.Template.QuickReplie.Quick_replies;
import com.example.dashy_platforms.domaine.model.Template.QuickReplie.Quick_replies_Request;
import com.example.dashy_platforms.domaine.service.IInstagramService;
import com.example.dashy_platforms.infrastructure.database.entities.MessageEntity;
import com.example.dashy_platforms.infrastructure.database.repositeries.MessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;
@Service
public class InstagramService implements IInstagramService {

    @Value("${instagram.graph.api.url}")
    private String graphApiUrl;

    @Value("${instagram.graph.access.token:default-value}")
    private String accessToken;

    @Value("${instagram.graph.page.id}")
    private String pageId;

    @Value("${instagram.graph.facebookpage.id}")
    private String facebookPageId;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public InstagramMessageResponse sendTextMessage(InstagramMessageRequest messageRequest) {
        try {

            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMessageType("text");
            messageEntity.setRecipientId(messageRequest.getRecipient().getId());
            messageEntity.setMessageContent(messageRequest.getMessage().getText());
            messageEntity.setStatus("PENDING");
            messageEntity.setCreatedAt(LocalDateTime.now());
            messageEntity.setSentAt(LocalDateTime.now());
            MessageEntity dbMessage = messageRepository.save(messageEntity);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("recipient", Map.of("id", messageRequest.getRecipient().getId()));
            requestBody.put("message", Map.of("text", messageRequest.getMessage().getText()));
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
                dbMessage.setMessageContent(messageRequest.getMessage().getText());
                messageRepository.save(dbMessage);

                return new InstagramMessageResponse(messageId, messageRequest.getRecipient().getId(), "SENT");
            } else {
                dbMessage.setStatus("FAILED");
                messageRepository.save(dbMessage);
                return new InstagramMessageResponse("FAILED", "Échec de l'envoi du message");
            }
        } catch (Exception e) {
            return new InstagramMessageResponse("ERROR", e.getMessage());
        }
    }


    @Override
    public InstagramMessageResponse sendGenericTemplate(String recipientId, InstagramTemplateRequest templateData) {
        try {
            MessageEntity dbMessage = messageRepository.save(new MessageEntity());

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

                dbMessage.setMessageType("template");
                dbMessage.setRecipientId(recipientId);
                dbMessage.setMessageContent("Generic template sent");
                dbMessage.setStatus("SENT");
                dbMessage.setSentAt(LocalDateTime.now());
                dbMessage.setCreatedAt(LocalDateTime.now());
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
    public InstagramMessageResponse sendButtonTemplate(String recipientId, InstagramButtonTemplateRequest templateRequest) {
        try {
            MessageEntity dbMessage = messageRepository.save(new MessageEntity());

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
                dbMessage.setMessageType("button_template");
                dbMessage.setRecipientId(recipientId);
                dbMessage.setMessageContent(templateRequest.getMessage().getAttachment().getPayload().getText());
                dbMessage.setStatus("SENT");
                dbMessage.setSentAt(LocalDateTime.now());
                dbMessage.setCreatedAt(LocalDateTime.now());
                messageRepository.save(dbMessage);

                return new InstagramMessageResponse(messageId, recipientId, "SENT");
            } else {
                dbMessage.setStatus("FAILED");
                messageRepository.save(dbMessage);

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
            MessageEntity dbMessage = messageRepository.save(new MessageEntity());

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

                dbMessage.setMessageType("quick_reply");
                dbMessage.setRecipientId(quickReplies.getRecipient().getId());
                dbMessage.setMessageContent("Quick replies message sent");
                dbMessage.setStatus("SENT");
                dbMessage.setSentAt(LocalDateTime.now());
                dbMessage.setCreatedAt(LocalDateTime.now());
                messageRepository.save(dbMessage);

                return new InstagramMessageResponse(messageId, quickReplies.getRecipient().getId(), "SENT");
            } else {
                dbMessage.setStatus("FAILED");
                messageRepository.save(dbMessage);

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


    @Override
    public void processPendingMessages() {

    }

    @Override
    public Set<UserListInfoResponse> listMessagedUsers() {
        Set<UserListInfoResponse> users = new HashSet<>();

        String conversationEndpoint = graphApiUrl + "/" + pageId + "/conversations?platform=instagram&access_token=" + accessToken;
        ResponseEntity<Map> response = restTemplate.getForEntity(conversationEndpoint, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            List<Map<String, Object>> conversations = (List<Map<String, Object>>) response.getBody().get("data");

            if (conversations != null) {
                for (Map<String, Object> conversation : conversations) {
                    String conversationId = (String) conversation.get("id");

                    String messageEndpoint = graphApiUrl + "/" + conversationId + "/messages?fields=from,to,message,created_time,id&access_token=" + accessToken;

                    ResponseEntity<Map> messageResponse = restTemplate.getForEntity(messageEndpoint, Map.class);
                    if (messageResponse.getStatusCode() == HttpStatus.OK && messageResponse.getBody() != null) {
                        List<Map<String, Object>> messages = (List<Map<String, Object>>) messageResponse.getBody().get("data");

                        if (messages != null) {
                            for (Map<String, Object> messageData : messages) {
                                Map<String, Object> from = (Map<String, Object>) messageData.get("from");


                                Map<String, Object> to = (Map<String, Object>) messageData.get("to");
                                if (to != null) {
                                    List<Map<String, Object>> toData = (List<Map<String, Object>>) to.get("data");
                                    for (Map<String, Object> user : toData) {
                                        users.add(new UserListInfoResponse((String) user.get("id"), (String) user.get("username")));
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return users;
    }


    @Override
    public AttachementResponse uploadAttachment(AttachmentRequest attachmentRequest) {

        String url = String.format("https://graph.facebook.com/v22.0/%s/message_attachments?access_token=%s", facebookPageId, accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        HttpEntity<AttachmentRequest> request = new HttpEntity<>(attachmentRequest, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<AttachementResponse> response = restTemplate.exchange(url, HttpMethod.POST, request, AttachementResponse.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return new AttachementResponse(response.getBody().getAttachmentId());
        } else {
            throw new RuntimeException("Failed to upload attachment");
        }
    }

    public InstagramMessageResponse sendImageMessage(MessageFileRequest messageRequest) {
        try {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMessageType("image");
            messageEntity.setRecipientId(messageRequest.getRecipient().getId());
            messageEntity.setStatus("PENDING");
            messageEntity.setCreatedAt(LocalDateTime.now());
            messageEntity.setSentAt(LocalDateTime.now());
            messageRepository.save(messageEntity);

            String url = "https://graph.facebook.com/v22.0/me/messages?access_token=" + accessToken;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<MessageFileRequest> request = new HttpEntity<>(messageRequest, headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String messageId = (String) response.getBody().get("message_id");

                messageEntity.setStatus("SENT");
                messageRepository.save(messageEntity);

                return new InstagramMessageResponse(messageId, messageRequest.getRecipient().getId(), "SENT");
            } else {
                messageEntity.setStatus("FAILED");
                messageRepository.save(messageEntity);
                return new InstagramMessageResponse("FAILED", "Échec de l'envoi du message");
            }
        } catch (Exception e) {
            return new InstagramMessageResponse("ERROR", e.getMessage());
        }
    }

}