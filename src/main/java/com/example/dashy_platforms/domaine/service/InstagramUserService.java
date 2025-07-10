package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.infrastructure.database.entities.InstagramUserEntity;

import java.util.Optional;

public interface InstagramUserService {

    /**
     * Finds an Instagram user by their Instagram user ID
     * @param instagramUserId The unique ID of the Instagram user
     * @return Optional containing the found InstagramUserEntity, or empty if not found
     */
    Optional<InstagramUserEntity> findByInstagramUserId(String instagramUserId);
}