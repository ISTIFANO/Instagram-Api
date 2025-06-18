package com.example.dashy_platforms.infrastructure.database.service;

import com.example.dashy_platforms.domaine.helper.JsoonFormat;
import com.example.dashy_platforms.domaine.model.*;
import com.example.dashy_platforms.domaine.model.BroadcastMessage.*;
import com.example.dashy_platforms.domaine.model.BroadcastMessage.Message;
import com.example.dashy_platforms.domaine.model.MediaAttachment.AttachementResponse;
import com.example.dashy_platforms.domaine.model.MediaAttachment.AttachmentDto;
import com.example.dashy_platforms.domaine.model.MediaAttachment.AttachmentRequest;
import com.example.dashy_platforms.domaine.model.MessageMedia.InstagramMediaMessageRequest;
import com.example.dashy_platforms.domaine.model.MessageMedia.MessageFileRequest;
import com.example.dashy_platforms.domaine.model.MessageSticker.InstagramStickerRequest;
import com.example.dashy_platforms.domaine.model.MessageText.InstagramMessageRequest;
import com.example.dashy_platforms.domaine.model.Reaction.ReactionContainer;
import com.example.dashy_platforms.domaine.model.Template.Button_Template.InstagramButtonTemplateRequest;
import com.example.dashy_platforms.domaine.model.Template.QuickReplie.Quick_replies_Request;
import com.example.dashy_platforms.domaine.service.IInstagramService;
import com.example.dashy_platforms.infrastructure.database.entities.MessageEntity;
import com.example.dashy_platforms.infrastructure.database.repositeries.MessageRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
@Slf4j
@Service
public class InstagramService implements IInstagramService {

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
    public InstagramMessageResponse sendGenericTemplate(String recipientId, InstagramTemplateRequest templateData , String attachmentId) {
        try {
            MessageEntity dbMessage = messageRepository.save(new MessageEntity());

            if (templateData != null &&
                    templateData.getMessage() != null &&
                    templateData.getMessage().getAttachment() != null &&
                    templateData.getMessage().getAttachment().getPayload() != null &&
                    templateData.getMessage().getAttachment().getPayload().getElements() != null) {

                for (ElementModel element : templateData.getMessage().getAttachment().getPayload().getElements()) {
                    element.setAttachment_id(attachmentId);
                }
            }
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


    public InstagramMessageResponse sendImageMessage(AttachmentRequest messageRequest) {
        try {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMessageType("image");
            messageEntity.setSentAt(LocalDateTime.now());
            messageEntity.setRecipientId(messageRequest.getRecipient().getId());
            messageEntity.setStatus("PENDING");
            messageEntity.setCreatedAt(LocalDateTime.now());
            messageRepository.save(messageEntity);
            AttachmentDto AttachmentDto = new AttachmentDto();
            AttachmentDto.setMessage(messageRequest.getMessage());
            AttachmentDto.setPlatform(messageRequest.getPlatform());


            AttachementResponse attachement = this.uploadAttachment(AttachmentDto);
            MessageFileRequest messageFileRequest = this.UploadFile(attachement, messageRequest.getRecipient());


            String url = "https://graph.facebook.com/v22.0/me/messages?access_token=" + accessToken;

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<MessageFileRequest> request = new HttpEntity<>(messageFileRequest, headers);

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

    @Override
    public AttachementResponse uploadAttachment(AttachmentDto attachmentRequest) {

        String url = String.format("https://graph.facebook.com/v22.0/%s/message_attachments?access_token=%s", facebookPageId, pageaccessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JsoonFormat jsoonFormat = new JsoonFormat();
        headers.set("Authorization", "OAuth2 " + pageaccessToken);
        HttpEntity<AttachmentDto> request = new HttpEntity<>(attachmentRequest, headers);
        jsoonFormat.printJson(request);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<AttachementResponse> response = restTemplate.exchange(url, HttpMethod.POST, request, AttachementResponse.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            return new AttachementResponse(response.getBody().getAttachmentId());
        } else {
            throw new RuntimeException("Failed to upload attachment");
        }
    }

    public MessageFileRequest UploadFile(AttachementResponse attachmentResponse, Recipient recipient) {
        com.example.dashy_platforms.domaine.model.MessageMedia.Payload payload = new com.example.dashy_platforms.domaine.model.MessageMedia.Payload();
        payload.setAttachmentId(String.valueOf(attachmentResponse.getAttachmentId()));
        com.example.dashy_platforms.domaine.model.MessageMedia.Attachment attachment = new com.example.dashy_platforms.domaine.model.MessageMedia.Attachment();
        attachment.setType("image");
        attachment.setPayload(payload);
        com.example.dashy_platforms.domaine.model.MessageMedia.Message message = new com.example.dashy_platforms.domaine.model.MessageMedia.Message();
        message.setAttachment(attachment);
        MessageFileRequest fileRequestDto = new MessageFileRequest();
        fileRequestDto.setRecipient(recipient);
        fileRequestDto.setPlatform("instagram");
        fileRequestDto.setMessaging_type("RESPONSE");
        fileRequestDto.setMessage(message);
        return fileRequestDto;
    }

    public InstagramMessageResponse sendReaction(ReactionContainer request) {
        try {
            String url = String.format("%s/v23.0/me/messages", graphApiUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<ReactionContainer> httpEntity = new HttpEntity<>(request, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, httpEntity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String messageId = (String) response.getBody().get("message_id");
                return new InstagramMessageResponse(messageId, request.getRecipient().getId(), "SENT");
            } else {
                return new InstagramMessageResponse("FAILED", "Échec de l'envoi de la réaction");
            }

        } catch (Exception e) {
            return new InstagramMessageResponse("ERROR", e.getMessage());
        }
    }

    public InstagramMessageResponse sendSticker(InstagramStickerRequest request) {
        try {
            MessageEntity messageEntity = new MessageEntity();
            messageEntity.setMessageType("sticker");
            messageEntity.setRecipientId(request.getRecipient().getId());
            messageEntity.setMessageContent("Sticker: " + request.getMessage().getAttachment().getType());
            messageEntity.setStatus("PENDING");
            messageEntity.setCreatedAt(LocalDateTime.now());
            messageEntity.setSentAt(LocalDateTime.now());
            MessageEntity dbMessage = messageRepository.save(messageEntity);

            String url = String.format("%s/v23.0/me/messages", graphApiUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            HttpEntity<InstagramStickerRequest> entity = new HttpEntity<>(request, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String messageId = (String) response.getBody().get("message_id");

                dbMessage.setStatus("SENT");
                messageRepository.save(dbMessage);

                return new InstagramMessageResponse(messageId, request.getRecipient().getId(), "SENT");
            } else {
                dbMessage.setStatus("FAILED");
                messageRepository.save(dbMessage);

                return new InstagramMessageResponse("FAILED", "Échec de l'envoi du sticker");
            }

        } catch (Exception e) {
            return new InstagramMessageResponse("ERROR", e.getMessage());
        }
    }
    public String uploadMediaAndGetAttachmentId(MultipartFile file) throws JsonProcessingException {
        String url = String.format(
                "https://graph.facebook.com/v22.0/%s/message_attachments?access_token=%s",
                facebookPageId,
                pageaccessToken
        );

        String mediaType = getMediaType(file.getContentType());
        Map<String, Object> payloadMap = new HashMap<>();
        payloadMap.put("is_reusable", true);

        Map<String, Object> attachmentMap = new HashMap<>();
        attachmentMap.put("type", mediaType);
        attachmentMap.put("payload", payloadMap);

        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("attachment", attachmentMap);

        ObjectMapper mapper = new ObjectMapper();
        String messageJson = mapper.writeValueAsString(messageMap);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("platform", "instagram");
        body.add("filedata", file.getResource());
        body.add("message", messageJson);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.setBearerAuth(pageaccessToken);

        try {
            ResponseEntity<JsonNode> resp = new RestTemplate()
                    .postForEntity(url, new HttpEntity<>(body, headers), JsonNode.class);

            if (resp.getStatusCode() == HttpStatus.OK && resp.getBody() != null) {
                JsonNode attachmentIdNode = resp.getBody().get("attachment_id");
                if (attachmentIdNode != null) {
                    return attachmentIdNode.asText();
                }
            }

            String errorMsg = String.format("Upload failed - Status: %s, Body: %s",
                    resp.getStatusCode(), resp.getBody());
            throw new RuntimeException(errorMsg);

        } catch (Exception e) {
            throw new RuntimeException("Media upload failed: " + e.getMessage(), e);
        }
    }

    public InstagramMessageResponse sendMediaByAttachmentId(String recipientId, String attachmentId, String mediaType) {
        String url = String.format("%s/v22.0/me/messages", graphApiUrl);

        System.out.println(recipientId);
        System.out.println(attachmentId);
        System.out.println(mediaType);

        Map<String, Object> payload = Map.of(
                "attachment_id", attachmentId
        );
        Map<String, Object> attachment = Map.of(
                "type", mediaType,
                "payload", payload
        );
        Map<String, Object> message = Map.of(
                "attachment", attachment
        );
        Map<String, Object> body = Map.of(
                "recipient", Map.of("id", recipientId),
                "message", message
        );
        JsoonFormat jsoonFormat = new JsoonFormat();
        jsoonFormat.printJson(body);
        MessageEntity msg = new MessageEntity();
        msg.setRecipientId(recipientId);
        msg.setMessageType(mediaType);
        msg.setMessageContent("attachment_id:" + attachmentId);
        msg.setStatus("PENDING");
        LocalDateTime now = LocalDateTime.now();
        msg.setCreatedAt(now);
        msg.setSentAt(now);

        try {
            messageRepository.save(msg);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save message entity: " + e.getMessage(), e);
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(accessToken);

            ObjectMapper objectMapper = new ObjectMapper();

            String jsonBody = objectMapper.writeValueAsString(body);
            HttpEntity<String> req = new HttpEntity<>(jsonBody, headers);

            RestTemplate rest = new RestTemplate();
            ResponseEntity<JsonNode> resp = rest.postForEntity(url, req, JsonNode.class);

            if (resp.getStatusCode() == HttpStatus.OK && resp.getBody() != null) {
                JsonNode messageIdNode = resp.getBody().get("message_id");
                if (messageIdNode != null) {
                    String messageId = messageIdNode.asText();
                    msg.setStatus("SENT");
                    msg.setMessageContent(attachmentId);
                    messageRepository.save(msg);

                    return new InstagramMessageResponse(messageId, recipientId, "SENT");
                }
            }
            msg.setStatus("FAILED");
            messageRepository.save(msg);

            String errorMsg = String.format("API call failed - Status: %s, Body: %s",
                    resp.getStatusCode(), resp.getBody());
            return new InstagramMessageResponse("FAILED", errorMsg);

        } catch (Exception e) {
            msg.setStatus("ERROR");
            try {
                messageRepository.save(msg);
            } catch (Exception saveEx) {
                System.err.println("Failed to update message status: " + saveEx.getMessage());
            }

            return new InstagramMessageResponse("ERROR", "Send failed: " + e.getMessage());
        }
    }
    private String getMediaType(String contentType) {
        if (contentType == null) {
            throw new IllegalArgumentException("Content type is null");
        }

        if (contentType.startsWith("image/")) return "image";
        if (contentType.startsWith("audio/")) return "audio";
        if (contentType.startsWith("video/")) return "video";

        throw new IllegalArgumentException("Unsupported media type: " + contentType);
    }
    public List<String> getActiveConversations() {
        try {
            String url = String.format("%s/%s/conversations?platform=instagram&access_token=%s",
                   graphApiUrl, pageId, accessToken);

            ConversationResponse response = restTemplate.getForObject(url, ConversationResponse.class);

            if (response != null && response.getData() != null) {
                return response.getData().stream()
                        .map(Conversation::getId)
                        .collect(Collectors.toList());
            }

            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching conversations: ", e);
            return Collections.emptyList();
        }
    }

    public List<Message> getConversationMessages(String conversationId) {
        try {
            String url = String.format("%s/%s/messages?fields=from,to,message,created_time,id&access_token=%s",
                    graphApiUrl, conversationId, accessToken);

            MessagesResponse response = restTemplate.getForObject(url, MessagesResponse.class);

            if (response != null && response.getData() != null) {
                return response.getData();
            }

            return Collections.emptyList();
        } catch (Exception e) {
            log.error("Error fetching messages for conversation {}: ", conversationId, e);
            return Collections.emptyList();
        }
    }

    public boolean isConversationActive(List<Message> messages) {
        if (messages.isEmpty()) {
            return false;
        }

        Message lastMessage = messages.get(0);
        try {
            String fixedDate = lastMessage.getCreatedTime().replaceAll("(\\+\\d{2})(\\d{2})$", "$1:$2");
            Instant lastMessageTime = Instant.parse(fixedDate);
            Instant now = Instant.now();
            return Duration.between(lastMessageTime, now).toHours() < 24;
        } catch (Exception e) {
            log.error("Error parsing message time: {}", lastMessage.getCreatedTime());
            return false;
        }
    }

    public Set<String> getActiveUsers() {
        List<String> conversations = getActiveConversations();
        Set<String> activeUsers = new HashSet<>();

        for (String conversationId : conversations) {
            List<Message> messages = getConversationMessages(conversationId);

            if (isConversationActive(messages)) {
                messages.stream()
                        .filter(msg -> msg.getFrom() != null && !isPageMessage(msg.getFrom().getId()))
                        .forEach(msg -> activeUsers.add(msg.getFrom().getId()));
            }
        }

        log.info("Found {} active users", activeUsers.size());
        return activeUsers;
    }
    private boolean isPageMessage(String fromId) {
        return pageId.equals(fromId);
    }
    public SendMessageResponse sendTextMessage(String userId, String messageText) {
        try {
            String url = String.format("%s/%s/messages?access_token=%s",
                    graphApiUrl, pageId, accessToken);

            SendMessageRequest request = new SendMessageRequest();
            SendMessageRequest.Recipient recipient = new SendMessageRequest.Recipient();
            recipient.setId(userId);
            request.setRecipient(recipient);

            SendMessageRequest.MessageContent messageContent = new SendMessageRequest.MessageContent();
            messageContent.setText(messageText);
            request.setMessage(messageContent);

            return sendMessageRequest(url, request, userId);
        } catch (Exception e) {
            log.error("Error sending text message to user {}: ", userId, e);
            return null;
        }
    }
    public SendMessageResponse sendMediaMessage(String userId, String attachmentId, String mediaType, String caption) {
        try {
            String url = String.format("%s/%s/messages?access_token=%s",
                    graphApiUrl, pageId, accessToken);

            SendMessageRequest request = new SendMessageRequest();
            SendMessageRequest.Recipient recipient = new SendMessageRequest.Recipient();
            recipient.setId(userId);
            request.setRecipient(recipient);

            SendMessageRequest.Attachment attachment = new SendMessageRequest.Attachment();
            attachment.setType(mediaType.toLowerCase());

            SendMessageRequest.Attachment.Payload payload = new SendMessageRequest.Attachment.Payload();
            payload.setAttachmentId(attachmentId);
            payload.setIsReusable(true);
            attachment.setPayload(payload);

            request.setAttachment(attachment);

            if (caption != null && !caption.trim().isEmpty()) {
                SendMessageRequest.MessageContent messageContent = new SendMessageRequest.MessageContent();
                messageContent.setText(caption);
                request.setMessage(messageContent);
            }

            return sendMessageRequest(url, request, userId);
        } catch (Exception e) {
            log.error("Error sending media message to user {}: ", userId, e);
            return null;
        }
    }

    public SendMessageResponse sendTemplateMessage(String userId, MessageTemplate template) {
        switch (template.getType().toUpperCase()) {
            case "TEXT":
                return sendTextMessage(userId, template.getContent());
            case "IMAGE":
            case "VIDEO":
            case "FILE":
                return sendMediaMessage(userId, template.getContent(), template.getType(), template.getCaption());
            default:
                log.error("Unsupported message type: {}", template.getType());
                return null;
        }
    }

    private SendMessageResponse sendMessageRequest(String url, SendMessageRequest request, String userId) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<SendMessageRequest> entity = new HttpEntity<>(request, headers);

            SendMessageResponse response = restTemplate.postForObject(url, entity, SendMessageResponse.class);

            log.info("Message sent to user {}: {}", userId, response != null ? response.getMessageId() : "unknown");
            return response;
        } catch (Exception e) {
            log.error("Error sending HTTP request to user {}: ", userId, e);
            return null;
        }
    }

    public Map<String, Boolean> sendTemplateToAllActiveUsers(MessageTemplate template) {
        Set<String> activeUsers = getActiveUsers();
        return sendTemplateToUsers(template, activeUsers);
    }

    public Map<String, Boolean> sendTemplateToUsers(MessageTemplate template, Set<String> userIds) {

        JsoonFormat josonFormat = new JsoonFormat();
        josonFormat.printJson(template);
        System.out.println(userIds);
        Map<String, Boolean> results = new HashMap<>();

        for (String userId : userIds) {
            try {
                SendMessageResponse response = sendTemplateMessage(userId, template);
                results.put(userId, response != null && response.getMessageId() != null);

                // Add delay to avoid rate limiting
                Thread.sleep(1000);
            } catch (Exception e) {
                log.error("Error sending template message to user {}: ", userId, e);
                results.put(userId, false);
            }
        }

        long successCount = results.values().stream().mapToLong(success -> success ? 1 : 0).sum();
        log.info("Template messages sent successfully to {}/{} users", successCount, userIds.size());

        return results;
    }
    public Map<String, Boolean> sendMessageToAllActiveUsers(String messageText) {
        MessageTemplate template = new MessageTemplate();
        template.setType("TEXT");
        template.setContent(messageText);

        return sendTemplateToAllActiveUsers(template);
    }

}