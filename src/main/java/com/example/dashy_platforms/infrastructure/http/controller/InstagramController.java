package com.example.dashy_platforms.infrastructure.http.controller;

import com.example.dashy_platforms.domaine.model.*;
import com.example.dashy_platforms.domaine.model.BroadcastMessage.GenericTemplateRequest;
import com.example.dashy_platforms.domaine.model.BroadcastMessage.InstagramMessageR;
import com.example.dashy_platforms.domaine.model.BroadcastMessage.MessageTemplate;
import com.example.dashy_platforms.domaine.model.MediaAttachment.AttachementResponse;
import com.example.dashy_platforms.domaine.model.MediaAttachment.AttachmentDto;
import com.example.dashy_platforms.domaine.model.MediaAttachment.AttachmentRequest;
import com.example.dashy_platforms.domaine.model.MessageMedia.InstagramMediaMessageRequest;
import com.example.dashy_platforms.domaine.model.MessageSticker.InstagramStickerRequest;
import com.example.dashy_platforms.domaine.model.MessageText.InstagramMessageRequest;
import com.example.dashy_platforms.domaine.model.Reaction.ReactionContainer;
import com.example.dashy_platforms.domaine.model.Template.Button_Template.InstagramButtonTemplateRequest;
import com.example.dashy_platforms.domaine.model.Template.QuickReplie.Quick_replies_Request;
import com.example.dashy_platforms.infrastructure.database.service.InstagramService;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/instagram")
@CrossOrigin(origins = "*")
public class InstagramController {

private final InstagramService instagramService;
    private final ObjectMapper objectMapper;

