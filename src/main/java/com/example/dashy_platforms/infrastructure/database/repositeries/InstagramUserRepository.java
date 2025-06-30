package com.example.dashy_platforms.infrastructure.database.repositeries;

import com.example.dashy_platforms.infrastructure.database.entities.InstagramUserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface InstagramUserRepository extends JpaRepository<InstagramUserEntity,Long> {
    InstagramUserEntity findById(long id);
    @Query("SELECT u FROM InstagramUserEntity u WHERE u.tokenExpiresAt < :expiryDate")
    List<InstagramUserEntity> findTokensExpiringBefore(LocalDateTime expiryDate);

    boolean existsByInstagramUserId(String instagramUserId);

}
