package com.example.dashy_platforms.infrastructure.database.service;

import com.example.dashy_platforms.domaine.service.InstagramUserService;
import com.example.dashy_platforms.infrastructure.database.entities.InstagramUserEntity;
import com.example.dashy_platforms.infrastructure.database.repositeries.InstagramUserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;
@Service
public class InstagramUserServiceImpl  implements InstagramUserService {
    private final InstagramUserRepository instagramUserRepository;

    public InstagramUserServiceImpl(InstagramUserRepository instagramUserRepository) {
        this.instagramUserRepository = instagramUserRepository;
    }

    @Override
    public Optional<InstagramUserEntity> findByInstagramUserId(String instagramUserId) {
        return instagramUserRepository.findByInstagramUserId(instagramUserId);
    }
}
