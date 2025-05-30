package com.example.dashy_platforms.infrastructure.http.controller;

import com.example.dashy_platforms.domaine.model.GenericTemplateData;
import com.example.dashy_platforms.domaine.model.InstagramMessageResponse;
import com.example.dashy_platforms.domaine.model.InstagramTemplateRequest;
import com.example.dashy_platforms.domaine.model.Message;
import com.example.dashy_platforms.infrastructure.database.service.InstagramService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/instagram")
@CrossOrigin(origins = "*")
public class InstagramController {

private final InstagramService instagramService;

    public InstagramController(InstagramService instagramService) {
        this.instagramService = instagramService;
    }

    @PostMapping("/sendmessage")
    public ResponseEntity<InstagramMessageResponse> sendTextMessage(@RequestBody Map<String, Object> payload) {


        String recipientId = String.valueOf(payload.get("recipientId"));
        String message = String.valueOf(payload.get("message"));


        InstagramMessageResponse resp = instagramService.sendTextMessage(recipientId, message);
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
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from Instagram API!";
    }


}
