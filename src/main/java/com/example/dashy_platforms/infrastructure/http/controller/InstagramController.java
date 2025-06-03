package com.example.dashy_platforms.infrastructure.http.controller;

import com.example.dashy_platforms.domaine.model.*;
import com.example.dashy_platforms.domaine.model.MessageText.InstagramMessageRequest;
import com.example.dashy_platforms.domaine.model.Template.Button_Template.InstagramButtonTemplateRequest;
import com.example.dashy_platforms.domaine.model.Template.QuickReplie.Quick_replies_Request;
import com.example.dashy_platforms.infrastructure.database.service.InstagramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/instagram")
@CrossOrigin(origins = "*")
public class InstagramController {

private final InstagramService instagramService;

    public InstagramController(InstagramService instagramService) {
        this.instagramService = instagramService;
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
    @PostMapping("/sendtemplate")
    public ResponseEntity<InstagramMessageResponse> sendTemplateMessage(
            @RequestParam String recipient_id,
            @RequestBody InstagramTemplateRequest message) {

        InstagramMessageResponse response = instagramService.sendGenericTemplate(recipient_id, message);
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
}
