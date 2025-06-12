package com.example.dashy_platforms.infrastructure.http.controller;

import com.example.dashy_platforms.infrastructure.database.entities.InstagramUserEntity;
import com.example.dashy_platforms.infrastructure.database.service.InstagramServiceImp;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/instagram")
public class OAuthController {
    @Autowired
    private InstagramServiceImp instagramServiceImp;

    private final Logger logger = LoggerFactory.getLogger(OAuthController.class);

    public OAuthController(InstagramServiceImp instagramService) {
        this.instagramServiceImp = instagramService;
    }
    @GetMapping("/login")
    public ResponseEntity<Map<String, String>> initiateLogin(HttpServletRequest request) {
        String state = UUID.randomUUID().toString();

        HttpSession session = request.getSession();
        session.setAttribute("instagram_oauth_state", state);

        String authUrl = instagramServiceImp.generateAuthorizationUrl(state);

        Map<String, String> response = new HashMap<>();
        response.put("authorization_url", authUrl);
        response.put("state", state);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/callback")
    public ResponseEntity<String> handleCallback(
            @RequestParam("code") String code,
            HttpServletRequest request) {
        try {
            InstagramUserEntity user = instagramServiceImp.OuthentificationProcess(code);
            return ResponseEntity.ok("User authenticated: " + user.getInstagramUserId());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
