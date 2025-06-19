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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Tag(name = "Instagram Messaging API", description = "Endpoints for Instagram messaging functionality")
public class InstagramController {

private final InstagramService instagramService;
    private final ObjectMapper objectMapper;

    public InstagramController(InstagramService instagramService, ObjectMapper objectMapper) {
        this.instagramService = instagramService;
        this.objectMapper = objectMapper;
    }
    @Operation(
            summary = "Send text message",
            description = "Send a simple text message to a specific user"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Message sent successfully",
                    content = @Content(schema = @Schema(implementation = InstagramMessageResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input",
                    content = @Content)
    })

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
    @Operation(summary = "Send generic template", description = "Send a generic template message to a user")
    @PostMapping("/sendtemplate")
    public ResponseEntity<InstagramMessageResponse> sendTemplateMessage(
            @RequestBody InstagramTemplateRequest message) {
String recipient_id = message.getRecipient().getId();
        InstagramMessageResponse response = instagramService.sendGenericTemplate(recipient_id, message);
        if ("SENT".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }
    @Operation(summary = "Send button template", description = "Send a button template message to a user")
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
    @Operation(summary = "Send quick replies", description = "Send quick reply options to a user")
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
    @Operation(summary = "Get messaged users", description = "Get list of users who have messaged the page")
    @GetMapping("/users")
    public ResponseEntity<Set<UserListInfoResponse>> getMessagedUsers() {
        Set<UserListInfoResponse> users = instagramService.listMessagedUsers();
        return ResponseEntity.ok(users);
    }
    @Operation(summary = "Upload attachment", description = "Upload a media attachment to Instagram")
    @PostMapping("/upload-attachment")
    public ResponseEntity<AttachementResponse> uploadAttachment(@RequestBody AttachmentDto attachmentRequest) {
        try {
            AttachementResponse attachmentId = instagramService.uploadAttachment(attachmentRequest);
            return ResponseEntity.ok(attachmentId);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Operation(summary = "Send image message", description = "Send an image message using attachment ID")
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
    @Operation(summary = "Send reaction", description = "Send a reaction to a message")

    @PostMapping("/send-reaction")
    public ResponseEntity<InstagramMessageResponse> sendReaction(
            @RequestBody ReactionContainer request) {

        InstagramMessageResponse response = instagramService.sendReaction(request);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Send sticker", description = "Send a sticker message")
    @PostMapping("/send-sticker")
    public ResponseEntity<InstagramMessageResponse> sendSticker(
            @RequestBody InstagramStickerRequest request) {

        InstagramMessageResponse response = instagramService.sendSticker(request);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Upload and send media", description = "Upload media and send it to a recipient")
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
    @Operation(summary = "Send text to all active users", description = "Send a text message to all active users")
    @PostMapping("/send-text-to-all")
    public ResponseEntity<Map<String, Object>> sendTextToAll(@RequestBody Map<String, String> request) {
        String message = request.get("message");
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Message text is required"));
        }

        try {
            Map<String, Boolean> results = instagramService.sendTextToAllActiveUsers(message);
            long successCount = results.values().stream().filter(Boolean::booleanValue).count();
            Map<String, Object> response = new HashMap<>();
            response.put("totalUsers", results.size());
            response.put("successCount", successCount);
            response.put("failureCount", results.size() - successCount);
            response.put("details", results);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to send text to all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to send messages: " + e.getMessage()));
        }
    }
    @Operation(summary = "Upload and broadcast media", description = "Upload media and send it to all active users")
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
    @Operation(summary = "Send generic template to all", description = "Send a generic template to all active users")
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
    @Operation(summary = "Send custom message to all", description = "Send a custom formatted message to all active users")
    @PostMapping("/send-custom-to-all")
    public ResponseEntity<Map<String, Object>> sendCustomToAll(@RequestBody InstagramMessageR request) {
        if (request.getMessage() == null) {
            return badRequest("Message content is required");
        }
        return okResponse(instagramService.sendCustomMessageToAllActiveUsers(request));
    }

    @Operation(summary = "Get active users", description = "Get list of users with active conversations")

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



