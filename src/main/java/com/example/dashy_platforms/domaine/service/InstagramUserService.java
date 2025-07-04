package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.infrastructure.database.entities.InstagramUserEntity;

import java.util.Optional;

public interface InstagramUserService {
    Optional<InstagramUserEntity> findByInstagramUserId(String instagramUserId);

}
