package com.example.dashy_platforms.infrastructure.http.controller;

import com.example.dashy_platforms.domaine.helper.JsoonFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/instagram")
public class WebhookController {

    @Value("${instagram.graph.access.token:default-value}")
    private String accessToken;

    @GetMapping("/webhook")
    public ResponseEntity<String> verifyWebhook(
            @RequestParam(name = "hub.mode", required = false) String mode,
            @RequestParam(name = "hub.challenge", required = false) String challenge,
            @RequestParam(name = "hub.verify_token", required = false) String token) {
        if (mode != null && token != null) {
            if (mode.equals("subscribe") && token.equals(accessToken)) {
                return ResponseEntity.ok(challenge);
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Verification token mismatch");
            }
        }
        return ResponseEntity.badRequest().body("Missing mode or token");
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> receiveWebhook(@RequestBody String payload) {
        System.out.println("Received webhook payload: " + payload);
        return ResponseEntity.ok("EVENT_RECEIVED");
    }

    @PostMapping("/instagram")
    public ResponseEntity<String> handleReadReceipt(@RequestBody String payload) {

        try {
            JsoonFormat jsoonFormat = new JsoonFormat();
            jsoonFormat.printJson(payload);
//            processInstagramWebhook(payload);
        return ResponseEntity.ok("EVENT_RECEIVED");}catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing webhook");

        }
    }
    private void processInstagramWebhook(Map<String, Object> payload ) {
        try {
            String object = (String) payload.get("object");
            if (!"instagram".equals(object)) {
                return;
            }

            List<Map<String, Object>> entries = (List<Map<String, Object>>) payload.get("entry");

            for (Map<String,Object> entry : entries) {
                List<Map<String, Object>> messagingList = (List<Map<String, Object>>) entry.get("messaging");

                if (messagingList != null) {
                    for (Map<String, Object> messaging : messagingList) {
                        processMessaging(messaging);
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
    private void processMessaging(Map<String, Object> messaging) {
        // Handle read receipts
        if (messaging.containsKey("read")) {
//            handleReadReceipt(messaging);
        }

        // Handle incoming messages
        if (messaging.containsKey("message")) {
//            handleIncomingMessage(messaging);
        }

        // Handle delivery receipts (if needed)
        if (messaging.containsKey("delivery")) {
//            handleDeliveryReceipt(messaging);
        }
    }
}
