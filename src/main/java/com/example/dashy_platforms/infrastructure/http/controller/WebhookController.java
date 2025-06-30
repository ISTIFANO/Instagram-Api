package com.example.dashy_platforms.infrastructure.http.controller;

import com.example.dashy_platforms.domaine.model.Webhook.WebhookPayload;
import com.example.dashy_platforms.infrastructure.database.service.AutoReplyService;
import com.example.dashy_platforms.infrastructure.database.service.MessageServiceImp;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/instagram")
public class WebhookController {

    @Value("${instagram.graph.access.token:default-value}")
    private String accessToken;

    @Autowired
    private MessageServiceImp messageService;

    @Autowired
    private AutoReplyService autoReplyService;

    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.challenge", required = false) String challenge,
            @RequestParam(name = "hub.verify_token", required = false) String token) {

        if (mode != null && token != null) {
            if ("subscribe".equals(mode) && accessToken.equals(token)) {
                return ResponseEntity.ok(challenge);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification token mismatch");
        }
        return ResponseEntity.badRequest().body("Missing mode or token");
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String rawPayload) {
        System.out.println("📦 Received raw payload: " + rawPayload);

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            WebhookPayload payload;

            try {
                payload = objectMapper.readValue(rawPayload, WebhookPayload.class);
            } catch (Exception parseException) {
                System.out.println("⚠️ Could not parse as structured payload: " + parseException.getMessage());
                return ResponseEntity.ok("RAW_EVENT_RECEIVED");
            }

            if (payload != null && payload.getEntry() != null) {
                payload.getEntry().forEach(entry -> {
                    if (entry.getMessaging() != null) {
                        entry.getMessaging().forEach(msg -> {
                            try {
                                String senderId = msg.getSender() != null ? msg.getSender().getId() : null;
                                String recipientId = msg.getRecipient() != null ? msg.getRecipient().getId() : null;
                                LocalDateTime eventTime = Instant.ofEpochMilli(msg.getTimestamp())
                                        .atZone(ZoneId.of("Africa/Casablanca"))
                                        .toLocalDateTime();

                                // Save sender as user if needed
                                if (senderId != null) {
                                    messageService.saveInstagramUserIfNotExists(senderId);
                                }

                                // Handle "read" (seen) messages
                                if (msg.getRead() != null && msg.getRead().getMid() != null) {
                                    String mid = msg.getRead().getMid();
                                    messageService.updateMessageStatus(mid, "SEEN");
                                    System.out.println("👁️ Message seen (mid: " + mid + ")");
                                    autoReplyService.markMessageSeenByMid(senderId, mid, eventTime);
                                } else if (msg.getRead() != null && msg.getRead().getWatermark() != null) {
                                    LocalDateTime seenUntil = Instant.ofEpochMilli(msg.getRead().getWatermark())
                                            .atZone(ZoneId.of("Africa/Casablanca"))
                                            .toLocalDateTime();
                                    System.out.println("👁 All messages seen by " + senderId + " until " + seenUntil);
                                    autoReplyService.markMessagesSeenUntil(senderId, seenUntil);
                                }
                                if (msg.getReaction() != null) {
                                    String mid = msg.getReaction().getMid();
                                    String reactionType = msg.getReaction().getReaction();
                                    System.out.println("❤️ Reaction received: " + reactionType + " on message " + mid);

                                    messageService.addReactionToMessage(mid, reactionType);
                                }

                                if (msg.getMessage() != null && msg.getMessage().getText() != null) {
                                    String text = msg.getMessage().getText();
                                    String mid = msg.getMessage().getMid();
                                    messageService.saveIncomingMessage(senderId, recipientId, text, "TEXT", eventTime,mid);
                                    System.out.println("📩 Message from " + senderId + ": " + text);
                                    autoReplyService.checkAndReply(senderId, text, eventTime);
                                }

                            } catch (Exception e) {
                                System.err.println("❌ Error processing individual message: " + e.getMessage());
                                e.printStackTrace();
                            }
                        });
                    }
                });
                return ResponseEntity.ok("WEBHOOK_RECEIVED");
            }

            return ResponseEntity.ok("RAW_EVENT_RECEIVED");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing webhook: " + e.getMessage());
        }
    }
}
