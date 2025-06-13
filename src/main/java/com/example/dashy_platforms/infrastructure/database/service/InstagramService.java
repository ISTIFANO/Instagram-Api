package com.example.dashy_platforms.infrastructure.database.service;

import com.example.dashy_platforms.domaine.helper.JsoonFormat;
import com.example.dashy_platforms.domaine.model.*;
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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.logging.Logger;

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
        JsoonFormat jsoonFormat = new JsoonFormat();
        jsoonFormat.printJson(fileRequestDto);
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
    public String uploadMediaAndGetAttachmentId(MultipartFile file) {
        String url = String.format("https://graph.facebook.com/v22.0/%s/message_attachments?access_token=%s", facebookPageId, pageaccessToken);

        String mediaType = getMediaType(file.getContentType());

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("platform", "instagram");
        body.add("filedata", file.getResource());
        body.add("message", String.format(
                    "{\"attachment\":{\"type\":\"%s\",\"payload\":{\"is_reusable\":true}}}", mediaType));

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
        String url = String.format("%s/v18.0/me/messages", graphApiUrl);

        System.out.println(recipientId);
        System.out.println(attachmentId);
        System.out.println(mediaType);

        Map<String, Object> payload = Map.of(
                "attachment_id", attachmentId
        );

        Map<String, Object> attachment = Map.of(
                "type", "MEDIA_SHARE",
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
            headers.setBearerAuth(pageaccessToken);

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
}