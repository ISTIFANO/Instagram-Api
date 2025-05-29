package com.example.dashy_platforms.infrastructure.http.controller;

import com.example.dashy_platforms.domaine.model.InstagramMessageResponse;
import com.example.dashy_platforms.infrastructure.database.service.InstagramService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/instagram")
@CrossOrigin(origins = "*")
public class InstagramController {

private final InstagramService instagramService;

    public InstagramController(InstagramService instagramService) {
        this.instagramService = instagramService;
    }

    @PostMapping("/send-text-message")
public ResponseEntity<InstagramMessageResponse> sendTextMessage( @RequestParam String recipientId, @RequestParam String message) {

    InstagramMessageResponse resp = instagramService.sendTextMessage(recipientId, message);
    if ("SENT".equals(resp.getStatus())) {
        return ResponseEntity.ok(resp);
    } else {
        return ResponseEntity.badRequest().body(resp);
    }




}


    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from Instagram API!";
    }












}
