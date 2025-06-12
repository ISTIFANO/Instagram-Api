package com.example.dashy_platforms.infrastructure.database.service;

import com.example.dashy_platforms.domaine.helper.JsoonFormat;
import com.example.dashy_platforms.domaine.model.TokenDTOs.InstagramLongLivedTokenResponse;
import com.example.dashy_platforms.domaine.model.TokenDTOs.InstagramTokenResponse;
import com.example.dashy_platforms.infrastructure.database.entities.InstagramUserEntity;
import com.example.dashy_platforms.infrastructure.database.entities.UserEntity;
import com.example.dashy_platforms.infrastructure.database.repositeries.InstagramUserRepository;
import com.example.dashy_platforms.infrastructure.http.configuration.InstagramProperties;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
public class InstagramServiceImp {
    @Value("${instagram.app.Id:default-value}")
    private String appId;
    @Value("${instagram.app.Secret:default-value}")
    private String appSecret;
    @Value("${instagram.redirectUri:default-value}")
    private String redirectUri;
    private List<String> scopes = Arrays.asList(
            "instagram_business_basic",
            "instagram_business_content_publish",
            "instagram_business_manage_messages",
            "instagram_business_manage_comments"
    );
    private final Map<String, Boolean> usedCodes = new ConcurrentHashMap<>();


    private static final Logger logger = LoggerFactory.getLogger(InstagramServiceImp.class);


    private final InstagramUserRepository instagramRepository;
    private final RestTemplate restTemplate;

    public InstagramServiceImp(InstagramProperties properties, InstagramUserRepository instagramRepository, RestTemplate restTemplate) {
        this.instagramRepository = instagramRepository;
        this.restTemplate = restTemplate;
    }
/*
*
get Authorization from meta @return le code d access pour l utilisateur
*
*
 */
    public String generateAuthorizationUrl(String state) {
        StringBuilder authorizationUrl = new StringBuilder("https://www.instagram.com/oauth/authorize");

        authorizationUrl.append("?client_id=").append(appId);
        authorizationUrl.append("&redirect_uri=").append(URLEncoder.encode(redirectUri, StandardCharsets.UTF_8));
        authorizationUrl.append("&response_type=code");
        authorizationUrl.append("&scope=").append(String.join(",", scopes));

        if (state != null && !state.isEmpty()) {
            authorizationUrl.append("&state=").append(URLEncoder.encode(state, StandardCharsets.UTF_8));
        }

        logger.info("Generated Instagram authorization URL: {}", authorizationUrl);
        return authorizationUrl.toString();
    }

    public InstagramTokenResponse.TokenData getInstagramToken(String code) {
        String url = "https://api.instagram.com/oauth/access_token";
JsoonFormat jsoonFormat = new JsoonFormat();
jsoonFormat.printJson(code);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", appId);
        params.add("client_secret", appSecret);
        params.add("grant_type", "authorization_code");
        params.add("redirect_uri", redirectUri);
        params.add("code", code);


        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        try {
            String rawResponse = restTemplate.postForObject(url, request, String.class);
            logger.info("Instagram token raw response: {}", rawResponse);

            ResponseEntity<InstagramTokenResponse> response =
                    restTemplate.postForEntity(url, request, InstagramTokenResponse.class);

            if (response.getStatusCode() == HttpStatus.OK &&
                    response.getBody() != null &&
                    response.getBody().getData() != null &&
                    !response.getBody().getData().isEmpty()) {

                return response.getBody().getData().get(0);
            }
            throw new RuntimeException("Instagram returned invalid response. Response: " + rawResponse);
        } catch (HttpClientErrorException e) {
            logger.error("Instagram token error: {}", e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get Instagram token: " + e.getResponseBodyAsString());
        }
    }

    public  InstagramLongLivedTokenResponse  exchangeForLongLivedToken(String shortLivedToken){

        String url = "https://graph.instagram.com/access_token" +
                "?grant_type=ig_exchange_token" +
                "&client_secret=" + appSecret +
                "&access_token=" + shortLivedToken;

        try {
            ResponseEntity<InstagramLongLivedTokenResponse> response =
                    restTemplate.getForEntity(url, InstagramLongLivedTokenResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to exchange for long-lived token");
            }
        } catch (HttpClientErrorException e) {
            logger.error("Error exchanging for long-lived token: " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to get long-lived token: " + e.getMessage());
        }
    }

    public  InstagramLongLivedTokenResponse refreshAccessToken(String LongLivedToken){
        String url = "https://graph.instagram.com/refresh_access_token" +
                "?grant_type=ig_refresh_token" +
                "&access_token=" + LongLivedToken;

        try {
            ResponseEntity<InstagramLongLivedTokenResponse> response =
                    restTemplate.getForEntity(url, InstagramLongLivedTokenResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to refresh access token");
            }
        } catch (HttpClientErrorException e) {
            logger.error("Error refreshing access token: " + e.getResponseBodyAsString());
            throw new RuntimeException("Failed to refresh token: " + e.getMessage());
        }


    }

    public InstagramUserEntity saveInstagramUser(String instagramUserId, String accessToken,Long expiresIn, String permissions){

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(expiresIn);
        Optional<InstagramUserEntity> userTrouvable = instagramRepository.findById(Long.valueOf(instagramUserId));
        InstagramUserEntity user; ;
   if(userTrouvable.isPresent()){
   user = userTrouvable.get();
    user.setAccessToken(accessToken);
    user.setTokenExpiresAt(expiresAt);
    user.setPermissions(permissions);

   }else{
    user = new InstagramUserEntity(instagramUserId, accessToken, expiresAt, permissions);
   }
        user = instagramRepository.save(user);
        return user;
    }

    public InstagramUserEntity OuthentificationProcess(String code) throws Exception {


        if(usedCodes.containsKey(code)){
            throw new Exception("Authorization code already used");
        }
        InstagramTokenResponse.TokenData tokenData = getInstagramToken(code);

        InstagramLongLivedTokenResponse longLivedToken = exchangeForLongLivedToken(tokenData.getAccess_token());

        return saveInstagramUser(
                tokenData.getUser_id(),
                longLivedToken.getAccessToken(),
                longLivedToken.getExpiresIn(),
                tokenData.getPermissions()
        );
    }

    public void refreshExpiringTokens(){

        LocalDateTime expiryThreshold = LocalDateTime.now().plusDays(7);

        List<InstagramUserEntity> users = instagramRepository.findTokensExpiringBefore(expiryThreshold);

        for (InstagramUserEntity user : users) {

            try {

                if (user.getUpdatedAt().isBefore(LocalDateTime.now().minusHours(24))) {
                    InstagramLongLivedTokenResponse refreshedToken = refreshAccessToken(user.getAccessToken());

                    user.setAccessToken(refreshedToken.getAccessToken());
                    user.setTokenExpiresAt(LocalDateTime.now().plusSeconds(refreshedToken.getExpiresIn()));
                    instagramRepository.save(user);
                    logger.info("Refreshed token for user: " + user.getInstagramUserId());
                }
            } catch (Exception e) {
                logger.error("Failed to refresh token for user " + user.getInstagramUserId(), e);
            }
        }
    }











    public Optional<InstagramUserEntity> getUserByInstagramId(Long instagramUserId) {
        return instagramRepository.findById(instagramUserId);
    }

}
