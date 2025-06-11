package com.example.dashy_platforms.infrastructure.http.configuration;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "instagram")
@Component
@Getter
@Setter
public class InstagramProperties {
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
    public String getAppId() { return appId; }
    public void setAppId(String appId) { this.appId = appId; }

    public String getAppSecret() { return appSecret; }
    public void setAppSecret(String appSecret) { this.appSecret = appSecret; }

    public String getRedirectUri() { return redirectUri; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }

    public List<String> getScopes() { return scopes; }
    public void setScopes(List<String> scopes) { this.scopes = scopes; }
}
