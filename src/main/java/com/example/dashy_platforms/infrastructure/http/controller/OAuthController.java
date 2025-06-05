package com.example.dashy_platforms.infrastructure.http.controller;

import com.example.dashy_platforms.infrastructure.database.service.InstagramServiceImp;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/instagram")
public class OAuthController {
    @Autowired
    private InstagramServiceImp instagramServiceImp;

    private final Logger logger = LoggerFactory.getLogger(OAuthController.class);

    public OAuthController(InstagramServiceImp instagramService) {
        this.instagramServiceImp = instagramService;
    }



}
