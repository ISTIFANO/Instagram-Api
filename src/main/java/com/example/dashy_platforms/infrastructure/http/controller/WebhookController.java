package com.example.dashy_platforms.infrastructure.http.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        System.out.println("Verification request received:");
        System.out.println("mode: " + mode);
        System.out.println("challenge: " + challenge);
        System.out.println("token: " + token);

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
}
