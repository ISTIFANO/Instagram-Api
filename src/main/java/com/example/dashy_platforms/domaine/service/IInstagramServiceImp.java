package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.domaine.model.TokenDTOs.InstagramLongLivedTokenResponse;
import com.example.dashy_platforms.domaine.model.TokenDTOs.InstagramTokenResponse;
import com.example.dashy_platforms.infrastructure.database.entities.InstagramUserEntity;

import java.util.Optional;

public interface IInstagramServiceImp {
    /**
     * Generates Instagram authorization URL for OAuth flow
     * @param state Optional state parameter for security
     * @return Authorization URL string
     */
    String generateAuthorizationUrl(String state);

    /**
     * Exchanges authorization code for short-lived access token
     * @param code Authorization code from Instagram
     * @return Token data including access token and user info
     */
    InstagramTokenResponse.TokenData getInstagramToken(String code);

    /**
     * Exchanges short-lived token for long-lived token
     * @param shortLivedToken Short-lived access token
     * @return Long-lived token response
     */
    InstagramLongLivedTokenResponse exchangeForLongLivedToken(String shortLivedToken);

    /**
     * Refreshes a long-lived access token
     * @param LongLivedToken Long-lived token to refresh
     * @return Refreshed token response
     */
    InstagramLongLivedTokenResponse refreshAccessToken(String LongLivedToken);

    /**
     * Saves or updates Instagram user with token info
     * @param instagramUserId User's Instagram ID
     * @param accessToken Access token
     * @param expiresIn Token expiration in seconds
     * @param permissions Granted permissions
     * @return Saved Instagram user entity
     */
    InstagramUserEntity saveInstagramUser(String instagramUserId, String accessToken, Long expiresIn, String permissions);

    /**
     * Complete OAuth authentication process
     * @param code Authorization code
     * @return Authenticated Instagram user
     * @throws Exception If code was already used or authentication fails
     */
    InstagramUserEntity OuthentificationProcess(String code) throws Exception;

    /**
     * Refreshes tokens that will expire soon
     */
    void refreshExpiringTokens();

    /**
     * Finds Instagram user by their ID
     * @param instagramUserId User's Instagram ID
     * @return Optional containing user if found
     */
    Optional<InstagramUserEntity> getUserByInstagramId(Long instagramUserId);
}
