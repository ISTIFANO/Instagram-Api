package com.example.dashy_platforms.infrastructure.database.service;

import com.example.dashy_platforms.domaine.model.*;
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

            Map<String, Object> messageMap = new HashMap<>();
            messageMap.put("text", message);

            Map<String, Object> recipientMap = new HashMap<>();
            recipientMap.put("id", recipientId);

            Map<String, Object> body = new HashMap<>();
            body.put("recipient", recipientMap);
            body.put("message", messageMap);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ObjectMapper objectMapper = new ObjectMapper();
            String jsonBody = objectMapper.writeValueAsString(body);

            RestTemplate restTemplate = new RestTemplate();


            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {

                String message_id = (String) response.getBody().get("message_id");
                dbMessage.setMessageType("text");
                dbMessage.setRecipientId(recipientId);
                dbMessage.setMessageContent(message);
                dbMessage.setStatus("SENT");
                dbMessage.setSentAt(LocalDateTime.now());
                dbMessage.setCreatedAt(LocalDateTime.now());
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
    public InstagramMessageResponse sendGenericTemplate(String recipientId, InstagramTemplateRequest templateData) {
        System.out.println("Inside sendGenericTemplate");
        System.out.println(templateData.getMessage().getAttachment().getType());
        try {
            MessageEntity messageEntity = new MessageEntity();
            MessageEntity dbMessage = messageRepository.save(messageEntity);

            String url = String.format("%s/v22.0/me/messages", graphApiUrl);

            List<Map<String, Object>> elementsList = new ArrayList<>();

            if (templateData.getMessage().getAttachment() != null &&
                    templateData.getMessage().getAttachment().getPayload() != null &&
                    templateData.getMessage().getAttachment().getPayload().getElements() != null) {

                for (ElementModel elem : templateData.getMessage().getAttachment().getPayload().getElements()) {
                    Map<String, Object> element = new HashMap<>();
                    element.put("title", elem.getTitle());
                    element.put("image_url", elem.getImageUrl());
                    element.put("subtitle", elem.getSubtitle());

                    if (elem.getDefaultAction() != null) {
                        Map<String, Object> defaultAction = new HashMap<>();
                        defaultAction.put("type", elem.getDefaultAction().getType());
                        defaultAction.put("url", elem.getDefaultAction().getUrl());
                        element.put("default_action", defaultAction);
                    }

                    if (elem.getButtons() != null && !elem.getButtons().isEmpty()) {
                        List<Map<String, Object>> buttons = new ArrayList<>();
                        for (TemplateButton button : elem.getButtons()) {
                            Map<String, Object> buttonMap = new HashMap<>();
                            buttonMap.put("type", button.getType());
                            buttonMap.put("title", button.getTitle());
                            if ("web_url".equals(button.getType())) {
                                buttonMap.put("url", button.getUrl());
                            } else if ("postback".equals(button.getType())) {
                                buttonMap.put("payload", button.getPayload());
                            }
                            buttons.add(buttonMap);
                        }
                        element.put("buttons", buttons);
                    }

                    elementsList.add(element);
                }
            }

            Map<String, Object> payload = new HashMap<>();
            payload.put("template_type", "generic");
            payload.put("elements", elementsList);

            Map<String, Object> attachment = new HashMap<>();
            attachment.put("type", "template");
            attachment.put("payload", payload);

            Map<String, Object> message = new HashMap<>();
            message.put("attachment", attachment);

            Map<String, Object> recipient = new HashMap<>();
            recipient.put("id", recipientId);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("recipient", recipient);
            requestBody.put("message", message);

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
        System.out.println("Inside sendButtonTemplate");

        try {
            MessageEntity messageEntity = new MessageEntity();
            MessageEntity dbMessage = messageRepository.save(messageEntity);

            String url = String.format("%s/v22.0/me/messages", graphApiUrl);

            List<Map<String, Object>> buttonList = new ArrayList<>();
            if (templateRequest.getMessage() != null &&
                    templateRequest.getMessage().getAttachment() != null &&
                    templateRequest.getMessage().getAttachment().getPayload() != null &&
                    templateRequest.getMessage().getAttachment().getPayload().getElements() != null) {

                for (TemplateButton button : templateRequest.getMessage().getAttachment().getPayload().getElements()) {
                    Map<String, Object> buttonMap = new HashMap<>();
                    buttonMap.put("type", button.getType());
                    buttonMap.put("title", button.getTitle());

                    if ("web_url".equals(button.getType())) {
                        buttonMap.put("url", button.getUrl());
                    } else if ("postback".equals(button.getType())) {
                        buttonMap.put("payload", button.getPayload());
                    }

                    buttonList.add(buttonMap);
                }
            }
            Map<String, Object> payload = new HashMap<>();
            payload.put("template_type", "button");
            payload.put("text", templateRequest.getMessage().getAttachment().getPayload().getText());
            payload.put("buttons", buttonList);

            Map<String, Object> attachment = new HashMap<>();
            attachment.put("type", "template");
            attachment.put("payload", payload);
            Map<String, Object> message = new HashMap<>();
            message.put("attachment", attachment);

            Map<String, Object> recipient = new HashMap<>();
            recipient.put("id", recipientId);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("recipient", recipient);
            requestBody.put("message", message);

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
            MessageEntity messageEntity = new MessageEntity();
            MessageEntity dbMessage = messageRepository.save(messageEntity);

            String url = String.format("%s/v18.0/me/messages", graphApiUrl);

            List<Map<String, Object>> quickRepliesList = new ArrayList<>();

            if (quickReplies.getMessage() != null && quickReplies.getMessage().getQuick_replies() != null) {
                for (Quick_replies reply : quickReplies.getMessage().getQuick_replies()) {
                    Map<String, Object> replyMap = new HashMap<>();
                    replyMap.put("content_type", reply.getContentType());
                    replyMap.put("title", reply.getTitle());
                    replyMap.put("payload", reply.getPayload());
                    quickRepliesList.add(replyMap);
                }
            }

            Map<String, Object> message = new HashMap<>();
            message.put("text", quickReplies.getMessage().getText());
            message.put("quick_replies", quickRepliesList);

            Map<String, Object> recipient = new HashMap<>();
            recipient.put("id", quickReplies.getRecipient().getId());

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("recipient", recipient);
            requestBody.put("message", message);

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
    public InstagramMessageResponse sendMessage(InstagramTemplateRequest request) {


        if ("TEMPLATE".equals(request) && request.getMessage() != null) {
            return sendGenericTemplate(request.getRecipient().getId(), request);
        } else {
            return sendTextMessage(request.getRecipient().getId(), String.valueOf(request));
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



}