    public InstagramController(InstagramService instagramService, ObjectMapper objectMapper) {
        this.instagramService = instagramService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/sendmessage")
    public ResponseEntity<InstagramMessageResponse> sendTextMessage(@RequestBody InstagramMessageRequest payload) {

        InstagramMessageResponse resp = instagramService.sendTextMessage(payload);
        if ("SENT".equals(resp.getStatus())) {
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.badRequest().body(resp);
        }
    }
    @PostMapping("/sendtemplateTest")
    public ResponseEntity<?> testUpload(
            @RequestPart("file") MultipartFile file,
            @RequestPart("recipient_id") String recipientId,
            @RequestPart("message") String messageJson
    ) {
        return ResponseEntity.ok("Upload Successful");
    }
    @PostMapping(value = "/sendtemplate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InstagramTemplateRequest> sendTemplateMessage(
            @RequestPart("recipient_id") String recipient_id,
            @RequestPart("file") MultipartFile file,
            @RequestPart("message") String message) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        InstagramTemplateRequest instagram = objectMapper.readValue(message, InstagramTemplateRequest.class);

        String attachmentId = instagramService.uploadMediaAndGetAttachmentId(file);

        InstagramMessageResponse response = instagramService.sendGenericTemplate(recipient_id, instagram, attachmentId);
        if ("SENT".equals(response.getStatus())) {
            return ResponseEntity.ok(instagram);
        } else {
            return ResponseEntity.badRequest().body(instagram);
        }
    }
    @PostMapping("/sendbuttontemplate")
    public ResponseEntity<InstagramMessageResponse> sendTemplateButtonMessage(
            @RequestParam String recipient_id,
            @RequestBody InstagramButtonTemplateRequest message) {

        InstagramMessageResponse response = instagramService.sendButtonTemplate(recipient_id, message);
        if ("SENT".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    @PostMapping("/sendQuick_repliestemplate")
    public ResponseEntity<InstagramMessageResponse> sendTemplateQuick_replies(
            @RequestParam String recipient_id,
            @RequestBody Quick_replies_Request message) {

        InstagramMessageResponse response = instagramService.sendQuick_repliesTemplate(message);
        if ("SENT".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from Instagram API!";
    }
    @GetMapping("/auth/instagram/callback")
    public String callback() {
        return "_auth_facebook_callback";
    }

    @GetMapping("/users")
    public ResponseEntity<Set<UserListInfoResponse>> getMessagedUsers() {
        Set<UserListInfoResponse> users = instagramService.listMessagedUsers();
        return ResponseEntity.ok(users);
    }

    @PostMapping("/upload-attachment")
    public ResponseEntity<AttachementResponse> uploadAttachment(@RequestBody AttachmentDto attachmentRequest) {
        try {
            AttachementResponse attachmentId = instagramService.uploadAttachment(attachmentRequest);
            return ResponseEntity.ok(attachmentId);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PostMapping("/send-image-message")
    public ResponseEntity<InstagramMessageResponse> sendImageMessage(
            @RequestBody AttachmentRequest messageRequest ) {
        try {
            InstagramMessageResponse response = instagramService.sendImageMessage(messageRequest);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new InstagramMessageResponse("ERROR", e.getMessage()));
        }
    }
    @PostMapping("/send-reaction")
    public ResponseEntity<InstagramMessageResponse> sendReaction(
            @RequestBody ReactionContainer request) {

        InstagramMessageResponse response = instagramService.sendReaction(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-sticker")
    public ResponseEntity<InstagramMessageResponse> sendSticker(
            @RequestBody InstagramStickerRequest request) {

        InstagramMessageResponse response = instagramService.sendSticker(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/send-media")
    public ResponseEntity<InstagramMessageResponse> uploadAndSend(
            @RequestParam("file") MultipartFile file,
            @RequestParam("recipientId") String recipientId) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new InstagramMessageResponse("ERROR", "File is empty"));
            }

            String attachmentId = instagramService.uploadMediaAndGetAttachmentId(file);
            String mediaType = getMediaType(file.getContentType());
            InstagramMessageResponse resp = instagramService.sendMediaByAttachmentId(recipientId, attachmentId, mediaType);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(new InstagramMessageResponse("ERROR", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new InstagramMessageResponse("ERROR", e.getMessage()));
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

    @PostMapping("/send-text-to-all")
    public ResponseEntity<Map<String, Object>> sendTextToAll(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return badRequest("Message text is required");
        }
        return okResponse(instagramService.sendMessageToAllActiveUsers(message));
    }

    @PostMapping("/upload-and-send-media")
    public ResponseEntity<Map<String, Object>> uploadAndSendMedia(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "caption", required = false) String caption) {
        try {
            String attachmentId = instagramService.uploadMediaAndGetAttachmentId(file);
            String mediaType = getMediaType(file.getContentType());

            System.out.println(attachmentId);
            Map<String, Object> response = new HashMap<>();
            response.put("attachmentId", attachmentId);
            response.put("mediaType", mediaType);
            response.putAll(buildStats(instagramService.sendMediaToAllActiveUsers(attachmentId, mediaType, caption)));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return serverError("Failed to upload and send media: " + e.getMessage());
        }
    }

    @PostMapping("/send-generic-template")
    public ResponseEntity<Map<String, Object>> sendGenericTemplate(
            @RequestBody GenericTemplateRequest request) {

        MessageTemplate template = new MessageTemplate();
        template.setType("GENERIC");

        MessageTemplate.GenericContent content = new MessageTemplate.GenericContent();
        content.setElements(request.getElements());

        template.setContent(content);
        template.setCaption(request.getCaption());

        Map<String, Boolean> results = instagramService.sendTemplateToAllActiveUsers(template);

        long successCount = results.values().stream().filter(Boolean::booleanValue).count();

        return ResponseEntity.ok(Map.of(
                "totalUsers", results.size(),
                "successCount", successCount,
                "failureCount", results.size() - successCount,
                "details", results
        ));
    }

    @PostMapping("/send-custom-to-all")
    public ResponseEntity<Map<String, Object>> sendCustomToAll(@RequestBody InstagramMessageR request) {
        if (request.getMessage() == null) {
            return badRequest("Message content is required");
        }
        return okResponse(instagramService.sendCustomMessageToAllActiveUsers(request));
    }

    @GetMapping("/active-users")
    public ResponseEntity<Set<String>> getActiveUsers() {
        return ResponseEntity.ok(instagramService.getActiveUsers());
    }

    private ResponseEntity<Map<String, Object>> okResponse(Map<String, Boolean> results) {
        return ResponseEntity.ok(buildStats(results));
    }

    private ResponseEntity<Map<String, Object>> badRequest(String error) {
        return ResponseEntity.badRequest().body(Map.of("error", error));
    }

    private ResponseEntity<Map<String, Object>> serverError(String error) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", error));
    }

    private Map<String, Object> buildStats(Map<String, Boolean> results) {
        long successCount = results.values().stream().filter(Boolean::booleanValue).count();
        return Map.of(
                "totalUsers", results.size(),
                "successCount", successCount,
                "failureCount", results.size() - successCount,
                "details", results
        );
    }
}



