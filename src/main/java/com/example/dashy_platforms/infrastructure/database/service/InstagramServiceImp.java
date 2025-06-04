package com.example.dashy_platforms.infrastructure.database.service;

import com.example.dashy_platforms.infrastructure.database.repositeries.InstagramUserRepository;
import com.example.dashy_platforms.infrastructure.http.configuration.InstagramProperties;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
@Service
@Transactional
public class InstagramServiceImp {

    private final InstagramProperties properties;
    private final InstagramUserRepository instagramRepository;
    private final RestTemplate  restTemplate;
    private final Logger logger = (Logger) LoggerFactory.getLogger(InstagramServiceImp.class);


    public InstagramServiceImp(InstagramProperties properties, InstagramUserRepository instagramRepository, RestTemplate restTemplate) {
        this.properties = properties;
        this.instagramRepository = instagramRepository;
        this.restTemplate = restTemplate;
    }

    public String generateAuthorizationUrl(String State){
StringBuilder authorizationUrl = new StringBuilder("https://www.instagram.com/oauth/authorize");

        authorizationUrl.append("?client_id=").append(properties.getAppId())
        authorizationUrl.append("&redirect_uri=").append(URLEncoder.encode(properties.getRedirectUri(), StandardCharsets.UTF_8));
        authorizationUrl.append("&response_type=code");
        authorizationUrl.append("&scope=").append(String.join(",", properties.getScopes()));

        if (State != null && !State.isEmpty()) {
            authorizationUrl.append("&state=").append(URLEncoder.encode(State, StandardCharsets.UTF_8));
        }

        return authorizationUrl.toString();
    }
}
