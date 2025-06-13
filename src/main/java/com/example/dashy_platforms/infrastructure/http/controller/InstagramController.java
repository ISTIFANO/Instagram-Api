package com.example.dashy_platforms.infrastructure.http.controller;

import com.example.dashy_platforms.domaine.model.*;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        System.out.println("Recipient ID: " + recipientId);
        System.out.println("Message JSON: " + messageJson);
        System.out.println("File name: " + file.getOriginalFilename());
        return ResponseEntity.ok("Upload Successful");
    }
    @PostMapping(value = "/sendtemplate", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InstagramMessageResponse> sendTemplateMessage(
            @RequestPart("recipient_id") String recipient_id,
            @RequestPart("file") MultipartFile file,
            @RequestPart InstagramTemplateRequest message) throws JsonProcessingException {
        String attachmentId = instagramService.uploadMediaAndGetAttachmentId(file);

        InstagramMessageResponse response = instagramService.sendGenericTemplate(recipient_id, message, attachmentId);
        if ("SENT".equals(response.getStatus())) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
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
}
