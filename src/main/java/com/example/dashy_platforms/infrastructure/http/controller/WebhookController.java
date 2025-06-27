package com.example.dashy_platforms.infrastructure.http.controller;

import com.example.dashy_platforms.domaine.helper.JsoonFormat;
import com.example.dashy_platforms.domaine.model.Webhook.WebhookPayload;
import com.example.dashy_platforms.infrastructure.database.service.AutoReplyService;
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
public class WebhookController{

    @Value("${instagram.graph.access.token:default-value}")
    private String accessToken;

    @Autowired
    private AutoReplyService autoReplyService;

    // Verification endpoint (GET)
    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.challenge", required = false) String challenge,
            @RequestParam(name = "hub.verify_token", required = false) String token) {

        if (mode != null && token != null) {
            if (mode.equals("subscribe") && token.equals(accessToken)) {
                return ResponseEntity.ok(challenge);
            }
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification token mismatch");
        }
        return ResponseEntity.badRequest().body("Missing mode or token");
    }
//    @PostMapping("/webhook")
//    public ResponseEntity<String> receiveWebhook(@RequestBody String payload) {
//        System.out.println("Received webhook payload: " + payload);
//        return ResponseEntity.ok("EVENT_RECEIVED");
//    }

    // Main webhook processing endpoint (POST)
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody(required = false) WebhookPayload payload,
            @RequestBody(required = false) String rawPayload) {

        try {
            if (rawPayload != null) {
                System.out.println("Received raw payload: " + rawPayload);
                JsoonFormat jsoonFormat = new JsoonFormat();
                jsoonFormat.printJson(rawPayload);
                return ResponseEntity.ok("RAW_EVENT_RECEIVED");
            }

            // Handle structured payload
            if (payload != null && payload.getEntry() != null) {
                payload.getEntry().forEach(entry -> {
                    if (entry.getMessaging() != null) {
                        entry.getMessaging().forEach(msg -> {
                            try {
                                if (msg != null && msg.getSender() != null && msg.getMessage() != null) {
                                    String senderId = msg.getSender().getId();
                                    String text = msg.getMessage().getText();
                                    LocalDateTime receivedAt = Instant.ofEpochMilli(msg.getTimestamp())
                                            .atZone(ZoneId.of("Africa/Casablanca"))
                                            .toLocalDateTime();
                                    autoReplyService.checkAndReply(senderId, text, receivedAt);
                                }
                            } catch (Exception e) {
                                System.err.println("Error processing individual message: " + e.getMessage());
                                e.printStackTrace();
                            }
                        });
                    }
                });
                return ResponseEntity.ok("WEBHOOK_RECEIVED");
            }

            return ResponseEntity.badRequest().body("No valid payload received");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing webhook: " + e.getMessage());
        }
    }
}